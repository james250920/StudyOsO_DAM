package com.menfroyt.studyoso.ViewModel.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menfroyt.studyoso.data.entities.Usuario
import com.menfroyt.studyoso.data.repositories.UsuarioRepository
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
    fun login(
        correo: String,
        contrasena: String,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val usuario = repository.login(correo, contrasena)
                if (usuario != null) {
                    onSuccess(usuario)
                } else {
                    onError("Credenciales incorrectas")
                }
            } catch (e: Exception) {
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