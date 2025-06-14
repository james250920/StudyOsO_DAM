package com.menfroyt.studyoso.data.dao

import androidx.room.Dao
import androidx.room.*
import com.menfroyt.studyoso.data.entities.TipoPrueba
import com.menfroyt.studyoso.domain.CursoConTiposPrueba
import kotlinx.coroutines.flow.Flow


@Dao
interface TipoPruebaDao {
    @Insert
    suspend fun insert(tipoPrueba: TipoPrueba): Long

    @Update
    suspend fun update(tipoPrueba: TipoPrueba)

    @Delete
    suspend fun delete(tipoPrueba: TipoPrueba)

    @Query("SELECT * FROM Tipos_Prueba WHERE id_curso = :cursoId")
    fun getTiposByCurso(cursoId: Int): Flow<List<TipoPrueba>>

    //prueba
    @Query("""
    SELECT 
        c.id_curso AS idCurso,
        tp.nombre_tipo AS nombreTipo,
        tp.cantidad_pruebas AS cantidadPruebas,
        tp.peso_total AS pesoTotal
    FROM Cursos c
    INNER JOIN Tipos_Prueba tp ON c.id_curso = tp.id_curso
    WHERE c.id_curso = :cursoId
""")
    fun getTiposPruebaByCurso(cursoId: Int): Flow<List<CursoConTiposPrueba>>


}