package com.example.thirstyquest.data

object DrinkVolumes {
    val volumeLabels = listOf("Shot (4cl)", "Verre (25cl)", "Bouteille (33cl)", "Pinte (50cl)", "Pichet (1L)")

    val volumeMultipliers = mapOf(
        4 to 0.5,     // Shot → 20%
        25 to 0.5,    // Verre → 50%
        33 to 0.66,   // Bouteille → 66%
        50 to 1.0,    // Pinte → 100%
        100 to 2.0        // Pichet → 200%
    )
}
