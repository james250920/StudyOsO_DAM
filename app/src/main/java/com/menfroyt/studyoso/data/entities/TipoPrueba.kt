package com.menfroyt.studyoso.data.entities

import androidx.room.*

@Entity(
    tableName = "Tipos_Prueba",
    foreignKeys = [
        ForeignKey(
            entity = Curso::class,
            parentColumns = ["id_curso"],
            childColumns = ["id_curso"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_curso")]
)
data class TipoPrueba(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_tipo_prueba")
    val idTipoPrueba: Int = 0,

    @ColumnInfo(name = "id_curso")
    val idCurso: Int,

    @ColumnInfo(name = "nombre_tipo")
    val nombreTipo: String,

    @ColumnInfo(name = "cantidad_pruebas")
    val cantidadPruebas: Int,

    @ColumnInfo(name = "peso_total")
    val pesoTotal: Double
)