package com.menfroyt.studyoso.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.menfroyt.studyoso.MainActivity
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.repositories.TareaRepository
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import com.menfroyt.studyoso.R
import com.menfroyt.studyoso.data.entities.Tarea
import com.menfroyt.studyoso.navigation.NavigationConstants
import com.menfroyt.studyoso.presentation.auth.SessionManager
import kotlinx.coroutines.flow.forEach

class NotificationWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {

    private val db = AppDatabase.getInstance(applicationContext)
    private val tareaRepository = TareaRepository(db.TareaDao())

    override  fun doWork(): Result {
        Log.d("NotificationWorker", "Iniciando verificación de tareas...")

        try {
            val sessionManager = SessionManager(applicationContext)
            if (!sessionManager.isLoggedIn()) {
                Log.d("NotificationWorker", "No hay sesión activa, omitiendo verificación")
                return Result.success()
            }

            runBlocking {
                val tareasNotificar = verificarTareasVencidas()
                Log.d("NotificationWorker", "Tareas encontradas para notificar: ${tareasNotificar.size}")

                tareasNotificar.forEachIndexed { index, par ->
                    val tarea = par.first
                    val estado = par.second
                    val titulo = when(estado) {
                        "VENCIDA" -> "¡Tarea vencida!"
                        "HOY" -> "¡Tarea vence hoy!"
                        "MAÑANA" -> "¡Tarea vence mañana!"
                        else -> "Tarea pendiente"
                    }

                    val mensaje = when(estado) {
                        "VENCIDA" -> "La tarea \"${tarea.descripcion}\" ha vencido"
                        "HOY" -> "La tarea \"${tarea.descripcion}\" vence hoy"
                        "MAÑANA" -> "La tarea \"${tarea.descripcion}\" vence mañana"
                        else -> "La tarea \"${tarea.descripcion}\" está pendiente"
                    }

                    showNotification(titulo, mensaje, index)
                }
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error en doWork: ${e.message}")
            return Result.failure()
        }
    }

    private suspend fun verificarTareasVencidas(): List<Pair<Tarea, String>> {
        val tareasANotificar = mutableListOf<Pair<Tarea, String>>()
        val sessionManager = SessionManager(applicationContext)

        if (!sessionManager.isLoggedIn()) {
            Log.d("NotificationWorker", "No hay sesión activa")
            return emptyList()
        }

        val usuarioActual = sessionManager.getUserId()
        val fechaActual = LocalDate.now()
        val fechaMañana = fechaActual.plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        try {
            val tareasUsuario = runBlocking {
                tareaRepository.getAllTareas().filter { it.idUsuario == usuarioActual }
            }

            tareasUsuario.forEach { tarea ->
                try {
                    val fechaVencimiento = LocalDate.parse(tarea.fechaVencimiento, formatter)
                    if (tarea.estado == "Pendiente") {
                        when {
                            fechaVencimiento.isBefore(fechaActual) ->
                                tareasANotificar.add(Pair(tarea, "VENCIDA"))
                            fechaVencimiento.isEqual(fechaActual) ->
                                tareasANotificar.add(Pair(tarea, "HOY"))
                            fechaVencimiento.isEqual(fechaMañana) ->
                                tareasANotificar.add(Pair(tarea, "MAÑANA"))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NotificationWorker", "Error procesando tarea: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error general: ${e.message}")
        }

        return tareasANotificar
    }


    private fun showNotification(title: String, desc: String, notificationId: Int = 1) {
        // Verificar permisos de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                Log.e("NotificationWorker", "No hay permiso para mostrar notificaciones")
                return
            }
        }

        Log.d("NotificationWorker", "Mostrando notificación: $title")

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_deadline_channel"
        val channelName = "Task Deadlines"

        // Obtener el userId de SessionManager
        val sessionManager = SessionManager(applicationContext)
        val userId = sessionManager.getUserId()
        // Crear Intent para abrir la lista de tareas
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(NavigationConstants.EXTRA_SCREEN, NavigationConstants.SCREEN_LIST_TASK)
            putExtra("userId", userId) // Añadir el userId al intent
        }

        // Crear PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Configurar canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        // Crear y mostrar la notificación con el PendingIntent
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(desc)
            .setSmallIcon(R.drawable.calificacion)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true) // La notificación desaparece al tocarla
            .setContentIntent(pendingIntent) // Agregar el PendingIntent

        manager.notify(notificationId, builder.build())
    }



    companion object {
        const val WORK_RESULT = "work_result"

        // Método para programar verificaciones periódicas
        fun programarVerificacionTareas(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            // Cancelar trabajos antiguos para evitar duplicados
            WorkManager.getInstance(context).cancelAllWorkByTag("verificacion_tareas")

            // Verificar cada 15 minutos (mínimo permitido)
            val periodicWork = PeriodicWorkRequestBuilder<NotificationWorker>(
                15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag("verificacion_tareas")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "verificacion_tareas",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWork)

            // Verificar inmediatamente
            val immediateWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .addTag("verificacion_tareas")
                .build()

            WorkManager.getInstance(context).enqueue(immediateWork)

            Log.d("NotificationWorker", "Trabajos de notificación programados")
        }
    }
}