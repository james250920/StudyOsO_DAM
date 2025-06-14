package com.menfroyt.studyoso.data.dao

import androidx.room.Dao
import androidx.room.*
import com.menfroyt.studyoso.data.entities.Horario
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Insert
    suspend fun insert(horario: Horario): Long

    @Update
    suspend fun update(horario: Horario)

    @Delete
    suspend fun delete(horario: Horario)

    @Query("SELECT * FROM Horarios WHERE id_curso = :cursoId")
    fun getHorariosByCurso(cursoId: Int): Flow<List<Horario>>

    @Query("SELECT * FROM Horarios")
    fun getAllHorarios(): Flow<List<Horario>>
}