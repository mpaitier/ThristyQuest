package com.example.thirstyquest.data

data class League(
    val leagueID: Int,
    val name: String,
    val XP: Int,
    val leagueCode: String,
    val photoUrl: String? = null
)
