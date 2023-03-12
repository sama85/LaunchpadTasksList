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
import com.example.launchpadtaskslist.databinding.TaskItemViewBinding


class TasksListAdapter : ListAdapter<Task, RecyclerView.ViewHolder>(diffCallback) {

    override fun getCurrentList(): MutableList<Task> {
        return super.getCurrentList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as TaskViewHolder).bind(item, position)
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
        if(position > 0) {
            binding.taskIdText.text = "وصل الطلب رقم #" + task.id.toString()
            binding.taskIdText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey_custom))
            binding.startBtn.visibility = View.GONE
        }
        else {
            binding.taskIdText.text = "تأكيد استلام الطلبات"
            binding.startBtn.visibility = View.VISIBLE
        }
    }

}

interface StartButtonListener {
    fun onClick(binding: TaskItemViewBinding)
}

object diffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }

}

sealed class DataItem(){

    abstract val id : Int

    class TaskItem(val task : Task) : DataItem(){
        override val id = task.id
    }

    class HeaderItem(val header : Header) : DataItem(){
        override val id = header.id
    }

}