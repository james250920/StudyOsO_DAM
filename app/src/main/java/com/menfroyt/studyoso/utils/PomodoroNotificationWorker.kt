package com.menfroyt.studyoso.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.menfroyt.studyoso.R

class PomodoroNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val isBreak = inputData.getBoolean("isBreak", false)
        val sessionCount = inputData.getInt("sessionCount", 0)

        mostrarNotificacionPomodoro(isBreak, sessionCount)
        return Result.success()
    }

    private fun mostrarNotificacionPomodoro(isBreak: Boolean, sessionCount: Int) {
        val channelId = "pomodoro_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pomodoro",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val titulo = if (isBreak) "¡Tiempo de descanso!" else "¡Sesión completada!"
        val mensaje = if (isBreak)
            "Es hora de tomar un descanso"
        else
            "Sesión $sessionCount completada. ¡Prepárate para la siguiente!"

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.calificacion)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(100, notification)
    }
}