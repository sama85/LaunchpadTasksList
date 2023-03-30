package com.example.launchpadtaskslist.adapters

import Header
import Task
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.launchpadtaskslist.R
import com.example.launchpadtaskslist.databinding.HeaderItemViewBinding
import com.example.launchpadtaskslist.databinding.TaskItemViewBinding
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.FlexibleTypeDeserializer.ThrowException

enum class ViewType(val IntType: Int) {
    TASK(0),
    HEADER(1)
}

val statusTranslation = mapOf(
    "new" to "لم تبدأ",
    "confirmed" to "لم تبدأ",
    "ready_for_preparation" to "لم تبدأ",
    "under_preparation" to "لم تبدأ",
    "ready_for_delivery" to "جاهز للتوصيل",
    "on_the_way" to "جاري التوصيل",
    "delivered" to "تم التوصيل",
    "cancelled" to "تم الالغاء",
    "done" to "تم بنجاح"
)


class TasksListAdapter(
    val listener: StartButtonListener,
    val todayDate: String,
    val tomorrowDate: String
) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.TASK.IntType -> TaskViewHolder.from(parent)
            ViewType.HEADER.IntType -> HeaderViewHolder.from(parent)
            else -> throw java.lang.ClassCastException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TaskViewHolder -> {
                holder.bind(item as DataItem.TaskItem, todayDate, position, listener)
            }
            is HeaderViewHolder -> {
                val header = (item as DataItem.HeaderItem).header
                holder.bind(header, todayDate, tomorrowDate)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.TaskItem -> ViewType.TASK.IntType
            is DataItem.HeaderItem -> ViewType.HEADER.IntType
        }
    }
}


class HeaderViewHolder(val binding: HeaderItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): HeaderViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = HeaderItemViewBinding.inflate(inflater, parent, false)
            return HeaderViewHolder(binding)
        }
    }

    fun bind(header: Header, todayDate: String, tomorrowDate: String) {

        when (header.date) {
            todayDate -> {
                binding.dateText.text = "مهام اليوم"
                binding.tasksNumberText.text = "(${header.numTasks} مهام)"
            }
            tomorrowDate -> {
                binding.dateText.text = "طلبات غدا"
                binding.tasksNumberText.text = "(${header.numTasks} طلبات)"
            }
            else -> {
                binding.dateText.text = "طلبات " + header.date
                binding.tasksNumberText.text = "(${header.numTasks} طلبات)"
            }
        }
    }
}

class TaskViewHolder(val binding: TaskItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): TaskViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TaskItemViewBinding.inflate(inflater, parent, false)
            return TaskViewHolder(binding)
        }
    }

    fun bind(
        taskItem: DataItem.TaskItem,
        todayDate: String,
        position: Int,
        listener: StartButtonListener
    ) {
        val task = taskItem.task
        binding.statusText.text = statusTranslation[task.status]

        if (task.status == "done")
            displayDoneTask()

        if (task.deliveryTime != null)
            binding.deliveryTimeText.text = "الوصول" + task.deliveryTime
        else binding.deliveryTimeText.text = "------"

        // set button visibilty according to isActive property
        when (taskItem.isActive) {

            true -> displayActiveTask()
            else -> displayInactiveTask(task)
        }
        //vm handles clicked items and modify their data, then fragment notify adapter of items changed to rebind
        binding.startBtn.setOnClickListener {
            listener.onClick(position)
        }
    }

    private fun displayDoneTask() {
        binding.statusText.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.green_custom
            )
        )
        binding.statusText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_done,
            0,
            0,
            0
        )
    }

    private fun displayInactiveTask(task: Task) {
        binding.startBtn.visibility = View.GONE
        binding.taskIdText.text = "وصل الطلب رقم #" + task.id.toString()
        binding.taskIdText.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.grey_custom
            )
        )
        binding.sequenceImg.visibility = View.INVISIBLE
        binding.sequeneceText.text = task.sequenceNum.toString()
        binding.sequeneceText.visibility = View.VISIBLE
    }

    private fun displayActiveTask() {
        binding.startBtn.visibility = View.VISIBLE
        binding.taskIdText.text = "تأكيد استلام الطلبات"
        binding.taskIdText.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.black
            )
        )
        binding.sequenceImg.visibility = View.VISIBLE
        binding.sequeneceText.visibility = View.INVISIBLE
    }
}

object diffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return when {
            oldItem is DataItem.TaskItem && newItem is DataItem.TaskItem ->
                oldItem.task.id == newItem.task.id
            oldItem is DataItem.HeaderItem && newItem is DataItem.HeaderItem ->
                oldItem.header.id == newItem.header.id
            else -> false
        }

    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return when {
            oldItem is DataItem.TaskItem && newItem is DataItem.TaskItem ->
                oldItem.task == newItem.task
            oldItem is DataItem.HeaderItem && newItem is DataItem.HeaderItem ->
                oldItem.header == newItem.header
            else -> false
        }
    }

}

sealed class DataItem() {

    abstract val id: Int

    class TaskItem(val task: Task) : DataItem() {
        override val id = task.id
        var isActive: Boolean = false
    }

    class HeaderItem(val header: Header) : DataItem() {
        override val id = header.id
    }

}

interface StartButtonListener {
    //position of clicked item to be passed to vm through fragment implementing this listener
    fun onClick(position: Int)
}