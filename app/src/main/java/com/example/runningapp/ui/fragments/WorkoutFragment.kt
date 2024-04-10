package com.example.runningapp.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runningapp.R
import com.example.runningapp.adapters.ExerciseStatusAdapter
import com.example.runningapp.databinding.FragmentWorkoutBinding
import com.example.runningapp.other.Constants_workout
import com.example.runningapp.other.ExerciseModel
import java.util.Locale

class WorkoutFragment: Fragment(R.layout.fragment_workout),TextToSpeech.OnInitListener{

    private var binding : FragmentWorkoutBinding?=null
    private var restTimer: CountDownTimer? = null
    private var restProgress= 0
    private var restTimerDuration:Long = 1
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress= 0
    private var exerciseTimerDuration:Long = 1
    private var exerciseList : ArrayList<ExerciseModel>?=null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech?=null
    private var player: MediaPlayer?=null

    private var exerciseAdapter : ExerciseStatusAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view :View  , savedInstanceState: Bundle?) {

        binding=FragmentWorkoutBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

//        setSupportActionBar(binding?.toolBarExercise)
//        if (supportActionBar!=null)
//        {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        }

        exerciseList = Constants_workout.defaultExerciseList()
        tts= TextToSpeech(view.context,this)

//        binding?.toolBarExercise?.setNavigationOnClickListener {
//            customDialogForBackButton()
//        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                customDialogForBackButton()
            }
        })


        setupRestView()
        setupExerciseStatusRecyclerView()

        view.findViewById<FrameLayout>(R.id.flCancel).setOnClickListener{
            customDialogForBackButton()
        }
    }

