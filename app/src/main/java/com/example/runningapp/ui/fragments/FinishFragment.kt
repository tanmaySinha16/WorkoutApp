package com.example.runningapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R

class FinishFragment : Fragment(R.layout.fragment_finish){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnFinish).setOnClickListener{
            findNavController().navigate(R.id.action_finishFragment_to_startFragment)
        }
    }
}