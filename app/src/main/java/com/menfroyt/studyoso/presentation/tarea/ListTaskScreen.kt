package com.menfroyt.studyoso.presentation.tarea


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.tarea.TareaViewModel
import com.menfroyt.studyoso.ViewModel.tarea.TareaViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Tarea
import com.menfroyt.studyoso.data.repositories.TareaRepository
import androidx.compose.material.icons.filled.DateRange


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
                onUpdateTask = { tareaViewModel.actualizarTarea(it) },
                onTaskClick = { tarea ->
                    // Implementar navegación si es necesario
                }
            )
        }

        FloatingActionButton(
            onClick = { onScreenSelected("AddTaskScreen") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF3355ff),
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
    onUpdateTask: (Tarea) -> Unit,
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
                onDelete = { onDeleteTask(tarea) },
                onUpdate = { onUpdateTask(it) }
            )
        }
    }
}

@Composable
private fun TaskItem(
    tarea: Tarea,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (Tarea) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogoActualizar by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
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

            IconButton(onClick = { mostrarDialogoActualizar = true }) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = "Actualizar tarea",
                    tint = MaterialTheme.colorScheme.primary
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

        if (mostrarDialogoActualizar) {
            DialogoActualizarTarea(
                tarea = tarea,
                onDismiss = { mostrarDialogoActualizar = false },
                onConfirm = { tareaActualizada ->
                    onUpdate(tareaActualizada)
                    mostrarDialogoActualizar = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoActualizarTarea(
    tarea: Tarea,
    onDismiss: () -> Unit,
    onConfirm: (Tarea) -> Unit
) {
    var descripcionTarea by remember { mutableStateOf(tarea.descripcion) }
    var esImportante by remember { mutableStateOf(tarea.esImportante) }
    var esUrgente by remember { mutableStateOf(tarea.esUrgente) }
    var fechaVencimiento by remember { mutableStateOf(tarea.fechaVencimiento) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        fechaVencimiento = formatter.format(java.util.Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Tarea") },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = descripcionTarea,
                    onValueChange = { descripcionTarea = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("¿Es importante?", style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = esImportante,
                        onClick = { esImportante = true }
                    )
                    Text("Sí", modifier = Modifier.padding(start = 8.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = !esImportante,
                        onClick = { esImportante = false }
                    )
                    Text("No", modifier = Modifier.padding(start = 8.dp))
                }

                Text("¿Es urgente?", style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = esUrgente,
                        onClick = { esUrgente = true }
                    )
                    Text("Sí", modifier = Modifier.padding(start = 8.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = !esUrgente,
                        onClick = { esUrgente = false }
                    )
                    Text("No", modifier = Modifier.padding(start = 8.dp))
                }

                OutlinedTextField(
                    value = fechaVencimiento,
                    onValueChange = { },
                    label = { Text("Fecha de vencimiento") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Seleccionar fecha"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(tarea.copy(
                    descripcion = descripcionTarea,
                    esImportante = esImportante,
                    esUrgente = esUrgente,
                    fechaVencimiento = fechaVencimiento
                ))
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
