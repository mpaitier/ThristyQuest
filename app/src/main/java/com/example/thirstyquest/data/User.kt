package com.example.thirstyquest.data

data class User(
    val ID: Int,
    val name: String,
    val level: Int,
    val isFriend: Boolean
)

val userList = listOf(
    User(ID = 1, name = "Alex", level = 12, isFriend = true),
    User(ID = 2, name = "Alexa", level = 8, isFriend = false),
    User(ID = 3, name = "Alexander", level = 15, isFriend = true),
    User(ID = 4, name = "Alexis", level = 5, isFriend = false),
    User(ID = 5, name = "Ben", level = 20, isFriend = true),
    User(ID = 6, name = "Benjamin", level = 3, isFriend = false),
    User(ID = 7, name = "Benedict", level = 11, isFriend = true),
    User(ID = 8, name = "Benny", level = 7, isFriend = false),
    User(ID = 9, name = "Charlie", level = 14, isFriend = true),
    User(ID = 10, name = "Charlotte", level = 9, isFriend = false),
    User(ID = 11, name = "Charlene", level = 17, isFriend = true),
    User(ID = 12, name = "Charles", level = 6, isFriend = false),
    User(ID = 13, name = "Dan", level = 19, isFriend = true),
    User(ID = 14, name = "Daniel", level = 4, isFriend = false),
    User(ID = 15, name = "Danielle", level = 13, isFriend = true),
    User(ID = 16, name = "Danny", level = 10, isFriend = false),
    User(ID = 17, name = "Eli", level = 16, isFriend = true),
    User(ID = 18, name = "Elijah", level = 5, isFriend = false),
    User(ID = 19, name = "Elisa", level = 21, isFriend = true),
    User(ID = 20, name = "Elise", level = 2, isFriend = false),
    User(ID = 21, name = "Sam", level = 18, isFriend = true),
    User(ID = 22, name = "Samuel", level = 7, isFriend = false),
    User(ID = 23, name = "Samantha", level = 22, isFriend = true),
    User(ID = 24, name = "Samson", level = 1, isFriend = false),
    User(ID = 26, name = "Lulu", level = 100, isFriend = true)
)

val members = listOf(
    User(26,"Mathias", 10, isFriend = true),
    User(12,"Romain", 38, isFriend = true),
    User(84,"Paul", 6, isFriend = true),
    User(2,"Goustan", 11, isFriend = true),
    User(1,"Dimitri", 7, isFriend = true),
    User(4,"Florent", 5, isFriend = true),
    User(18,"Dorian", 12, isFriend = true),
    User(14,"Killian", 9, isFriend = true),
    User(8,"Damien", 4, isFriend = true),
    User(74,"Mathis", 3, isFriend = true),
    User(51,"Maxime", 2, isFriend = true),
    User(42,"Vincent", 1, isFriend = true),
    User(28,"Titouan", 13, isFriend = true),
    User(47,"Antoine", 18, isFriend = true),
    User(1,"Alexandre", 100, isFriend = true)
)

val followersList = listOf(
    User(1, "Alice", 10, true),
    User(2, "Bob", 8, false),
    User(3, "Charlie", 12, true),
    User(4, "Diana", 9, false),
    User(5, "Ethan", 11, true),
    User(6, "Fiona", 6, false),
    User(7, "George", 14, true),
    User(8, "Hannah", 10, false)
)

val followingList = listOf(
    User(1, "Alice", 10, true),
    User(4, "Diana", 9, false),
    User(7, "George", 14, true)
)