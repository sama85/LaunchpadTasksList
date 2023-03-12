package com.example.launchpadtaskslist.viewmodels

import Header
import Task
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpadtaskslist.adapters.DataItem
import com.example.launchpadtaskslist.network.Network
import kotlinx.coroutines.*

class TasksViewModel : ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _tasksList = MutableLiveData<List<Task>>()
    val tasksList: LiveData<List<Task>>
        get() = _tasksList

//    private val _buttonClicked = MutableLiveData<Boolean>()
//    val buttonClicked : LiveData<Boolean>
//        get() = _buttonClicked

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getTasks()
    }

    // use network api to fetch data and initialize ui live data to be displayed
    private fun getTasks() {
        coroutineScope.launch {
            try {
                //switch coroutine context to a background thread
                withContext(Dispatchers.IO) {
                    val tasksList = Network.tasksApiService.getTasks().result
                    _status.postValue("there are ${tasksList.size} tasks received")
                    _tasksList.postValue(tasksList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _status.postValue("error retrieving the tasks")
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

            } else numOfTasks++

        }
        return itemsList
    }

    fun onStartButtonClicked() {
//        _buttonClicked.value = true
    }

}