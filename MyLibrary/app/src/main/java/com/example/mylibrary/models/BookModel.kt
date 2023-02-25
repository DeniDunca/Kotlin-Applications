package com.example.mylibrary.models

import java.io.Serializable

data class BookModel(
    val id : Int,
    val title: String,
    val image: String,
    val description: String,
    val date: String
): Serializable