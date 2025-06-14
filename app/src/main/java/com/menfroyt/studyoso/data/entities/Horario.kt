package com.menfroyt.studyoso.data.entities


import androidx.room.*

@Entity(
    tableName = "Horarios",
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
data class Horario(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_horario")
    val idHorario: Int = 0,

    @ColumnInfo(name = "id_curso")
    val idCurso: Int,

    @ColumnInfo(name = "dia_semana")
    val diaSemana: String, 

    @ColumnInfo(name = "hora_inicio")
    val horaInicio: String, // "HH:mm:ss"

    @ColumnInfo(name = "hora_fin")
    val horaFin: String,
	
	@ColumnInfo(name = "aula")
    val aula: String
)