import com.squareup.moshi.Json
import java.util.*

data class TaskContainer(
    val result : List<Task>,
    @Json(name = "current_task_id")
    val currentTaskId : Int?
)

data class Task(
    val id : Int,
    @Json(name = "current_status")
    val status : String,
    @Json(name = "task_date")
    val taskDate : String,
    @Json(name = "estimated_delivery_time")
    val deliveryTime : String?,
    @Json(name = "sequence")
    var sequenceNum : Int?
)

data class Header(
    val id : Int,
    val date : String,
    val numTasks : Int
)

