package com.example.thirstyquest.data

data class Publication(
    val ID: Int,
    val description: String,
    val user_ID: String,
    val date: String,
    val hour: String,
    val category: String,
    val price: Double,
    val photo: String,
    val points: Int
)
// TODO : delete this false history
val PublicationHist = listOf(
    Publication(1, "Pinte de bête rouge au Bistrot", "26", "12/02/2025", "19:00","Biere", 5.50, "drawable_biere", 50),
    Publication(2, "Aguardiente chez Moe's", "12", "12/02/2025", "20:00","Liqueur",6.50, "drawable_vodka", 50),
    Publication(3, "Moscow Mule chez Croguy", "84", "12/02/2025", "20:12","Moscow Mule",8.50, "drawable_vodka", 50),
    Publication(4, "Binch de malade", "2", "13/02/2025", "02:26","Biere",6.0, "drawable_biere", 50),
    Publication(5, "Ricard du midi", "3", "13/02/2025", "12:26","Ricard",11.0, "drawable_ricard", 50),
    Publication(6, "Double IPA qui arrache", "4", "13/02/2025", "16:52","Biere",5.50, "drawable_biere", 50),
    Publication(7, "Bouteille de vin en mode classe", "18", "14/02/2025", "21:30", "Vin Rouge",9.70, "drawable_ricard", 50),
    Publication(8, "Ricard pur x_x", "14", "15/02/2025", "19:15","Ricard",5.50, "drawable_ricard", 50),
    Publication(9, "La potion de Shrek", "8", "15/02/2025", "01:26","Cimetière",16.0, "drawable_vodka", 50),
    Publication(10, "Pinte à la Voie Maltée", "74", "16/02/2025", "10:28","Biere",4.50, "drawable_biere", 50)
)