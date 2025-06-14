package com.menfroyt.studyoso.data.repositories

import com.menfroyt.studyoso.data.dao.HorarioDao
import com.menfroyt.studyoso.data.entities.Horario
import kotlinx.coroutines.flow.Flow

class HorarioRepository(private val dao: HorarioDao) {
    fun getHorariosByCurso(cursoId: Int): Flow<List<Horario>> = dao.getHorariosByCurso(cursoId)

    suspend fun insert(horario: Horario) = dao.insert(horario)

    suspend fun update(horario: Horario) = dao.update(horario)

    suspend fun delete(horario: Horario) = dao.delete(horario)

    fun getAllHorarios(): Flow<List<Horario>> = dao.getAllHorarios()

}