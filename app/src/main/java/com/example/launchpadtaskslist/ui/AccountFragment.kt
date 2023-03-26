package com.example.launchpadtaskslist.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.launchpadtaskslist.R
import com.example.launchpadtaskslist.databinding.FragmentAccountBinding
import com.example.launchpadtaskslist.databinding.FragmentWalletBinding

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentAccountBinding.inflate(inflater)
        return binding.root
    }
}