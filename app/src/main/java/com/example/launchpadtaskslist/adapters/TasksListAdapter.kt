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

//const val referenceTodayDate = "2022-11-07"
//const val referenceTomorrowDate = "2022-11-08"

class TasksListAdapter(val todayDate: String, val tomorrowDate: String) :
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
                val task = (item as DataItem.TaskItem).task
                holder.bind(task, todayDate)
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
            setButtonListener(binding, parent)
            return TaskViewHolder(binding)
        }

        private fun setButtonListener(
            binding: TaskItemViewBinding,
            parent: ViewGroup
        ) {
            binding.startBtn.setOnClickListener {
                it.visibility = View.GONE
                binding.statusText.text = "تم بنجاح"
                binding.statusText.setTextColor(
                    ContextCompat.getColor(
                        parent.context,
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
        }
    }

    fun bind(task: Task, todayDate: String) {
        binding.statusText.text = task.status
        task.deliveryTime?.apply {
            binding.deliveryTimeText.text = "الوصول" + task.deliveryTime
        }
        if (task.taskDate != todayDate) {
            binding.taskIdText.text = "وصل الطلب رقم #" + task.id.toString()
            binding.taskIdText.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.grey_custom
                )
            )
            binding.startBtn.visibility = View.GONE
        } else {
            binding.taskIdText.text = "تأكيد استلام الطلبات"
            if(task.sequenceNum == 0) binding.startBtn.visibility = View.VISIBLE
            else  binding.startBtn.visibility = View.GONE
        }
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
    }

    class HeaderItem(val header: Header) : DataItem() {
        override val id = header.id
    }

}