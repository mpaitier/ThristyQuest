package com.example.thirstyquest.ui.dialog

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

import androidx.datastore.preferences.core.edit

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.thirstyquest.R
import com.example.thirstyquest.ui.NotificationPreferences.getNotificationsEnabled

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        "default_channel",
        "Notifications Générales",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Notifications générales de l'application"
    }

    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

class NotificationWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        val notificationsEnabled = runBlocking {
            getNotificationsEnabled(applicationContext)
        }

        if (!notificationsEnabled) {
            Log.w("Notif", "Notifications désactivées : switch off ou permission refusée")
            return Result.success()
        }

        val notification = NotificationCompat.Builder(applicationContext, "default_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Rappel")
            .setContentText("Pense à boire de l'eau !")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext).notify(1, notification)
        } catch (e: SecurityException) {
            Log.e("Notif", "Permission POST_NOTIFICATIONS manquante au moment de l'envoi", e)
        }

        return Result.success()
    }
}
