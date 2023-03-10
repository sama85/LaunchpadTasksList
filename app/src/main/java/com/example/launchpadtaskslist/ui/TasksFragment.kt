package com.example.launchpadtaskslist.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.launchpadtaskslist.adapters.DataItem
import com.example.launchpadtaskslist.adapters.TasksListAdapter
import com.example.launchpadtaskslist.databinding.FragmentTasksBinding
import com.example.launchpadtaskslist.viewmodels.TasksViewModel

class TasksFragment : Fragment() {

    private val viewModel: TasksViewModel by lazy {
        ViewModelProvider(this).get(TasksViewModel::class.java)
    }

    private lateinit var adapter: TasksListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentTasksBinding.inflate(inflater)

//        viewModel.todayDate.observe(viewLifecycleOwner, Observer {
//            adapter = TasksListAdapter(it, viewModel.tomorrowDate.value!!)
//            binding.tasksList?.adapter = adapter
//        })

        adapter = TasksListAdapter("2022-11-08", "2022-11-09")
        binding.tasksList?.adapter = adapter

//        viewModel.status.observe(viewLifecycleOwner, Observer {
//            //HOW IS TEXT VIEW IN BINDING NULLABLE?
//            binding.statusText?.text = it
//        })

        viewModel.tasksList.observe(viewLifecycleOwner, Observer {
            it?.apply {
                val itemsList = viewModel.addHeadersAndTasksSequence(it)
                Log.i("TasksFragment", (itemsList[0] is DataItem.HeaderItem).toString())
                Log.i("TasksFragment", (itemsList[1] is DataItem.HeaderItem).toString())
                Log.i("TasksFragment", (itemsList[2] is DataItem.HeaderItem).toString())
                adapter.submitList(itemsList)
            }
        })
        return binding.root
    }
}