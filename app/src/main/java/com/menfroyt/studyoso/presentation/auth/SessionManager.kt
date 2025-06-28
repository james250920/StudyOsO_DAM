package com.menfroyt.studyoso.presentation.auth

import android.content.Context
import androidx.work.WorkManager
import android.app.NotificationManager
import android.util.Log


class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private val context: Context = context.applicationContext

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveSession(userId: Int, email: String) {
        editor.apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun endSession() {
        val userId = getUserId()
        editor.clear().apply()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        WorkManager.getInstance(context).cancelAllWorkByTag("verificacion_tareas")
        Log.d("SessionManager", "Sesión terminada para usuario $userId")
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
}