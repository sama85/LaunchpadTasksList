package com.example.launchpadtaskslist.viewmodels

import Header
import Task
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpadtaskslist.adapters.DataItem
import com.example.launchpadtaskslist.network.Network
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class ApiStatus { LOADING, ERROR, DONE }
enum class RelativeDate { CURRENT, FUTURE }
class TasksListViewModel : ViewModel() {

    val referenceTodayDate = "2022-11-08"
    val referenceTomorrowDate = "2022-11-09"

    private val _todayDate = MutableLiveData<String>()
    val todayDate: LiveData<String>
        get() = _todayDate

    private val _tomorrowDate = MutableLiveData<String>()
    val tomorrowDate: LiveData<String>
        get() = _tomorrowDate

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _tasksList = MutableLiveData<List<Task>>()
    val tasksList: LiveData<List<Task>>
        get() = _tasksList

    val itemsList = mutableListOf<DataItem>()

    private val _itemClicked = MutableLiveData<Pair<Int, Int?>>()
    val itemClicked: LiveData<Pair<Int, Int?>>
        get() = _itemClicked

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        initializeDates()
        getTasks(RelativeDate.CURRENT)
    }

    private fun initializeDates() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        _todayDate.value = LocalDate.now().format(formatter)
        _tomorrowDate.value = LocalDate.now().plusDays(1).format(formatter)
    }

    // use network api to fetch data and initialize ui live data to be displayed
    fun getTasks(relativeDate: RelativeDate) {
        lateinit var tasksList: List<Task>
        coroutineScope.launch {
            try {
                //switch coroutine context to a background thread
                withContext(Dispatchers.IO) {
                    when(relativeDate){
                        RelativeDate.CURRENT -> {
                            _status.postValue(ApiStatus.LOADING)
                            tasksList = Network.tasksApiService.getCurrentTasks().result
                            if (tasksList.isEmpty()) _status.postValue(ApiStatus.ERROR)
                            else{
                                _status.postValue(ApiStatus.DONE)
                                _tasksList.postValue(tasksList)
                            }
                        }
                        else -> {
                            tasksList = Network.tasksApiService.getFutureTasks().result
                            _status.postValue(ApiStatus.DONE)
                            if(_tasksList.value == null) _tasksList.postValue(tasksList)
                            else _tasksList.postValue(_tasksList.value?.plus(tasksList))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _status.postValue(ApiStatus.ERROR)
            }
        }
    }


    fun addHeaders(tasksList: List<Task>): List<DataItem> {
        var headerId = 0
        var insertPos = 0
        var currentDate = tasksList[0].taskDate
        var numOfTasks = 1
        val taskItemList = tasksList.map {
            DataItem.TaskItem(it)
        }
        val itemsList: MutableList<DataItem> = taskItemList.toMutableList()

//        Log.i("TasksViewModel", (itemsList[0] as DataItem.TaskItem).task.taskDate)

        for (i in 0 until tasksList.size) {
            if (i < tasksList.size - 1 &&
                tasksList[i].taskDate != tasksList[i + 1].taskDate
            ) {
                val header = Header(headerId++, currentDate, numOfTasks)
                itemsList.add(insertPos, DataItem.HeaderItem(header))
                insertPos = i + 1 + headerId
                numOfTasks = 1
                currentDate = tasksList[i + 1].taskDate

            } else {
                numOfTasks++
            }
        }
        return itemsList
    }

    // taskslist:   d1 d1 d2 d2 d3 d3 d3
    //              h1 d1 d1 d2
    fun addHeadersAndTasksSequence(tasksList: List<Task>): List<DataItem> {

        if (tasksList.isEmpty()) return emptyList<DataItem>()

        var headerId = 0
        lateinit var currentDate: String
        var numOfTasks = 0

        var i = 0
        while (i < tasksList.size) {
            currentDate = tasksList[i].taskDate
            numOfTasks = 0
            for (j in i until tasksList.size) {
                if (tasksList[j].taskDate == currentDate) {
                    val taskItem = DataItem.TaskItem(tasksList[j])
                    taskItem.task.sequenceNum = numOfTasks++

                    //mark first item in today's tasks as active
                    /** set to todayDate for using real date / reference today date for mock testing */
                    if (tasksList[j].taskDate == referenceTodayDate && tasksList[j].sequenceNum == 0)
                        taskItem.isActive = true

                    itemsList.add(taskItem)

                } else {
                    //insert header and modify current date
                    val header = Header(headerId++, currentDate, numOfTasks)
                    itemsList.add(i + headerId - 1, DataItem.HeaderItem(header))
                    i = j - 1
                    break
                }
                if (j == tasksList.size - 1) {
                    val header = Header(headerId++, currentDate, numOfTasks)
                    itemsList.add(i + headerId - 1, DataItem.HeaderItem(header))
                    return itemsList
                }
            }
            ++i
        }
        Log.i("tasks vm", "task list size : ${tasksList.size}")
        Log.i("tasks vm", "item list size : ${itemsList.size}")

        return itemsList
    }

    fun handleClick(position: Int) {
        val currentItem = itemsList[position]
        if (currentItem is DataItem.TaskItem) {
            currentItem.isActive = false
            currentItem.task.status = "done"
            _itemClicked.value = Pair(position, null)
        }
        if (position < itemsList.size - 1) {
            val nextItem = itemsList[position + 1]
            if (nextItem is DataItem.TaskItem) {
                nextItem.isActive = true
                _itemClicked.value = _itemClicked.value?.copy(second = position + 1)
            }
        }
    }

//    fun handleClickDone(){
//        _itemClicked.value = Pair(null,null)
//    }

}