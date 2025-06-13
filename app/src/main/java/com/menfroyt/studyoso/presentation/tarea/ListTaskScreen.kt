package com.menfroyt.studyoso.presentation.tarea

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import esan.mendoza.teststudyoso.ViewModel.tarea.TareaViewModel
import esan.mendoza.teststudyoso.ViewModel.tarea.TareaViewModelFactory
import esan.mendoza.teststudyoso.data.db.AppDatabase
import esan.mendoza.teststudyoso.data.entities.Tarea
import esan.mendoza.teststudyoso.data.repositories.TareaRepository
import java.time.format.DateTimeFormatter
import java.time.LocalDate

@Composable
fun ListTaskScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    usuarioId: Int
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val tareaRepository = remember { TareaRepository(db.TareaDao()) }
    val tareaViewModel: TareaViewModel = viewModel(factory = TareaViewModelFactory(tareaRepository))

    // Estado reactivo que obtiene las tareas del usuario
    val tareas by tareaViewModel.tareas.collectAsState()

    // Cargar las tareas del usuario en el ViewModel
    LaunchedEffect(usuarioId) {
        tareaViewModel.cargarTareasPorUsuario(usuarioId)
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (tareas.isEmpty()) {
            EmptyState()
        } else {
            TaskList(
                tareas = tareas,
                onDeleteTask = { tareaViewModel.eliminarTarea(it) },
                onTaskClick = { tarea ->
                    // Aquí puedes implementar navegación a detalle o edición de tarea
                    // Ejemplo: onScreenSelected("DetalleTarea/${tarea.idTarea}")
                }
            )
        }

        FloatingActionButton(
            onClick = { onScreenSelected("AddTaskScreen") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar Tarea")
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No hay tareas registradas",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun TaskList(
    tareas: List<Tarea>,
    onDeleteTask: (Tarea) -> Unit,
    onTaskClick: (Tarea) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(tareas, key = { it.idTarea }) { tarea ->
            TaskItem(
                tarea = tarea,
                onClick = { onTaskClick(tarea) },
                onDelete = { onDeleteTask(tarea) }
            )
        }
    }
}

@Composable
private fun TaskItem(
    tarea: Tarea,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tarea.descripcion,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Importante: ${if (tarea.esImportante) "Sí" else "No"} | Urgente: ${if (tarea.esUrgente) "Sí" else "No"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vence: ${tarea.fechaVencimiento}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar tarea",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


