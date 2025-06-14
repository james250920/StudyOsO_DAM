package com.menfroyt.studyoso.domain

data class Horario(
    val id: Int = 0,
    val cursoId: Int,
    val diaSemana: String, // "Lunes", "Martes", etc.
    val horaInicio: String, // "HH:mm:ss"
    val horaFin: String
)