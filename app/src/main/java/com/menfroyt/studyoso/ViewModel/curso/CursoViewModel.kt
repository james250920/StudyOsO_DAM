package com.menfroyt.studyoso.ViewModel.curso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.repositories.CursoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CursoViewModel(private val repository: CursoRepository) : ViewModel() {

    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> = _cursos.asStateFlow()

    private val _cursoSeleccionado = MutableStateFlow<Curso?>(null)
    val cursoSeleccionado: StateFlow<Curso?> = _cursoSeleccionado.asStateFlow()

    // Carga cursos filtrados por usuario
    fun cargarCursos(usuarioId: Int) {
        viewModelScope.launch {
            repository.getCursosByUsuario(usuarioId).collect { lista ->
                _cursos.value = lista
            }
        }
    }

    // Carga todos los cursos sin filtro
    fun cargarTodosLosCursos() {
        viewModelScope.launch {
            repository.getCursos().collect { lista ->
                _cursos.value = lista
            }
        }
    }

    // Agrega un curso
    fun agregarCurso(curso: Curso) {
        viewModelScope.launch {
            repository.insert(curso)
        }
    }

    // Actualiza un curso
    fun actualizarCurso(curso: Curso) {
        viewModelScope.launch {
            repository.update(curso)
        }
    }

    // Elimina un curso
    fun eliminarCurso(curso: Curso) {
        viewModelScope.launch {
            repository.delete(curso)
        }
    }

    // Obtiene un curso por id y actualiza cursoSeleccionado
    suspend fun getCursoById(id: Int): Curso? {
        val curso = repository.getCurso(id)
        _cursoSeleccionado.value = curso
        return curso
    }

    // Solo actualiza el curso seleccionado sin modificar la lista
    fun seleccionarCurso(curso: Curso) {
        _cursoSeleccionado.value = curso
    }
}

