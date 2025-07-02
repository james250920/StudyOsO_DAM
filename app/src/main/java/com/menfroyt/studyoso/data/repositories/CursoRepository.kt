package com.menfroyt.studyoso.data.repositories

import com.menfroyt.studyoso.data.dao.CursoDao
import com.menfroyt.studyoso.data.entities.Curso
import kotlinx.coroutines.flow.Flow

class CursoRepository(private val dao: CursoDao) {
    fun getCursosByUsuario(userId: Int): Flow<List<Curso>> = dao.getCursosByUsuario(userId)

    suspend fun getCurso(id: Int): Curso? = dao.getById(id)

    suspend fun insert(curso: Curso) = dao.insert(curso)

    suspend fun update(curso: Curso) = dao.update(curso)

    suspend fun delete(curso: Curso) = dao.delete(curso)

    fun getCursos(): Flow<List<Curso>> {
        return dao.getCursos()
    }
    fun getTotalCreditosByUsuario(userId: Int): Int? {
        return dao.getTotalCreditosByUsuario(userId)
    }
}