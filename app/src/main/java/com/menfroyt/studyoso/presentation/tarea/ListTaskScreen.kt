package com.menfroyt.studyoso.presentation.tarea

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.tarea.TareaViewModel
import com.menfroyt.studyoso.ViewModel.tarea.TareaViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Tarea
import com.menfroyt.studyoso.data.repositories.TareaRepository
import com.menfroyt.studyoso.presentation.utils.EisenhowerColors


@Composable
fun ListTaskScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    usuarioId: Int
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val db = remember { AppDatabase.getInstance(context) }
    val tareaRepository = remember { TareaRepository(db.TareaDao()) }
    val tareaViewModel: TareaViewModel = viewModel(factory = TareaViewModelFactory(tareaRepository))

    // Estado reactivo que obtiene las tareas del usuario
    val tareas by tareaViewModel.tareas.collectAsState()

    // Animación para el FAB
    val fabScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fab_scale"
    )

    // Cargar las tareas del usuario en el ViewModel
    LaunchedEffect(usuarioId) {
        tareaViewModel.cargarTareasPorUsuario(usuarioId)
    }
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface
        )
    )
    // Contenedor principal mejorado
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mis Tareas",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Organiza tu tiempo y prioridades",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Resumen de tareas por categoría Eisenhower
                AnimatedVisibility(
                    visible = tareas.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically(),
                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically()
                ) {
                    EisenhowerSummary(tareas = tareas)
                }

                // Contenido principal con animaciones
                AnimatedVisibility(
                    visible = tareas.isEmpty(),
                    enter = fadeIn(animationSpec = tween(300)) + scaleIn(),
                    exit = fadeOut(animationSpec = tween(300)) + scaleOut()
                ) {
                    EmptyState()
                }

                AnimatedVisibility(
                    visible = tareas.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically(),
                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically()
                ) {
                    TaskList(
                        tareas = tareas,
                        onDeleteTask = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            tareaViewModel.eliminarTarea(it) 
                        },
                        onUpdateTask = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            tareaViewModel.actualizarTarea(it) 
                        },
                        onTaskClick = { tarea ->
                            // Implementar navegación si es necesario
                        }
                    )
                }

        }

        // FAB mejorado con animaciones
        FloatingActionButton(
            onClick = { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onScreenSelected("AddTaskScreen") 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .scale(fabScale)
                .semantics { 
                    contentDescription = "Agregar nueva tarea" 
                },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Filled.Add, 
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun EmptyState() {
    val haptic = LocalHapticFeedback.current
    
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ícono principal con animación
        Card(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),

        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Texto principal mejorado
        Text(
            text = "No hay tareas registradas",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Organiza tu tiempo agregando tus primeras tareas",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp) // Espacio para el FAB
    ) {
        items(tareas, key = { it.idTarea }) { tarea ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + 
                        slideInVertically(initialOffsetY = { it / 4 }),
                exit = fadeOut() + slideOutVertically()
            ) {
                TaskItem(
                    tarea = tarea,
                    onClick = { onTaskClick(tarea) },
                    onDelete = { onDeleteTask(tarea) },
                    onUpdate = { onUpdateTask(it) }
                )
            }
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
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { 
                contentDescription = "Tarea: ${tarea.descripcion}" 
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            color = EisenhowerColors.getBorderColor(
                tarea.esImportante, 
                tarea.esUrgente,
                alpha = 0.4f
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header de la tarea
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Icono de prioridad
                Card(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EisenhowerColors.getBackgroundColor(
                            tarea.esImportante, 
                            tarea.esUrgente, 
                            alpha = 0.15f
                        )
                    ),
                    border = BorderStroke(
                        width = 1.dp, 
                        color = EisenhowerColors.getBorderColor(
                            tarea.esImportante, 
                            tarea.esUrgente
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                tarea.esImportante && tarea.esUrgente -> Icons.Default.PriorityHigh
                                tarea.esImportante -> Icons.Default.Task
                                tarea.esUrgente -> Icons.Default.Speed
                                else -> Icons.AutoMirrored.Filled.Assignment
                            },
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = EisenhowerColors.getColorForTask(
                                tarea.esImportante, 
                                tarea.esUrgente
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Contenido de la tarea
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Etiquetas de prioridad mejoradas con colores de Eisenhower
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Etiqueta principal de categoría Eisenhower
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = EisenhowerColors.getBackgroundColor(
                                    tarea.esImportante, 
                                    tarea.esUrgente, 
                                    alpha = 0.15f
                                )
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = EisenhowerColors.getBorderColor(
                                    tarea.esImportante, 
                                    tarea.esUrgente
                                )
                            )
                        ) {
                            Text(
                                text = EisenhowerColors.getTitleForQuadrant(
                                    tarea.esImportante, 
                                    tarea.esUrgente
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = EisenhowerColors.getColorForTask(
                                    tarea.esImportante, 
                                    tarea.esUrgente
                                )
                            )
                        }
                        
                        // Etiquetas individuales si es necesario
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (tarea.esImportante) {
                                Card(
                                    shape = RoundedCornerShape(6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = EisenhowerColors.NoUrgenteImportante.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Text(
                                        text = "Importante",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = EisenhowerColors.NoUrgenteImportante
                                    )
                                }
                            }
                            
                            if (tarea.esUrgente) {
                                Card(
                                    shape = RoundedCornerShape(6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = EisenhowerColors.UrgenteNoImportante.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Text(
                                        text = "Urgente",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = EisenhowerColors.UrgenteNoImportante
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Fecha de vencimiento
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Vence: ${tarea.fechaVencimiento}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Botones de acción
                Row {
                    IconButton(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            mostrarDialogoActualizar = true 
                        },
                        modifier = Modifier.semantics { 
                            contentDescription = "Editar tarea" 
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDelete() 
                        },
                        modifier = Modifier.semantics { 
                            contentDescription = "Eliminar tarea" 
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
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
                        // Convertir milisegundos UTC a LocalDate usando UTC para evitar problemas de zona horaria
                        val localDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                        fechaVencimiento = localDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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

@Composable
private fun EisenhowerSummary(
    tareas: List<Tarea>,
    modifier: Modifier = Modifier
) {
    val urgenteImportante = tareas.count { it.esUrgente && it.esImportante }
    val noUrgenteImportante = tareas.count { !it.esUrgente && it.esImportante }
    val urgenteNoImportante = tareas.count { it.esUrgente && !it.esImportante }
    val noUrgenteNoImportante = tareas.count { !it.esUrgente && !it.esImportante }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),

    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen por categorías",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Hacer Ahora - Urgente e Importante
                SummaryItem(
                    count = urgenteImportante,
                    title = "Hacer Ahora",
                    color = EisenhowerColors.UrgenteImportante,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Planificar - No Urgente e Importante
                SummaryItem(
                    count = noUrgenteImportante,
                    title = "Planificar",
                    color = EisenhowerColors.NoUrgenteImportante,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Delegar - Urgente y No Importante
                SummaryItem(
                    count = urgenteNoImportante,
                    title = "Delegar",
                    color = EisenhowerColors.UrgenteNoImportante,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Eliminar - No Urgente y No Importante
                SummaryItem(
                    count = noUrgenteNoImportante,
                    title = "Eliminar",
                    color = EisenhowerColors.NoUrgenteNoImportante,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    count: Int,
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
