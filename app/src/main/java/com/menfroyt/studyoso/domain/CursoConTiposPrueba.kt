package com.menfroyt.studyoso.domain

import androidx.room.ColumnInfo


data class CursoConTiposPrueba(
    @ColumnInfo(name = "idCurso")
    val idCurso: Int,
    @ColumnInfo(name = "nombreTipo")
    val nombreTipo: String,
    @ColumnInfo(name = "cantidadPruebas")
    val cantidadPruebas: Int,
    @ColumnInfo(name = "pesoTotal")
    val pesoTotal: Double
)
