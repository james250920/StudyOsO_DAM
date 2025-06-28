package com.menfroyt.studyoso.data.repositories

import com.menfroyt.studyoso.data.dao.TareaDao
import com.menfroyt.studyoso.data.entities.Tarea
import kotlinx.coroutines.flow.Flow

class TareaRepository(private val dao: TareaDao) {
    suspend fun getTareasByUsuario(userId: Int): Flow<List<Tarea>> = dao.getTareasByUsuario(userId)

    fun getTareasByCurso(cursoId: Int): Flow<List<Tarea>> = dao.getTareasByCurso(cursoId)

    suspend fun insert(tarea: Tarea) = dao.insert(tarea)

    suspend fun update(tarea: Tarea) = dao.update(tarea)

    suspend fun delete(tarea: Tarea) = dao.delete(tarea)
    suspend fun getAllTareas(): List<Tarea> = dao.getAllTareas()
    suspend fun getTareasByUsuarioId(userId: Int): List<Tarea> {
        return dao.getAllTareas().filter { it.idUsuario== userId }
    }


}