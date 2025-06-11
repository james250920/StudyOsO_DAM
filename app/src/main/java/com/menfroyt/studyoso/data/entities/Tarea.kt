package com.menfroyt.studyoso.data.entities


import androidx.room.*

@Entity(
    tableName = "Tareas",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Curso::class,
            parentColumns = ["id_curso"],
            childColumns = ["id_curso"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("id_usuario"), Index("id_curso")]
)
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_tarea")
    val idTarea: Int = 0,

    @ColumnInfo(name = "descripcion")
    val descripcion: String,

    @ColumnInfo(name = "es_importante")
    val esImportante: Boolean = false,

    @ColumnInfo(name = "es_urgente")
    val esUrgente: Boolean = false,

    @ColumnInfo(name = "fecha_vencimiento")
    val fechaVencimiento: String,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String,

    @ColumnInfo(name = "estado")
    val estado: String = "Pendiente", // Validar valores en backend

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "id_curso")
    val idCurso: Int? = null
)