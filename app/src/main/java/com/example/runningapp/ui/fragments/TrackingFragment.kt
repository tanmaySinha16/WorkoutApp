package com.example.runningapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.Services.Polyline
import com.example.runningapp.Services.TrackingService
import com.example.runningapp.db.Run
import com.example.runningapp.other.Constants
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.other.Constants.MAP_ZOOM
import com.example.runningapp.other.Constants.POLYLINE_COLOR
import com.example.runningapp.other.Constants.POLYLINE_WIDTH
import com.example.runningapp.other.TrackingUtility
import com.example.runningapp.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.round

const val CANCEL_TRACKING_DIALOG_TAG = "cancelDialog"

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? =null
    private var currentTimeInMillis = 0L

    private var menu:Menu?=null

    @set:Inject
    var weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState!=null){
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG
            )as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener {
                stopRun()
            }
        }


        view.findViewById<MapView>(R.id.mapView).onCreate(savedInstanceState)

        view.findViewById<Button>(R.id.btnToggleRun).setOnClickListener {
            toggleRun()
        }
        view.findViewById<Button>(R.id.btnFinishRun).setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }


        view.findViewById<MapView>(R.id.mapView).getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints =it
            addLatestPolyline()
            moveCameraToUser()
        })
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMillis =it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeInMillis,true)
            view?.findViewById<TextView>(R.id.tvTimer)?.text = formattedTime
        })
    }


    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible =true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }
        else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking = isTracking
        if(!isTracking && currentTimeInMillis>0L){
            view?.findViewById<Button>(R.id.btnToggleRun)?.text="Start"
            view?.findViewById<Button>(R.id.btnFinishRun)?.visibility=View.VISIBLE
        }
        else if(isTracking){
            view?.findViewById<Button>(R.id.btnToggleRun)?.text="Stop"
            menu?.getItem(0)?.isVisible =true
            view?.findViewById<Button>(R.id.btnFinishRun)?.visibility=View.GONE
        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty())
        {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }
    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.Builder()
            for(polyline in pathPoints){
                for(pos in polyline)
                {
                    bounds.include(pos)
                }
            }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                view?.findViewById<MapView>(R.id.mapView)!!.width,
                view?.findViewById<MapView>(R.id.mapView)!!.height,
                (view?.findViewById<MapView>(R.id.mapView)!!.height * 0.05f).toInt()
            )

        )

    }
    private fun endRunAndSaveToDb()
    {
        map?.snapshot { bitmap ->
            var distanceInMeters = 0
            for (polyline in pathPoints){
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeters / 1000f) / (currentTimeInMillis / 1000f / 60 / 60)*10)/ 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters/1000f) * weight ).toInt()
            val run = Run(bitmap,dateTimestamp,avgSpeed,distanceInMeters,currentTimeInMillis,caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints)
        {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currentTimeInMillis > 0L)
        {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miCancelTracking -> {showCancelTrackingDialog()}
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showCancelTrackingDialog(){
       CancelTrackingDialog().apply {
           setYesListener {
               stopRun()
           }

       }.show(parentFragmentManager,CANCEL_TRACKING_DIALOG_TAG)
    }
    private fun stopRun()
    {
        view?.findViewById<TextView>(R.id.tvTimer)?.text = "00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }


    private fun addLatestPolyline()
    {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1)
        {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action:String) =
        Intent(requireContext(),TrackingService::class.java).also {
        it.action = action
        requireContext().startService(it)
    }

    override fun onResume() {
        super.onResume()
        view?.findViewById<MapView>(R.id.mapView)?.onResume()
    }

    override fun onStart() {
        super.onStart()
        view?.findViewById<MapView>(R.id.mapView)?.onStart()
    }

    override fun onStop() {
        super.onStop()
        view?.findViewById<MapView>(R.id.mapView)?.onStop()
    }

    override fun onPause() {
        super.onPause()
        view?.findViewById<MapView>(R.id.mapView)?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        view?.findViewById<MapView>(R.id.mapView)?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.findViewById<MapView>(R.id.mapView)?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        view?.findViewById<MapView>(R.id.mapView)?.onSaveInstanceState(outState)
    }
}