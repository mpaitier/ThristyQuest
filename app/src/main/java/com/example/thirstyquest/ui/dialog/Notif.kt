package com.example.thirstyquest.ui.dialog

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.thirstyquest.R

// --------------------- Use code below to send a notification ---------------------
/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
    ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
    ) != PackageManager.PERMISSION_GRANTED
) {
    activity?.let {
        ActivityCompat.requestPermissions(
            it,
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            1001
        )
    }
} else {
    trySendNotification(context, "Titre", "tu viens de rentrer dans une league")
}
*/

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
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notification = NotificationCompat.Builder(applicationContext, "default_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Rappel")
                .setContentText("Pense à boire de l'eau !")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext).notify(1, notification)

        } else {
            Log.w("Notif", "Permission POST_NOTIFICATIONS refusée ou non demandée")
        }
        return Result.success()
    }
}
