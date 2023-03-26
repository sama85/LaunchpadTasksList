package com.example.launchpadtaskslist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.launchpadtaskslist.databinding.FragmentWalletMainBinding

class WalletMainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWalletMainBinding.inflate(inflater)
        return binding.root
    }
}