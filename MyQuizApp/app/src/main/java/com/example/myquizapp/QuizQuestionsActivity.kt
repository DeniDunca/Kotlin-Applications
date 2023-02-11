package com.example.myquizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat

class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    private var mCurrentPosition : Int = 1
    private var mQuestionList : ArrayList<Question>? = null
    private var mSelectedOptionPosition : Int = 0
    private var mCorrectAnswers = 0
    private var mUserName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        mUserName = intent.getStringExtra(Constants.USER_NAME)

        mQuestionList = Constants.getQuestions()

        setQuestion()

        val tvOptionOne = findViewById<TextView>(R.id.tvOptionOne)
        val tvOptionTwo = findViewById<TextView>(R.id.tvOptionTwo)
        val tvOptionThree = findViewById<TextView>(R.id.tvOptionThree)
        val tvOptionFour = findViewById<TextView>(R.id.tvOptionFour)

        tvOptionOne.setOnClickListener(this)
        tvOptionTwo.setOnClickListener(this)
        tvOptionThree.setOnClickListener(this)
        tvOptionFour.setOnClickListener(this)

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        btnSubmit.setOnClickListener(this)
    }


    private fun setQuestion(){

        val question = mQuestionList!!.get(mCurrentPosition - 1)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        defaultOptionsView()

        if(mCurrentPosition == mQuestionList!!.size){
            btnSubmit.text = "FINISH"
        }else{
            btnSubmit.text = "SUBMIT"
        }

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvProgress = findViewById<TextView>(R.id.tvProgress)
        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        val ivImage = findViewById<ImageView>(R.id.ivImage)
        val tvOptionOne = findViewById<TextView>(R.id.tvOptionOne)
        val tvOptionTwo = findViewById<TextView>(R.id.tvOptionTwo)
        val tvOptionThree = findViewById<TextView>(R.id.tvOptionThree)
        val tvOptionFour = findViewById<TextView>(R.id.tvOptionFour)

        progressBar.progress = mCurrentPosition
        tvProgress.text = "$mCurrentPosition" + "/" + progressBar.max

        tvQuestion.text = question!!.question

        ivImage.setImageResource(question.image)

        tvOptionOne.text = question.optionOne
        tvOptionTwo.text = question.optionTwo
        tvOptionThree.text = question.optionThree
        tvOptionFour.text = question.optionFour
    }

    private fun defaultOptionsView(){
        val options = ArrayList<TextView>()
        val tvOptionOne = findViewById<TextView>(R.id.tvOptionOne)
        val tvOptionTwo = findViewById<TextView>(R.id.tvOptionTwo)
        val tvOptionThree = findViewById<TextView>(R.id.tvOptionThree)
        val tvOptionFour = findViewById<TextView>(R.id.tvOptionFour)
        options.add(0, tvOptionOne)
        options.add(1, tvOptionTwo)
        options.add(2, tvOptionThree)
        options.add(3, tvOptionFour)

        for (option in options){
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNumber: Int){
        defaultOptionsView()
        mSelectedOptionPosition = selectedOptionNumber
        tv.setTextColor(Color.parseColor("#363A46"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    private fun answerView(answer : Int, drawableView: Int){
        val tvOptionOne = findViewById<TextView>(R.id.tvOptionOne)
        val tvOptionTwo = findViewById<TextView>(R.id.tvOptionTwo)
        val tvOptionThree = findViewById<TextView>(R.id.tvOptionThree)
        val tvOptionFour = findViewById<TextView>(R.id.tvOptionFour)

        when(answer){
            1->{
                tvOptionOne.background = ContextCompat.getDrawable(this, drawableView)
            }
            2->{
                tvOptionTwo.background = ContextCompat.getDrawable(this, drawableView)
            }
            3->{
                tvOptionThree.background = ContextCompat.getDrawable(this, drawableView)
            }
            4->{
                tvOptionFour.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }

    override fun onClick(v: View?) {
        val tvOptionOne = findViewById<TextView>(R.id.tvOptionOne)
        val tvOptionTwo = findViewById<TextView>(R.id.tvOptionTwo)
        val tvOptionThree = findViewById<TextView>(R.id.tvOptionThree)
        val tvOptionFour = findViewById<TextView>(R.id.tvOptionFour)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
       when(v?.id){
           R.id.tvOptionOne->{
               selectedOptionView(tvOptionOne,1)
           }
           R.id.tvOptionTwo->{
               selectedOptionView(tvOptionTwo,2)
           }
           R.id.tvOptionThree->{
               selectedOptionView(tvOptionThree,3)
           }
           R.id.tvOptionFour->{
               selectedOptionView(tvOptionFour,4)
           }
           R.id.btnSubmit->{
                if(mSelectedOptionPosition == 0){
                    mCurrentPosition ++

                    when{
                        mCurrentPosition <= mQuestionList!!.size ->{
                            setQuestion()
                        }else ->{
                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionList!!.size)
                            startActivity(intent)
                            finish()
                        }
                    }
                }else{
                    val question = mQuestionList?.get(mCurrentPosition - 1)
                    if(question!!.correctAnswer != mSelectedOptionPosition)
                    {
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                    }else{
                        mCorrectAnswers++
                    }
                    answerView(question.correctAnswer, R.drawable.correct_option_border_bg)

                    if(mCurrentPosition == mQuestionList!!.size){
                        btnSubmit.text = "FINISH"
                    }else{
                        btnSubmit.text = "NEXT QUESTION"
                    }
                    mSelectedOptionPosition = 0
                }
           }
       }
    }
}