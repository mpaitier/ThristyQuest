package com.example.thirstyquest.data

data class User(
    val ID: String,
    val name: String,
    val XP: Int,
    val isFriend: Boolean
)

val userList = listOf(
    User(ID = "1", name = "Alex", XP = 12, isFriend = true),
    User(ID = "2", name = "Alexa", XP = 8, isFriend = false),
    User(ID = "3", name = "Alexander", XP = 15, isFriend = true),
    User(ID = "4", name = "Alexis", XP = 5, isFriend = false),
    User(ID = "5", name = "Ben", XP = 20, isFriend = true),
    User(ID = "6", name = "Benjamin", XP = 3, isFriend = false),
    User(ID = "7", name = "Benedict", XP = 11, isFriend = true),
    User(ID = "8", name = "Benny", XP = 7, isFriend = false),
    User(ID = "9", name = "Charlie", XP = 14, isFriend = true),
    User(ID = "10", name = "Charlotte", XP = 9, isFriend = false),
    User(ID = "11", name = "Charlene", XP = 17, isFriend = true),
    User(ID = "12", name = "Charles", XP = 6, isFriend = false),
    User(ID = "13", name = "Dan", XP = 19, isFriend = true),
    User(ID = "14", name = "Daniel", XP = 4, isFriend = false),
    User(ID = "15", name = "Danielle", XP = 13, isFriend = true),
    User(ID = "16", name = "Danny", XP = 10, isFriend = false),
    User(ID = "17", name = "Eli", XP = 16, isFriend = true),
    User(ID = "18", name = "Elijah", XP = 5, isFriend = false),
    User(ID = "19", name = "Elisa", XP = 21, isFriend = true),
    User(ID = "20", name = "Elise", XP = 2, isFriend = false),
    User(ID = "21", name = "Sam", XP = 18, isFriend = true),
    User(ID = "22", name = "Samuel", XP = 7, isFriend = false),
    User(ID = "23", name = "Samantha", XP = 22, isFriend = true),
    User(ID = "24", name = "Samson", XP = 1, isFriend = false),
    User(ID = "25", name = "Lulu", XP = 100, isFriend = true)
)

val members = listOf(
    User("26","Mathias", 10, isFriend = true),
    User("12","Romain", 38, isFriend = true),
    User("84","Paul", 6, isFriend = true),
    User("2","Goustan", 11, isFriend = true),
    User("7","Dimitri", 7, isFriend = true),
    User("4","Florent", 5, isFriend = true),
    User("18","Dorian", 12, isFriend = true),
    User("14","Killian", 9, isFriend = true),
    User("8","Damien", 4, isFriend = true),
    User("74","Mathis", 3, isFriend = true),
    User("51","Maxime", 2, isFriend = true),
    User("42","Vincent", 1, isFriend = true),
    User("28","Titouan", 13, isFriend = true),
    User("47","Antoine", 18, isFriend = true),
    User("1","Alexandre", 100, isFriend = true)
)

val followersList = listOf(
    User("1", "Alice", 10, true),
    User("2", "Bob", 8, false),
    User("3", "Charlie", 12, true),
    User("4", "Diana", 9, false),
    User("5", "Ethan", 11, true),
    User("6", "Fiona", 6, false),
    User("7", "George", 14, true),
    User("8", "Hannah", 10, false)
)

val followingList = listOf(
    User("1", "Alice", 10, true),
    User("4", "Diana", 9, false),
    User("99", "George", 14, true)
)