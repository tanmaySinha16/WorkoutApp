package com.example.runningapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runningapp.R
import com.example.runningapp.db.RunDao
import com.example.runningapp.other.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        navigateToTrackingFragmentIfNeeded(intent)

        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setupWithNavController(findViewById<FrameLayout>(R.id.navHostFragment).findNavController())
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setOnNavigationItemReselectedListener {
                // NO OPERATION
        }

        findViewById<FrameLayout>(R.id.navHostFragment).findNavController()
            .addOnDestinationChangedListener{ _,destination,_ ->
                when(destination.id) {
                    R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment ->
                        findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility=
                            View.VISIBLE
                    else -> findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility=
                            View.GONE
                }
            }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }
    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if(intent?.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT)
        {
            findViewById<View>(R.id.navHostFragment)
                .findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

}