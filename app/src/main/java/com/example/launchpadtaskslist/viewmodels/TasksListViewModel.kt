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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

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

    private val _currentTasksList = MutableLiveData<List<Task>>()
    val currentTasksList: LiveData<List<Task>>
        get() = _currentTasksList

    private val _futureTasksList = MutableLiveData<List<Task>>()
    val futureTasksList: LiveData<List<Task>>
        get() = _futureTasksList

    private val _itemsList = MutableLiveData<List<DataItem>>()
    val itemsList: LiveData<List<DataItem>>
        get() = _itemsList

    private val _itemClicked = MutableLiveData<Pair<Int, Int?>>()
    val itemClicked: LiveData<Pair<Int, Int?>>
        get() = _itemClicked

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private lateinit var latestTaskDate: String
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        initializeDates()
        getTasks(RelativeDate.CURRENT)
    }

    private fun initializeDates() {
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
                    when (relativeDate) {
                        RelativeDate.CURRENT -> {
                            _status.postValue(ApiStatus.LOADING)
                            tasksList = Network.tasksApiService.getCurrentTasks().result
                            if (tasksList.isEmpty()) _status.postValue(ApiStatus.ERROR)
                            else {
                                _status.postValue(ApiStatus.DONE)
                                _currentTasksList.postValue(tasksList)
                            }
                        }
                        else -> {
                            val relativeDate = calculateRelativeDate()
                            tasksList = Network.tasksApiService.getFutureTasks(relativeDate).result
                            _status.postValue(ApiStatus.DONE)
                            _futureTasksList.postValue(tasksList)
                        }
                    }

                    if (tasksList.isNotEmpty())
                        latestTaskDate = tasksList.last().taskDate

                }
            } catch (e: Exception) {
                e.printStackTrace()
                _status.postValue(ApiStatus.ERROR)
            }
        }
    }

    fun calculateRelativeDate(): Long {

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val todayDate = sdf.parse(_todayDate.value)
        val lastTaskDate = sdf.parse(latestTaskDate)

        val diff: Long = lastTaskDate.getTime() - todayDate.getTime()
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun addHeadersAndTasksSequence(tasksList: List<Task>) {
        val itemsList = mutableListOf<DataItem>()

        if (tasksList.isEmpty()) return

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
                    i = j
                }
            }
            ++i
        }

        //update items list
        if (_itemsList.value == null) _itemsList.value = itemsList
        else _itemsList.value = _itemsList.value?.plus(itemsList)
    }

    fun handleClick(position: Int) {
        val itemsList = _itemsList.value
        itemsList?.let {
            val currentItem = itemsList.get(position)
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
    }
}