//    override fun onBackPressed() {
//        customDialogForBackButton()
//    }
//
private fun customDialogForBackButton() {
    val customDialog = view?.context?.let { Dialog(it) }
    customDialog?.setContentView(R.layout.dialog_custom_back_confirmation)
    customDialog?.setCanceledOnTouchOutside(false)

    val btnYes = customDialog?.findViewById<Button>(R.id.btnYes)
    val btnNo = customDialog?.findViewById<Button>(R.id.btnNo)

    btnYes?.setOnClickListener {
        findNavController().navigate(R.id.action_workoutFragment_to_startFragment)
        customDialog?.dismiss()
    }

    btnNo?.setOnClickListener {
        customDialog?.dismiss()
    }

    customDialog?.show()
}


    private fun setupExerciseStatusRecyclerView(){
        view?.findViewById<RecyclerView>(R.id.rvExerciseStatus)?.layoutManager =
            LinearLayoutManager(view?.context, LinearLayoutManager.HORIZONTAL,false)


        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
//        binding?.rvExerciseStatus?.adapter=exerciseAdapter
        view?.findViewById<RecyclerView>(R.id.rvExerciseStatus)?.adapter=exerciseAdapter
    }

    private fun setupRestView(){

        try {
            val soundURI = Uri.parse(
                "android.resource://com.example.a7minuteworkout/"+ R.raw.press_start
            )

            player= MediaPlayer.create(view?.context,soundURI)
            player?.isLooping=false
            player?.start()

        }catch (e:Exception){
            e.printStackTrace()
        }

//        binding?.upComing?.visibility= View.VISIBLE
//        binding?.upComingExerciseName?.visibility= View.VISIBLE
//        binding?.flProgressBar?.visibility= View.VISIBLE
//        binding?.tvTitle?.visibility= View.VISIBLE
//        binding?.tvExercise?.visibility= View.INVISIBLE
//        binding?.flProgressBar2?.visibility= View.INVISIBLE
//        binding?.ivImage?.visibility= View.INVISIBLE

        view?.findViewById<TextView>(R.id.upComing)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.upComingExerciseName)?.visibility = View.VISIBLE
        view?.findViewById<FrameLayout>(R.id.flProgressBar)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.tvTitle)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.tvExercise)?.visibility = View.INVISIBLE
        view?.findViewById<FrameLayout>(R.id.flProgressBar2)?.visibility = View.INVISIBLE
        view?.findViewById<ImageView>(R.id.ivImage)?.visibility = View.INVISIBLE



        view?.findViewById<TextView>(R.id.upComingExerciseName)?.text = exerciseList!![currentExercisePosition+1].getName()


        if(restTimer!=null)
        {
            restTimer?.cancel()
            restProgress=0
        }

        setRestProgressBar()
    }

    private fun setupExerciseView(){
//        binding?.upComing?.visibility= View.INVISIBLE
//        binding?.upComingExerciseName?.visibility= View.INVISIBLE
//        binding?.flProgressBar?.visibility= View.INVISIBLE
//        binding?.tvTitle?.visibility= View.INVISIBLE
//        binding?.tvExercise?.visibility= View.VISIBLE
//        binding?.flProgressBar2?.visibility= View.VISIBLE
//        binding?.ivImage?.visibility= View.VISIBLE

        view?.findViewById<TextView>(R.id.upComing)?.visibility = View.INVISIBLE
        view?.findViewById<TextView>(R.id.upComingExerciseName)?.visibility = View.INVISIBLE
        view?.findViewById<FrameLayout>(R.id.flProgressBar)?.visibility = View.INVISIBLE
        view?.findViewById<TextView>(R.id.tvTitle)?.visibility = View.INVISIBLE
        view?.findViewById<TextView>(R.id.tvExercise)?.visibility = View.VISIBLE
        view?.findViewById<FrameLayout>(R.id.flProgressBar2)?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.ivImage)?.visibility = View.VISIBLE


        if(exerciseTimer!=null)
        {
            exerciseTimer?.cancel()
            exerciseProgress=0
        }

        speakText(exerciseList!![currentExercisePosition].getName())



        view?.findViewById<ImageView>(R.id.ivImage)?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        view?.findViewById<TextView>(R.id.tvExercise)?.text=exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar()
    }


    private fun setRestProgressBar(){
//        binding?.progressBar?.progress=restProgress
        view?.findViewById<ProgressBar>(R.id.progressBar)?.progress=restProgress
        restTimer = object: CountDownTimer(restTimerDuration *10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                view?.findViewById<ProgressBar>(R.id.progressBar)?.progress=10-restProgress
//                binding?.tvTimer?.text=(10-restProgress).toString()
                view?.findViewById<TextView>(R.id.tvTimer)?.text = (10-restProgress).toString()
            }


            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }

        }.start()

    }

    private fun setExerciseProgressBar(){
//        binding?.progressBar2?.progress=exerciseProgress
        view?.findViewById<ProgressBar>(R.id.progressBar2)?.progress=exerciseProgress

        exerciseTimer = object: CountDownTimer(exerciseTimerDuration*30000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                view?.findViewById<ProgressBar>(R.id.progressBar2)?.progress=30-exerciseProgress
                view?.findViewById<TextView>(R.id.tvTimer2)?.text=(30-exerciseProgress).toString()
            }


            override fun onFinish() {



                if(currentExercisePosition < exerciseList?.size!!-1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                }

                else{

                    findNavController().navigate(R.id.action_workoutFragment_to_finishFragment)
                }

            }

        }.start()

    }
    override fun onDestroy() {
        super.onDestroy()
        if(restTimer!=null) {
            restTimer?.cancel()

            restProgress = 0
        }
        if(exerciseTimer!=null)
        {
            exerciseTimer?.cancel()
            exerciseProgress=0
        }
        if(tts !=null)
        {
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player!=null)
        {
            player!!.stop()
        }

        binding=null
    }

     override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS)
        {
            val result = tts!!.setLanguage(Locale.UK)
            if(result== TextToSpeech.LANG_MISSING_DATA ||
                result== TextToSpeech.LANG_NOT_SUPPORTED){

                Log.e("TTS","the language specified is not supported!")
            }
            else{
                Log.e("TTS","Initialization failed")
            }
        }
    }
    private fun speakText(text:String)
    {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH,null,"")
    }
}