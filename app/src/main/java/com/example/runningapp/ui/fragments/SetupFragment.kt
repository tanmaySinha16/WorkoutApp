package com.example.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.room.Insert
import com.example.runningapp.R
import com.example.runningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.other.Constants.KEY_NAME
import com.example.runningapp.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref : SharedPreferences

    @set:Inject
    var isFirstAppOpen = true


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstAppOpen)
        {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }


        view.findViewById<TextView>(R.id.tvContinue).setOnClickListener{
            val success = writePersonalDataToSharedPref()
            if(success){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }
            else{
                Snackbar.make(requireView(),"Please enter all the fields",Snackbar.LENGTH_SHORT).show()
            }

        }

        view.findViewById<Button>(R.id.btnBMI).setOnClickListener {
            val weight = view.findViewById<EditText>(R.id.etWeight)?.text.toString().toDoubleOrNull()
            val height = view.findViewById<EditText>(R.id.etHeight)?.text.toString().toDoubleOrNull()

            if (weight != null && height != null && height > 0 && weight > 0) {
                val heightInMeters = height / 100
                val bmi = weight / (heightInMeters * heightInMeters)
                val bmiFormatted = String.format("%.2f", bmi)
                view.findViewById<Button>(R.id.btnBMI).setText("BMI = " +bmiFormatted)
            } else {
                view.findViewById<Button>(R.id.btnBMI).setText("Please provide correct data.")
            }
        }

    }

    private fun writePersonalDataToSharedPref():Boolean {
        val name = view?.findViewById<EditText>(R.id.etName)?.text.toString()
        val weight = view?.findViewById<EditText>(R.id.etWeight)?.text.toString()


        if(name.isEmpty() || weight.isEmpty())
        {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE,false)
            .apply()

        val toolbarText = "Let's go , $name!"
        val tvToolbarTitle = activity?.findViewById(R.id.tvToolbarTitle) as TextView

        tvToolbarTitle.text = toolbarText
        return true
    }
}