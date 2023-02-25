package com.example.mylibrary.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.mylibrary.R
import com.example.mylibrary.models.BookModel

class BookDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        var bookDetailModel : BookModel? = null
        if(intent.hasExtra(MainActivity.EXTRA_BOOK_DETAILS)){
            bookDetailModel = intent.getSerializableExtra(MainActivity.EXTRA_BOOK_DETAILS) as BookModel

        }

        if(bookDetailModel != null){
            val toolbarBookDetail = findViewById<Toolbar>(R.id.toolbarBookDetails)
            setSupportActionBar(toolbarBookDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = bookDetailModel.title

            toolbarBookDetail.setNavigationOnClickListener {
                onBackPressed()
            }

            val ivBookImage = findViewById<ImageView>(R.id.iv_book_image)
            ivBookImage.setImageURI(Uri.parse(bookDetailModel.image))

            val tvDescription = findViewById<TextView>(R.id.tv_description)
            tvDescription.text = bookDetailModel.description

            val tvDate = findViewById<TextView>(R.id.tv_date)
            tvDate.text = bookDetailModel.date
        }
    }
}