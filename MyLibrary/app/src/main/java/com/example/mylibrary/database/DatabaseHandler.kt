package com.example.mylibrary.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.mylibrary.models.BookModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Database version
        private const val DATABASE_NAME = "BooksDatabase" // Database name
        private const val TABLE_BOOK = "BooksTable" // Table Name

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_BOOK_TABLE = ("CREATE TABLE " + TABLE_BOOK + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT)")
        db?.execSQL(CREATE_BOOK_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_BOOK")
        onCreate(db)
    }


    fun addBook(book: BookModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, book.title)
        contentValues.put(KEY_IMAGE, book.image)
        contentValues.put(KEY_DESCRIPTION, book.description)
        contentValues.put(KEY_DATE, book.date)

        // Inserting Row
        val result = db.insert(TABLE_BOOK, null, contentValues)

        db.close() // Closing database connection
        return result
    }

    fun updateBook(book: BookModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, book.title)
        contentValues.put(KEY_IMAGE, book.image)
        contentValues.put(KEY_DESCRIPTION, book.description)
        contentValues.put(KEY_DATE, book.date)

        val success = db.update(
            TABLE_BOOK,
            contentValues,
            KEY_ID + "=" + book.id,
            null
        )

        db.close() // Closing database connection
        return success
    }

    fun deleteBook(book: BookModel): Int{
        val db = this.writableDatabase
        val success = db.delete(
            TABLE_BOOK,
            KEY_ID + "=" + book.id,
            null
        )
        db.close()
        return success
    }


    fun getBooksList(): ArrayList<BookModel> {

        val happyPlaceList: ArrayList<BookModel> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_BOOK"

        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val book = BookModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID) + 0),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE) + 0),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE) + 0),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION) + 0),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE) + 0),
                        )
                    happyPlaceList.add(book)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return happyPlaceList
    }
}