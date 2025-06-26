package com.menfroyt.studyoso.data.repositories

import com.menfroyt.studyoso.data.dao.UsuarioDao
import com.menfroyt.studyoso.data.entities.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val dao: UsuarioDao) {
    val usuarios: Flow<List<Usuario>> = dao.getAll()

    suspend fun getUsuario(id: Int): Usuario? = dao.getById(id)

    suspend fun insert(usuario: Usuario) = dao.insert(usuario)

    suspend fun update(usuario: Usuario) = dao.update(usuario)

    suspend fun delete(usuario: Usuario) = dao.delete(usuario)

    suspend fun getUsuarioByEmail(correo: String): Usuario? {
        return dao.getUsuarioByEmail(correo)
    }

    suspend fun getUsuarioAutenticado(id: Int): Usuario? {
        return dao.getUsuarioAutenticado(id)
    }
}