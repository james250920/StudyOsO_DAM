package com.menfroyt.studyoso.domain

data class Curso(
    val id: Int = 0,
    val nombreCurso: String,
    val color: String?,
    val profesor: String?,
    val aula: String?,
    val creditos: Int?,
    val usuarioId: Int
)