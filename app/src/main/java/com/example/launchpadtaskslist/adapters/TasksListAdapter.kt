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

enum class ViewType(val IntType : Int){
    TASK(0),
    HEADER(1)
}
class TasksListAdapter : ListAdapter<DataItem, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ViewType.TASK.IntType -> TaskViewHolder.from(parent)
            ViewType.HEADER.IntType -> HeaderViewHolder.from(parent)
            else -> throw java.lang.ClassCastException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(holder){
            is TaskViewHolder -> {
                val task = (item as DataItem.TaskItem).task
                holder.bind(task, position)
            }
            is HeaderViewHolder -> {
                val header = (item as DataItem.HeaderItem).header
                holder.bind(header)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem.TaskItem -> ViewType.TASK.IntType
            is DataItem.HeaderItem -> ViewType.HEADER.IntType
        }
    }
}


class HeaderViewHolder(val binding : HeaderItemViewBinding) : RecyclerView.ViewHolder(binding.root){
    companion object{
        fun from(parent : ViewGroup) : HeaderViewHolder{
            val inflater = LayoutInflater.from(parent.context)
            val binding = HeaderItemViewBinding.inflate(inflater, parent, false)
            return HeaderViewHolder(binding)
        }
    }

    fun bind(header: Header){
        binding.dateText.text = header.date
        binding.tasksNumberText.text = header.numTasks.toString()
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

    fun bind(task: Task, position: Int) {
        binding.statusText.text = task.status
        task.deliveryTime?.apply {
            binding.deliveryTimeText.text = "الوصول" + task.deliveryTime
        }
        if (position > 0) {
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
            binding.startBtn.visibility = View.VISIBLE
        }
    }

}

interface StartButtonListener {
    fun onClick(binding: TaskItemViewBinding)
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