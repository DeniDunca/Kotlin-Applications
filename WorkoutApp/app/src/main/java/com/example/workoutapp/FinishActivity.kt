package com.example.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.*

class FinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        val toolbarFinish = findViewById<Toolbar>(R.id.toolbarFinish)
        setSupportActionBar(toolbarFinish)
        val actionbar = supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
        }

        toolbarFinish.setNavigationOnClickListener{
            onBackPressed()
        }

        val btnFinish = findViewById<Button>(R.id.btnFinish)

        btnFinish.setOnClickListener{
            finish()
        }

        addDateToDatabase()

    }

    private fun addDateToDatabase(){
        val calendar = Calendar.getInstance()
        val dateTime = calendar.time
        Log.i("DATE:", ""+dateTime)

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)

        val dbHandler = SqliteOpenHelper(this, null)
        dbHandler.addDate(date)
        Log.i("DATE:", "Added")
    }

}