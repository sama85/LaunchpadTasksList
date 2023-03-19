package com.example.launchpadtaskslist.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.launchpadtaskslist.R
import com.example.launchpadtaskslist.adapters.DataItem
import com.example.launchpadtaskslist.adapters.StartButtonListener
import com.example.launchpadtaskslist.adapters.TasksListAdapter
import com.example.launchpadtaskslist.databinding.FragmentTasksBinding
import com.example.launchpadtaskslist.viewmodels.ApiStatus
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

        val startButtonListener = object : StartButtonListener {
            override fun onClick(position: Int) {
                viewModel.handleClick(position)
            }
        }
        adapter = TasksListAdapter(
            startButtonListener,
            viewModel.referenceTodayDate,
            viewModel.referenceTomorrowDate
        )
        binding.tasksList?.adapter = adapter

        viewModel.itemClicked.observe(viewLifecycleOwner, Observer {

            if (!binding.tasksList?.isComputingLayout!!) adapter.notifyItemChanged(it.first)
            it.second?.let {
                if (!binding.tasksList?.isComputingLayout)
                    adapter.notifyItemChanged(it)
            }
//            viewModel.handleClickDone()
        })
        viewModel.status.observe(viewLifecycleOwner, Observer {
            //HOW IS IMAGE VIEW IN BINDING NULLABLE?
            when(it){
                ApiStatus.LOADING -> {
                    binding.statusImg?.visibility = View.VISIBLE
                    binding.statusImg?.setImageResource(R.drawable.ic_profile)
                }
                ApiStatus.ERROR -> {
                    binding.statusImg?.visibility = View.VISIBLE
                    binding.statusImg?.setImageResource(R.drawable.ic_profile)
                }
                else -> binding.statusImg?.visibility = View.GONE
            }
        })

        viewModel.tasksList.observe(viewLifecycleOwner, Observer
        {
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