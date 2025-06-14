package com.menfroyt.studyoso.data.entities






import androidx.room.*

@Entity(
    tableName = "Cursos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_usuario")]
)
data class Curso(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_curso")
    val idCurso: Int = 0,

    @ColumnInfo(name = "nombre_curso")
    val nombreCurso: String,

    @ColumnInfo(name = "color")
    val color: String? = null,

    @ColumnInfo(name = "profesor")
    val profesor: String? = null,

    @ColumnInfo(name = "aula")
    val aula: String? = null,

    @ColumnInfo(name = "creditos")
    val creditos: Int? = null,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int
)