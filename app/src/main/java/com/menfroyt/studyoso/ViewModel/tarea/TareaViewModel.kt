package com.menfroyt.studyoso.ViewModel.tarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menfroyt.studyoso.data.entities.Tarea
import com.menfroyt.studyoso.data.repositories.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TareaViewModel(private val repository: TareaRepository) : ViewModel() {

    // Estado privado mutable de la lista de tareas
    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())
    val tareas: StateFlow<List<Tarea>> = _tareas.asStateFlow()

    // Carga las tareas del usuario (puedes agregar por curso si quieres)
    fun cargarTareasPorUsuario(usuarioId: Int) {
        viewModelScope.launch {
            repository.getTareasByUsuario(usuarioId).collect { lista ->
                _tareas.value = lista
            }
        }
    }

    // Carga las tareas por curso (opcional)
    fun cargarTareasPorCurso(cursoId: Int) {
        viewModelScope.launch {
            repository.getTareasByCurso(cursoId).collect { lista ->
                _tareas.value = lista
            }
        }
    }

    // Inserta una nueva tarea
    fun agregarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repository.insert(tarea)
        }
    }

    // Actualiza una tarea existente
    fun actualizarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repository.update(tarea)
        }
    }

    // Elimina una tarea
    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repository.delete(tarea)
        }
    }

    // Carga todas las tareas (opcional)
    fun cargarTodasLasTareas() {
        viewModelScope.launch {
            _tareas.value = repository.getAllTareas()
        }
    }
}
