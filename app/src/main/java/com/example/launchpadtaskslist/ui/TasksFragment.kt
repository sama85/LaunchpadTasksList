package com.example.launchpadtaskslist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.launchpadtaskslist.databinding.FragmentTasksBinding
import com.example.launchpadtaskslist.viewmodels.TasksViewModel

class TasksFragment : Fragment() {

        private val viewModel : TasksViewModel by lazy {
            ViewModelProvider(this).get(TasksViewModel::class.java)
        }
        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentTasksBinding.inflate(inflater)

        viewModel.status.observe(viewLifecycleOwner, Observer {
            //HOW IS TEXT VIEW IN BINDING NULLABLE?
            binding.statusText?.text = it
        })
        return binding.root
    }
}