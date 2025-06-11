package com.menfroyt.studyoso.data.repositories

import com.menfroyt.studyoso.data.dao.TipoPruebaDao
import com.menfroyt.studyoso.data.entities.TipoPrueba
import com.menfroyt.studyoso.domain.CursoConTiposPrueba
import kotlinx.coroutines.flow.Flow

class TipoPruebaRepository(private val dao: TipoPruebaDao) {
    fun getTiposByCurso(cursoId: Int): Flow<List<TipoPrueba>> = dao.getTiposByCurso(cursoId)

    suspend fun insert(tipoPrueba: TipoPrueba) = dao.insert(tipoPrueba)

    suspend fun update(tipoPrueba: TipoPrueba) = dao.update(tipoPrueba)

    suspend fun delete(tipoPrueba: TipoPrueba) = dao.delete(tipoPrueba)

    fun getTiposPruebaByCurso(cursoId: Int): Flow<List<CursoConTiposPrueba>> {
        return dao.getTiposPruebaByCurso(cursoId)
    }
}