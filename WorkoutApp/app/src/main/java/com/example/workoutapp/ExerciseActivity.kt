package com.example.workoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore.Audio.Media
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.LineNumberReader
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var restTimerDuration: Long = 10

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTimerDuration: Long = 30

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excercise)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        tts = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()
        setUpRestView()

        setUpExerciseStatusRecycleView()
    }

    override fun onDestroy() {
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player!= null){
            player!!.stop()
        }

        super.onDestroy()
    }

    private fun setRestProgressBar(){
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = restProgress
        restTimer = object : CountDownTimer(restTimerDuration * 1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressBar.progress = restTimerDuration.toInt() - restProgress
                val tvTimer = findViewById<TextView>(R.id.tvTimer)
                tvTimer.text = (restTimerDuration.toInt() - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setUpExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar(){
        val progressBar = findViewById<ProgressBar>(R.id.progressBarExercise)
        progressBar.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                progressBar.progress = exerciseTimerDuration.toInt() - exerciseProgress
                val tvTimer = findViewById<TextView>(R.id.tvExerciseTimer)
                tvTimer.text = (exerciseTimerDuration.toInt() - exerciseProgress).toString()
            }

            override fun onFinish() {
                if(currentExercisePosition < exerciseList?.size!! - 1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()

                    setUpRestView()
                }else{
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)

                }
            }
        }.start()
    }

    private fun setUpRestView(){

        try{
            player = MediaPlayer.create(applicationContext, R.raw.press_start)
            player!!.isLooping = false
            player!!.start()

        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }

        var llRestView = findViewById<LinearLayout>(R.id.llRestView)
        var llExerciseView = findViewById<LinearLayout>(R.id.llExerciseView)

        llRestView.visibility = View.VISIBLE
        llExerciseView.visibility = View.GONE

        if(restTimer!=null){
            restTimer!!.cancel()
            restProgress = 0
        }

        val tvUpcomingExerciseName = findViewById<TextView>(R.id.tvUpcomingExerciseName)
        tvUpcomingExerciseName.text = exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }

    private fun setUpExerciseView(){

        var llRestView = findViewById<LinearLayout>(R.id.llRestView)
        var llExerciseView = findViewById<LinearLayout>(R.id.llExerciseView)

        llRestView.visibility = View.GONE
        llExerciseView.visibility = View.VISIBLE

        if(exerciseTimer!=null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        setExerciseProgressBar()

        var ivImage = findViewById<ImageView>(R.id.ivImage)
        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        var tvExerciseName = findViewById<TextView>(R.id.tvExerciseName)
        tvExerciseName.text = exerciseList!![currentExercisePosition].getName()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "Not supported")
            }
        }else{
            Log.e("TTS", "Initialization failed")
        }
    }

    private fun speakOut(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun setUpExerciseStatusRecycleView(){
        val rvExerciseStatus = findViewById<RecyclerView>(R.id.rvExerciseStatus)
        rvExerciseStatus.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!, this)
        rvExerciseStatus.adapter = exerciseAdapter
    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
        customDialog.findViewById<Button>(R.id.tvYes).setOnClickListener {
            finish()
            customDialog.dismiss()
        }
        customDialog.findViewById<Button>(R.id.tvNo).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }
}