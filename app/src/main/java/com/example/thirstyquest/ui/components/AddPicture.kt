package com.example.thirstyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AddPicture() {                                                                                  // TODO : ajouter l'image enregistré dans la DB
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(140.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {

        }

        // Add picture button
        IconButton(
            onClick = { /* TODO: Add picture selection */ },
            modifier = Modifier
                .size(48.dp)
                .offset(x = 8.dp, y = 8.dp)
                .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = "Add picture",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}