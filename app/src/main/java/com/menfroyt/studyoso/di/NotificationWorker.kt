package com.menfroyt.studyoso.di

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.repositories.TareaRepository
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import com.menfroyt.studyoso.R
import com.menfroyt.studyoso.data.entities.Tarea

class NotificationWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {

    private val db = AppDatabase.getInstance(applicationContext)
    private val tareaRepository = TareaRepository(db.TareaDao())

    override fun doWork(): Result {
        Log.d("NotificationWorker", "Verificando tareas pendientes...")

        val tareasNotificar = verificarTareasVencidas()
        Log.d("NotificationWorker", "Tareas encontradas: ${tareasNotificar.size}")

        if (tareasNotificar.isNotEmpty()) {
            for ((index, par) in tareasNotificar.withIndex()) {
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

                Log.d("NotificationWorker", "Mostrando notificación: $titulo - $mensaje")
                showNotification(titulo, mensaje, index)
            }
        }

        return Result.success()
    }

    private fun verificarTareasVencidas(): List<Pair<Tarea, String>> {
        val tareasANotificar = mutableListOf<Pair<Tarea, String>>()
        val fechaActual = LocalDate.now()
        val fechaMañana = fechaActual.plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        Log.d("NotificationWorker", "Fecha actual: $fechaActual, Fecha mañana: $fechaMañana")

        try {
            // Obtener todas las tareas directamente (sin filtrar por usuario)
            val todasLasTareas = runBlocking {
                tareaRepository.getAllTareas()
            }

            Log.d("NotificationWorker", "Total tareas en BD: ${todasLasTareas.size}")

            todasLasTareas.forEach { tarea ->
                try {
                    val fechaVencimientoStr = tarea.fechaVencimiento
                    Log.d("NotificationWorker",
                        "Evaluando tarea: ${tarea.descripcion}, Vence: $fechaVencimientoStr, Estado: ${tarea.estado}")

                    val fechaVencimiento = LocalDate.parse(fechaVencimientoStr, formatter)

                    // Comparaciones de fecha
                    val esHoy = fechaVencimiento.isEqual(fechaActual)
                    val esMañana = fechaVencimiento.isEqual(fechaMañana)
                    val yaVencio = fechaVencimiento.isBefore(fechaActual)

                    Log.d("NotificationWorker",
                        "Comparaciones: Vence hoy=$esHoy, Vence mañana=$esMañana, Ya venció=$yaVencio")

                    // Comprobar si la tarea está pendiente
                    if (tarea.estado == "Pendiente") {
                        when {
                            yaVencio -> {
                                Log.d("NotificationWorker", "Tarea VENCIDA: ${tarea.descripcion}")
                                tareasANotificar.add(Pair(tarea, "VENCIDA"))
                            }
                            esHoy -> {
                                Log.d("NotificationWorker", "Tarea vence HOY: ${tarea.descripcion}")
                                tareasANotificar.add(Pair(tarea, "HOY"))
                            }
                            esMañana -> {
                                Log.d("NotificationWorker", "Tarea vence MAÑANA: ${tarea.descripcion}")
                                tareasANotificar.add(Pair(tarea, "MAÑANA"))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NotificationWorker", "Error al procesar tarea: ${e.message}")
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error general: ${e.message}")
            e.printStackTrace()
        }

        Log.d("NotificationWorker", "Tareas a notificar: ${tareasANotificar.size}")
        return tareasANotificar
    }

    private fun showNotification(title: String, desc: String, notificationId: Int = 1) {
        // Verificar si tenemos permisos para notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                Log.e("NotificationWorker", "No hay permiso para mostrar notificaciones")
                return
            }
        }

        // Resto del código para mostrar la notificación...
        Log.d("NotificationWorker", "Mostrando notificación: $title")

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_deadline_channel"
        val channelName = "Task Deadlines"

        // Verificar si la versión de Android es compatible con canales de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        // Crear y mostrar la notificación
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(desc)
            .setSmallIcon(R.drawable.calificacion)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

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