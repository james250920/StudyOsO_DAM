package com.menfroyt.studyoso.data.dao

import androidx.room.Dao
import androidx.room.*
import com.menfroyt.studyoso.data.entities.Calificacion
import kotlinx.coroutines.flow.Flow


@Dao
interface CalificacionDao {
    @Insert
    suspend fun insert(calificacion: Calificacion): Long

    @Update
    suspend fun update(calificacion: Calificacion)

    @Delete
    suspend fun delete(calificacion: Calificacion)

    @Query("SELECT * FROM Calificaciones WHERE id_curso = :cursoId")
    fun getCalificacionesByCurso(cursoId: Int): Flow<List<Calificacion>>

    @Query("SELECT * FROM Calificaciones WHERE id_tipo_prueba = :tipoPruebaId")
    fun getCalificacionesByTipoPrueba(tipoPruebaId: Int): Flow<List<Calificacion>>

    @Query("""
    SELECT cal.*
    FROM Calificaciones cal
    INNER JOIN Cursos c ON cal.id_curso = c.id_curso
    WHERE c.id_usuario = :usuarioId
""")
    fun getCalificacionesByUsuario(usuarioId: Int): Flow<List<Calificacion>>
}