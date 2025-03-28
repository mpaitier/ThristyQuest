package com.example.thirstyquest.data

import com.example.thirstyquest.R

data class Publication(
    val ID: Int,
    val description: String,
    val user_ID: Int,
    val date: String,
    val hour: String,
    val category: String,
    val price: Double,
    val photo: Int,
    val points: Int
)

val PublicationHist = listOf(
    Publication(1, "Pinte de bête rouge au Bistrot", 26, "12/02/2025", "19:00","Biere", 5.50, R.drawable.biere, 50),
    Publication(2, "Aguardiente chez Moe's", 12, "12/02/2025", "20:00","Liqueur",6.50, R.drawable.vodka, 50),
    Publication(3, "Moscow Mule chez Croguy", 84, "12/02/2025", "20:12","Moscow Mule",8.50, R.drawable.vodka, 50),
    Publication(4, "Binch de malade", 2, "13/02/2025", "02:26","Biere",6.0, R.drawable.biere, 50),
    Publication(5, "Ricard du midi", 1, "13/02/2025", "12:26","Ricard",11.0, R.drawable.ricard, 50),
    Publication(6, "Double IPA qui arrache", 4, "13/02/2025", "16:52","Biere",5.50, R.drawable.biere, 50),
    Publication(7, "Bouteille de vin en mode classe", 18, "14/02/2025", "21:30", "Vin Rouge",9.70, R.drawable.ricard, 50),
    Publication(8, "Ricard pur x_x", 14, "15/02/2025", "19:15","Ricard",5.50, R.drawable.ricard, 50),
    Publication(9, "La potion de Shrek", 8, "15/02/2025", "01:26","Cimetière",16.0, R.drawable.vodka, 50),
    Publication(10, "Pinte à la Voie Maltée", 74, "16/02/2025", "10:28","Biere",4.50, R.drawable.biere, 50)
)
