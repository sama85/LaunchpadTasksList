package com.example.launchpadtaskslist.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.launchpadtaskslist.R
import com.example.launchpadtaskslist.adapters.DataItem
import com.example.launchpadtaskslist.adapters.StartButtonListener
import com.example.launchpadtaskslist.adapters.TasksListAdapter
import com.example.launchpadtaskslist.databinding.FragmentTasksListBinding
import com.example.launchpadtaskslist.viewmodels.ApiStatus
import com.example.launchpadtaskslist.viewmodels.TasksViewModel

class TasksListFragment : Fragment() {

    private val viewModel: TasksViewModel by lazy {
        ViewModelProvider(this).get(TasksViewModel::class.java)
    }

    private lateinit var adapter: TasksListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentTasksListBinding.inflate(inflater)

        val startButtonListener = object : StartButtonListener {
            override fun onClick(position: Int) {
                viewModel.handleClick(position)
            }
        }
        /**     FOR SIMULATING WITH REAL TODAY DATE       */
//        viewModel.todayDate.observe(viewLifecycleOwner, Observer {
//            adapter = TasksListAdapter(startButtonListener, it,
//                        viewModel.tomorrowDate.value!!)
//            binding.tasksList?.adapter = adapter
//           // Log.i("Tasks Fragment", "today date $it, tomorrow date: ${viewModel.tomorrowDate.value!!}")
//        })

        adapter = TasksListAdapter(
            startButtonListener,
            viewModel.referenceTodayDate,
            viewModel.referenceTomorrowDate
        )
        binding.tasksList?.adapter = adapter

        binding.tasksList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE){
                    Toast.makeText(context, "end of list reached", Toast.LENGTH_LONG).show()
                }
            }
        })

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
                ApiStatus.ERROR -> {
                    binding.statusCard?.visibility = View.VISIBLE
                    binding.statusCard?.setBackgroundColor(resources.getColor(R.color.light_red))
                    binding.statusImg?.setImageResource(R.drawable.ic_error)
                    binding.statusText1?.text = "عذرا حدث خطأً ما"
                    binding.statusText2?.text = "برجاء اعادة المحاوله مرة اخرى"
                }
                ApiStatus.LOADING -> {
                    binding.statusCard?.visibility = View.VISIBLE
                    binding.statusCard?.setBackgroundColor(resources.getColor(R.color.light_grey))
                    binding.statusImg?.setImageResource(R.drawable.ic_loading)
                    binding.statusText1?.text = "انتظر، جاري تعيين الطلبات عليك"
                    binding.statusText2?.text = "الرجاء تغيير حالتك اذا كنت غير متاح للعمل"
                }
                else -> binding.statusCard?.visibility = View.GONE
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