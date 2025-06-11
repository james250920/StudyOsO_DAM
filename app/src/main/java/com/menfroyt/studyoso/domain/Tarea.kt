package com.menfroyt.studyoso.domain

data class Tarea(
    val id: Int = 0,
    val descripcion: String,
    val esImportante: Boolean = false,
    val esUrgente: Boolean = false,
    val fechaVencimiento: String?, // o Date
    val fechaCreacion: String, // o Date
    val estado: String = "Pendiente",
    val usuarioId: Int,
    val cursoId: Int?
)