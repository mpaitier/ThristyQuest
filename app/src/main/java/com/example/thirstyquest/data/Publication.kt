package com.example.thirstyquest.data

data class Publication(
    val ID: String,
    val description: String,
    val user_ID: String,
    val date: String,
    val hour: String,
    val category: String,
    val price: Double,
    val photo: String,
    val points: Int
)