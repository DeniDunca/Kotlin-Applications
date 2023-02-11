package com.example.myquizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val username = intent.getStringExtra(Constants.USER_NAME)
        val tvName = findViewById<TextView>(R.id.tvName)
        tvName.text = username

        val totalQuestions = intent.getIntExtra(Constants.TOTAL_QUESTIONS, 0)
        val correctAnswers = intent.getIntExtra(Constants.CORRECT_ANSWERS, 0)
        val tvScore = findViewById<TextView>(R.id.tvScore)
        tvScore.text = "Your Score is $correctAnswers out of $totalQuestions"

        val btnFinish = findViewById<Button>(R.id.btnFinish)
        btnFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}