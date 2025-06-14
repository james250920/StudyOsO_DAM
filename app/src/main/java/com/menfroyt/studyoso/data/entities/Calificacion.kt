package com.menfroyt.studyoso.data.entities



import androidx.room.*


@Entity(
    tableName = "Calificaciones",
    foreignKeys = [
        ForeignKey(
            entity = Curso::class,
            parentColumns = ["id_curso"],
            childColumns = ["id_curso"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TipoPrueba::class,
            parentColumns = ["id_tipo_prueba"],
            childColumns = ["id_tipo_prueba"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_curso"), Index("id_tipo_prueba")]
)
data class Calificacion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_calificacion")
    val idCalificacion: Int = 0,

    @ColumnInfo(name = "id_curso")
    val idCurso: Int,

    @ColumnInfo(name = "id_tipo_prueba")
    val idTipoPrueba: Int,

    @ColumnInfo(name = "numero_prueba")
    val numeroPrueba: Int,

    @ColumnInfo(name = "calificacion_obtenida")
    val calificacionObtenida: Double?
)