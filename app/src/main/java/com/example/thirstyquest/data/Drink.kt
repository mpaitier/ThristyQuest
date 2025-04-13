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

val drinkList = listOf( // TODO : récupérer les vraies valeurs
    Drink("Biere", R.drawable.biere, "Boisson alcoolisé à base de malt et de houblon", 10, 70, 80),
    Drink("Vodka", R.drawable.vodka, "Une vodka premium.", 3, 9, 10),
    Drink("Coca", R.drawable.coca, "Boisson gazeuse classique.", 1, 1, 3),
    Drink("Jäger", R.drawable.yager, "Un mélange parfait d'herbes, d'épices et de notes d'agrumes.", 21, 103, 120),
    Drink("Ricard", R.drawable.ricard, "Pastis à base d'anis, de réglisse et d'herbes de Provence.", 3, 7, 10)
)