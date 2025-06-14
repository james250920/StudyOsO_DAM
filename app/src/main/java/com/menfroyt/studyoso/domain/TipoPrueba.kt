package com.menfroyt.studyoso.domain

data class TipoPrueba(
    val id: Int = 0,
    val cursoId: String,
    val nombre: String,
    val numPruebas: Int,
    val peso: Double
)