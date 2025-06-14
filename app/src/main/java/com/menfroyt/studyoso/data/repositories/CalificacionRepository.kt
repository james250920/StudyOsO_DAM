package com.menfroyt.studyoso.data.repositories

import com.menfroyt.studyoso.data.dao.CalificacionDao
import com.menfroyt.studyoso.data.entities.Calificacion
import kotlinx.coroutines.flow.Flow

class CalificacionRepository(private val dao: CalificacionDao) {
    fun getCalificacionesByCurso(cursoId: Int): Flow<List<Calificacion>> = dao.getCalificacionesByCurso(cursoId)

    fun getCalificacionesByTipoPrueba(tipoPruebaId: Int): Flow<List<Calificacion>> = dao.getCalificacionesByTipoPrueba(tipoPruebaId)

    suspend fun insert(calificacion: Calificacion) = dao.insert(calificacion)

    suspend fun update(calificacion: Calificacion) = dao.update(calificacion)

    suspend fun delete(calificacion: Calificacion) = dao.delete(calificacion)

    fun getCalificacionesByUsuario(usuarioId: Int): Flow<List<Calificacion>> = dao.getCalificacionesByUsuario(usuarioId)

}