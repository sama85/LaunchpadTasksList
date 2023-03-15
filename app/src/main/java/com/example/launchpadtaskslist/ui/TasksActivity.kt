package com.example.launchpadtaskslist.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.launchpadtaskslist.R
import com.example.launchpadtaskslist.databinding.ActivityMainBinding

class TasksActivity: AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.tasksFragment -> loadFragment(TasksFragment())
                R.id.accountFragment -> loadFragment(AccountFragment())
                R.id.walletFragment -> loadFragment(WalletFragment())
            }

            true
        }
    }

    fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment,fragment)
        transaction.commit()
    }
}