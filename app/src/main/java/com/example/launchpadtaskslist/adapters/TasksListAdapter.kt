package com.example.launchpadtaskslist.adapters

import Task
import android.graphics.Color
import android.opengl.Visibility
import android.text.Layout
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as TaskViewHolder).bind(item)
    }
}


class TaskViewHolder(val binding: TaskItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): TaskViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TaskItemViewBinding.inflate(inflater, parent, false)
            binding.startBtn.setOnClickListener {
                it.visibility = View.GONE
                binding.statusText.text = "تم بنحاح"
                binding.statusText.setTextColor(ContextCompat.getColor(parent.context, R.color.green_custom))
                binding.statusText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_done, 0, 0, 0)
            }
            return TaskViewHolder(binding)
        }
    }

    fun bind(task: Task) {
        binding.statusText.text = task.status
        task.deliveryTime?.apply {
            binding.deliveryTimeText.text = task.deliveryTime
        }
        binding.taskIdText.text = task.id.toString()
    }

}

interface StartButtonListener{
    fun onClick(button : View)
}

object diffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }

}