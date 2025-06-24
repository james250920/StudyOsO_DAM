package com.menfroyt.studyoso.ViewModel.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menfroyt.studyoso.data.entities.Usuario
import com.menfroyt.studyoso.data.repositories.UsuarioRepository
import com.menfroyt.studyoso.utils.checkPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _usuarioAutenticado = MutableStateFlow<Usuario?>(null)
    val usuarioAutenticado: StateFlow<Usuario?> = _usuarioAutenticado.asStateFlow()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    fun setUsuarioAutenticado(usuario: Usuario) {
        _usuarioAutenticado.value = usuario
    }

    fun loadUsuarios() {
        viewModelScope.launch {
            repository.usuarios.collect { lista ->
                _usuarios.value = lista
            }
        }
    }

    fun agregarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.insert(usuario)
        }
    }

    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.update(usuario)
        }
    }

    fun eliminarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.delete(usuario)
        }
    }
    fun login(correo: String, contrasena: String, onSuccess: (Usuario) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val usuario = repository.getUsuarioByEmail(correo)
                println("Usuario encontrado: ${usuario != null}")
                if (usuario != null) {
                    println("Hash almacenado: ${usuario.contrasena}")
                    println("Contraseña ingresada (sin hash): $contrasena")
                    val isValidPassword = checkPassword(contrasena, usuario.contrasena)
                    println("Resultado de verificación: $isValidPassword")

                    if (isValidPassword) {
                        println("Login exitoso")
                        _usuarioAutenticado.value = usuario
                        onSuccess(usuario)
                    } else {
                        println("Contraseña incorrecta")
                        onError("Credenciales incorrectas")
                    }
                } else {
                    println("Usuario no encontrado")
                    onError("Usuario no encontrado")
                }
            } catch (e: Exception) {
                println("Error en login: ${e.message}")
                e.printStackTrace()
                onError("Error al iniciar sesión: ${e.message}")
            }
        }
    }
    fun getUsuarioAutenticado(id: Int, onSuccess: (Usuario) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val usuario = repository.getUsuarioAutenticado(id)
                if (usuario != null) {
                    onSuccess(usuario)
                } else {
                    onError("Usuario no encontrado")
                }
            } catch (e: Exception) {
                onError("Error al obtener usuario autenticado: ${e.message}")
            }
        }
    }
}