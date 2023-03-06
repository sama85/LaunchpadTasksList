package com.example.launchpadtaskslist.network

import com.squareup.moshi.Json
import java.util.Date


data class TaskContainer(
    val result : List<Task>,
    @Json(name = "current_task_id")
    val currentTaskId : Int
)

data class Task(
    val id : Int,
    @Json(name = "current_status")
    val status : String,
    @Json(name = "task_date")
    val taskDate : Date
)
