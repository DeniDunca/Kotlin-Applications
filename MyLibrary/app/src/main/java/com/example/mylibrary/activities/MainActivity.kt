package com.example.mylibrary.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mylibrary.R
import com.example.mylibrary.adapters.BooksAdapter
import com.example.mylibrary.database.DatabaseHandler
import com.example.mylibrary.models.BookModel
import com.example.mylibrary.utils.SwipeToDeleteCallback
import com.example.mylibrary.utils.SwipeToEditCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabAddBook = findViewById<FloatingActionButton>(R.id.fabAddBook)
        fabAddBook.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivityForResult(intent, ADD_BOOK_ACTIVITY_REQUEST)
        }

        getBooksListFromLocalDB()
    }

    private fun setUpBooksRecyclerView(bookList: ArrayList<BookModel>){
        val rvBookList = findViewById<RecyclerView>(R.id.rv_books_list)
        rvBookList.layoutManager = LinearLayoutManager(this)
        rvBookList.setHasFixedSize(true)

        val booksAdapter = BooksAdapter(this, bookList)
        rvBookList.adapter = booksAdapter

        booksAdapter.setOnClickListener(object: BooksAdapter.OnClickListener{
            override fun onClick(position: Int, model: BookModel) {
                val intent = Intent(this@MainActivity, BookDetailActivity::class.java)

                intent.putExtra(EXTRA_BOOK_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val rv_book_list = findViewById<RecyclerView>(R.id.rv_books_list)
                val adapter = rv_book_list.adapter as BooksAdapter
                adapter.notifyEditItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_BOOK_ACTIVITY_REQUEST
                )
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rvBookList)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val rv_book_list = findViewById<RecyclerView>(R.id.rv_books_list)
                val adapter = rv_book_list.adapter as BooksAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getBooksListFromLocalDB()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rvBookList)
    }

    private fun getBooksListFromLocalDB() {
        val rvBookList = findViewById<RecyclerView>(R.id.rv_books_list)
        val tvNoBooks = findViewById<TextView>(R.id.tv_no_books)
        val dbHandler = DatabaseHandler(this)

        val getBookList = dbHandler.getBooksList()

        if (getBookList.size > 0) {
            for (i in getBookList) {
                rvBookList.visibility = View.VISIBLE
                tvNoBooks.visibility = View.GONE
                setUpBooksRecyclerView(getBookList)
            }
        }else{
            rvBookList.visibility = View.GONE
            tvNoBooks.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_BOOK_ACTIVITY_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                getBooksListFromLocalDB()
            }else{
                Log.e("Activity", "Cancelled or back pressed")
            }
        }
    }

    companion object{
        var ADD_BOOK_ACTIVITY_REQUEST = 1
        var EXTRA_BOOK_DETAILS = "extra_book_details"
    }
}