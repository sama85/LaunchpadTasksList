package com.example.launchpadtaskslist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.launchpadtaskslist.R
import com.example.launchpadtaskslist.adapters.StartButtonListener
import com.example.launchpadtaskslist.adapters.TasksListAdapter
import com.example.launchpadtaskslist.databinding.FragmentTasksListBinding
import com.example.launchpadtaskslist.viewmodels.ApiStatus
import com.example.launchpadtaskslist.viewmodels.RelativeDate
import com.example.launchpadtaskslist.viewmodels.TasksListViewModel

class TasksListFragment : Fragment() {

    private val viewModel: TasksListViewModel by lazy {
        ViewModelProvider(this).get(TasksListViewModel::class.java)
    }

    private lateinit var adapter: TasksListAdapter
    lateinit var binding: FragmentTasksListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTasksListBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Toast.makeText(context, "end of list reached", Toast.LENGTH_LONG).show()
                    viewModel.getTasks(RelativeDate.FUTURE)
                }
            }
        })

        viewModel.itemClicked.observe(viewLifecycleOwner, Observer {

            if (!binding.tasksList?.isComputingLayout!!) adapter.notifyItemChanged(it.first)
            it.second?.let {
                if (!binding.tasksList?.isComputingLayout!!)
                    adapter.notifyItemChanged(it)
            }
        })
        viewModel.status.observe(viewLifecycleOwner, Observer {
            when (it) {
                ApiStatus.ERROR -> displayErrorStatus()
                ApiStatus.LOADING -> displayLoadingStatus()
                else -> displayList()
            }
        })

        viewModel.currentTasksList.observe(viewLifecycleOwner, Observer
        {
            it?.apply {
                viewModel.addHeadersAndTasksSequence(it)
            }
        })

        viewModel.futureTasksList.observe(viewLifecycleOwner, Observer
        {
            it?.apply {
                viewModel.addHeadersAndTasksSequence(it)
            }
        })

        viewModel.itemsList.observe(viewLifecycleOwner, Observer {
            it?.apply {
                adapter.submitList(it)
            }
        })
    }

    private fun displayList() {
        binding.statusCard?.visibility = View.GONE
    }

    private fun displayLoadingStatus() {
        binding.statusCard?.visibility = View.VISIBLE
        binding.statusCard?.setBackgroundColor(resources.getColor(R.color.light_grey))
        binding.statusImg?.setImageResource(R.drawable.ic_loading)
        binding.statusText1?.text = "انتظر، جاري تعيين الطلبات عليك"
        binding.statusText2?.text = "الرجاء تغيير حالتك اذا كنت غير متاح للعمل"
    }

    private fun displayErrorStatus() {
        binding.statusCard?.visibility = View.VISIBLE
        binding.statusCard?.setBackgroundColor(resources.getColor(R.color.light_red))
        binding.statusImg?.setImageResource(R.drawable.ic_error)
        binding.statusText1?.text = "عذرا حدث خطأً ما"
        binding.statusText2?.text = "برجاء اعادة المحاوله مرة اخرى"
    }
}