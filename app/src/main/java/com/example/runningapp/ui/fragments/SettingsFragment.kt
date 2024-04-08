package com.example.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.runningapp.R
import com.example.runningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.other.Constants.KEY_NAME
import com.example.runningapp.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPref()
        view.findViewById<Button>(R.id.btnApplyChanges)?.setOnClickListener {
            val success = applyChangesToSharedPref()

            if(success){
                Snackbar.make(view,"Saved Changes",Snackbar.LENGTH_LONG).show()
            }
            else{
                Snackbar.make(view,"Please fill out all the fields",Snackbar.LENGTH_LONG).show()
            }
        }
        view.findViewById<Button>(R.id.btnSettingsBMI).setOnClickListener {
            val weight = view.findViewById<EditText>(R.id.etSettingsWeight)?.text.toString().toDoubleOrNull()
            val height = view.findViewById<EditText>(R.id.etSettingsHeight)?.text.toString().toDoubleOrNull()

            if (weight != null && height != null && height > 0 && weight > 0) {
                val heightInMeters = height / 100
                val bmi = weight / (heightInMeters * heightInMeters)
                val bmiFormatted = String.format("%.2f", bmi)
                view.findViewById<Button>(R.id.btnSettingsBMI).setText("BMI = " +bmiFormatted)
            } else {
                view.findViewById<Button>(R.id.btnSettingsBMI).setText("Please provide correct data.")
            }
        }
    }

    private fun loadFieldsFromSharedPref()
    {
        val name = sharedPreferences.getString(KEY_NAME,"")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT,80f)
        view?.findViewById<EditText>(R.id.etSettingsName)?.setText(name)
        view?.findViewById<EditText>(R.id.etSettingsWeight)?.setText(weight.toString())
    }
    private fun applyChangesToSharedPref():Boolean{
        val nameText = view?.findViewById<EditText>(R.id.etSettingsName)?.text.toString()
        val weightText = view?.findViewById<EditText>(R.id.etSettingsWeight)?.text.toString()
        if(nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
     sharedPreferences.edit()
         .putString(KEY_NAME,nameText)
         .putFloat(KEY_WEIGHT,weightText.toFloat())
         .apply()

        val toolbarText = "Let's go , $nameText!"
        val tvToolbarTitle = activity?.findViewById(R.id.tvToolbarTitle) as TextView
        tvToolbarTitle.text = toolbarText
        return true

    }
}