package com.example.myfirstapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.myfirstapp.databinding.ActivityExerciseBinding
import com.example.myfirstapp.databinding.DialogCustomBackConfirmationBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private  var binding : ActivityExerciseBinding? = null
    private var TAG = "exerciseActivity"
    private var restTimer : CountDownTimer? = null
    private var restProgress : Int = 0

    private var exerciseTimer : CountDownTimer? = null
    private var exerciseProgress : Int = 0
    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExerciseNumber = -1
    private var tts : TextToSpeech? = null
    private var player : MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    private var exerciseTimerDuration : Long = 30
    private var restTimerDuration : Long = 10

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityExerciseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null)
        {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogOnBack()
        }
        setupRestView()
        setupExerciseRecyclerView()
    }

    override fun onBackPressed() {
        customDialogOnBack()
    }

    private fun customDialogOnBack(){
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnDialogYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnDialogNo.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }


    private fun setupExerciseRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setupRestView(){

        try{
            val soundURI = Uri.parse("android.resource://com.example.myfirstapp/" + R.raw.press_start)

            player = MediaPlayer.create(this, soundURI)
            player?.isLooping = false
            player?.start()
        }catch(e:Exception) {
            e.printStackTrace()
        }

        binding?.flProgressBar?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.upcomingExerciseView?.visibility = View.VISIBLE
        binding?.upcomingExerciseTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE

        if(currentExerciseNumber < exerciseList!!.size - 2) {
            binding?.upcomingExerciseView?.text = exerciseList!![currentExerciseNumber + 1].getName()
        }

        if(restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        setRestProgressBar()
    }

    private fun setupExerciseView(){
        binding?.flProgressBar?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.upcomingExerciseView?.visibility = View.INVISIBLE
        binding?.upcomingExerciseTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        val x = exerciseList!!.size
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExerciseNumber].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExerciseNumber].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExerciseNumber].getName()

        setExerciseProgressBar()
    }
    private fun setRestProgressBar() {
        binding?.progressBarRest?.setProgress(restProgress)
        restTimer = object : CountDownTimer(1000 * restTimerDuration, 1000){
            override fun onTick(p0: Long) {
                restProgress +=1
                binding?.progressBarRest?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }
            override fun onFinish() {
                restProgress = 0
                currentExerciseNumber++

                exerciseList!![currentExerciseNumber].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()

    }
    private fun setExerciseProgressBar() {
        binding?.progressBarExercise?.setProgress(exerciseProgress)
        exerciseTimer = object : CountDownTimer(1000 * exerciseTimerDuration, 1000){
            override fun onTick(p0: Long) {
                exerciseProgress +=1
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }
            override fun onFinish() {

                exerciseList!![currentExerciseNumber].setIsSelected(false)
                exerciseList!![currentExerciseNumber].setIsCompleted(true)
                exerciseAdapter!!.notifyDataSetChanged()

                if(currentExerciseNumber < exerciseList!!.size - 1){
                    setupRestView()
                }else{
                    finish()
                    val intent = Intent(this@ExerciseActivity, EndActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()

    }
    private fun speakOut(text : String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts?.setLanguage(Locale.UK)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS : Missing data or language not supported")
            }
        }else{
            Log.e(TAG, "TTS : init failed")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if(restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player != null){
            player!!.stop()
        }

        binding = null
    }
}