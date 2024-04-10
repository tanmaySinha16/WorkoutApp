package com.example.runningapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentStartBinding

class StartFragment: Fragment(R.layout.fragment_start) {
    private var binding : FragmentStartBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentStartBinding.inflate(layoutInflater)
        super.onViewCreated(view, savedInstanceState)

        binding?.flStart?.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_workoutFragment)
        }
        view.findViewById<FrameLayout>(R.id.flStart).setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_workoutFragment)
        }
    }
}