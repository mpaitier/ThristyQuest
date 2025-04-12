package com.example.thirstyquest.data

data class Category(
    val name: String,
    val level: Int = 0,
    val points: Double = 0.0,
    val total: Double = 0.0,
)