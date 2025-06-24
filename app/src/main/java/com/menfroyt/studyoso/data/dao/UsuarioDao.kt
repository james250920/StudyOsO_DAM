package com.menfroyt.studyoso.data.dao

import androidx.room.Dao
import com.menfroyt.studyoso.data.entities.Usuario
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insert(usuario: Usuario): Long

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)

    @Query("SELECT * FROM Usuarios WHERE id_usuario = :id")
    suspend fun getById(id: Int): Usuario?

    @Query("SELECT * FROM Usuarios")
    fun getAll(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getUsuarioByEmail(correo: String): Usuario?


    //usuario autenticado actualmente
    @Query("SELECT * FROM Usuarios WHERE id_usuario = :id")
    suspend fun getUsuarioAutenticado(id: Int): Usuario?

}