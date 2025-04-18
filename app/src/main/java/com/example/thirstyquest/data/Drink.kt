package com.example.thirstyquest.data

import com.example.thirstyquest.R

data class Drink(
    val name: String,
    val imageRes: Int,
    val description: String,
    val level: Int,
    val points: Int,
    val nextLevelPoints: Int
)