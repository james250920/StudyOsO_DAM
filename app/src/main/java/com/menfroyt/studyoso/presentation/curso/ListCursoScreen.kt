package com.menfroyt.studyoso.presentation.curso

import android.graphics.Color.parseColor
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.repositories.CursoRepository

@Composable
fun ListCursoScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    usuarioId: Int
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Instancia DB, repo y ViewModel
    val db = remember { AppDatabase.getInstance(context) }
    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(
        factory = CursoViewModelFactory(cursoRepository)
    )

    // Observa los cursos del ViewModel
    val cursos by cursoViewModel.cursos.collectAsState()

    // Animación para el FAB
    val fabScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fab_scale"
    )

    // Carga cursos para el usuarioId
    LaunchedEffect(usuarioId) {
        cursoViewModel.cargarCursos(usuarioId)
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
        // Header con título mejorado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mis Cursos",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Gestiona tus materias académicas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Contenido principal con animaciones
                AnimatedVisibility(
                    visible = cursos.isEmpty(),
                    enter = fadeIn(animationSpec = tween(300)) + scaleIn(),
                    exit = fadeOut(animationSpec = tween(300)) + scaleOut()
                ) {
                    EmptyState()
                }

                AnimatedVisibility(
                    visible = cursos.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically(),
                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically()
                ) {
                    CursoList(
                        cursos = cursos,
                        onScreenSelected = onScreenSelected,
                        onDeleteCurso = { curso ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            cursoViewModel.eliminarCurso(curso)
                        },
                        onUpdateCurso = { curso ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            cursoViewModel.actualizarCurso(curso)
                        },
                    )
                }
            }
        }

        // FAB mejorado con animaciones
        FloatingActionButton(
            onClick = { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onScreenSelected("AgregarCursos") 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .scale(fabScale)
                .semantics { 
                    contentDescription = "Agregar nuevo curso" 
                },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
private fun CursoList(
    cursos: List<Curso>,
    onScreenSelected: (String) -> Unit,
    onDeleteCurso: (Curso) -> Unit,
    onUpdateCurso: (Curso) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp) // Espacio para el FAB
    ) {
        items(
            count = cursos.size,
            key = { cursos[it].idCurso }
        ) { index ->
            val curso = cursos[index]
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300, delayMillis = index * 50)) + 
                        slideInVertically(initialOffsetY = { it / 4 }),
                exit = fadeOut() + slideOutVertically()
            ) {
                CursoItem(
                    curso = curso,
                    onClick = { onScreenSelected("DetalleCurso/${curso.idCurso}") },
                    onDelete = { onDeleteCurso(curso) },
                    onUpdate = onUpdateCurso,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
                    imageVector = Icons.Filled.AddToPhotos,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Texto principal mejorado
        Text(
            text = "No hay cursos agregados",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Comienza agregando tus primeros cursos para organizar tu estudio",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun CursoItem(
    curso: Curso,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (Curso) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogoActualizar by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick() 
            }
            .semantics { 
                contentDescription = "Ver detalles del curso ${curso.nombreCurso}" 
            },

        shape = RoundedCornerShape(16.dp),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono del curso con mejor diseño
                    Icon(
                        imageVector = Icons.Filled.Book,
                        contentDescription = "null",
                        tint = Color(parseColor(curso.color)),
                        modifier = Modifier.size(60.dp)
                    )

            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del curso mejorada
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = curso.nombreCurso.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = curso.aula.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${curso.creditos} créditos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Botones de acción mejorados
            Row {
                IconButton(
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        mostrarDialogoActualizar = true 
                    },
                    modifier = Modifier.semantics { 
                        contentDescription = "Editar curso ${curso.nombreCurso}" 
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
                        contentDescription = "Eliminar curso ${curso.nombreCurso}" 
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

        if (mostrarDialogoActualizar) {
            DialogoActualizarCurso(
                curso = curso,
                onDismiss = { mostrarDialogoActualizar = false },
                onConfirm = { cursoActualizado ->
                    onUpdate(cursoActualizado)
                    mostrarDialogoActualizar = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoActualizarCurso(
    curso: Curso,
    onDismiss: () -> Unit,
    onConfirm: (Curso) -> Unit
) {
    var nombreCurso by remember { mutableStateOf(curso.nombreCurso) }
    var profesor by remember { mutableStateOf(curso.profesor) }
    var creditos by remember { mutableStateOf("${curso.creditos}") }
    var color by remember { mutableStateOf(curso.color) }
    var tipoAulaExpanded by remember { mutableStateOf(false) }
    var tipoAulaSeleccionado by remember { mutableStateOf(curso.aula ?: "Presencial") }

    val tiposAula = listOf("Presencial", "Virtual", "Híbrido")
    val colores = listOf(
        "#FF1744" to "Rojo",
        "#2979FF" to "Azul",
        "#00E676" to "Verde",
        "#FF3D00" to "Naranja",
        "#651FFF" to "Morado",
        "#00B8D4" to "Cian",
        "#FF4081" to "Rosa",
        "#9E9E9E" to "Gris",
        "#212121" to "Gris Oscuro",
        "#BDBDBD" to "Gris Claro",
        "#6200EE" to "Morado Oscuro",
        "#03DAC5" to "Cian Claro",
        "#f60a87" to "Rosa Oscuro",
        "#6200EE" to "Morado",
        "#03DAC5" to "Cian",
        "#FFC107" to "" ,
        "#FF5722" to "",
        "#9C27B0" to "",
        "#2196F3" to " ",
        "#4CAF50" to "  "
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Curso") },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nombreCurso,
                    onValueChange = { nombreCurso = it },
                    label = { Text("Nombre del curso") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = profesor.toString(),
                    onValueChange = { profesor = it },
                    label = { Text("Profesor") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = tipoAulaExpanded,
                    onExpandedChange = { tipoAulaExpanded = it }
                ) {
                    OutlinedTextField(
                        value = tipoAulaSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Aula") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoAulaExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = tipoAulaExpanded,
                        onDismissRequest = { tipoAulaExpanded = false }
                    ) {
                        tiposAula.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoAulaSeleccionado = tipo
                                    tipoAulaExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = creditos.toString(),
                    onValueChange = { creditos = it },
                    label = { Text("Créditos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Color del curso:", style = MaterialTheme.typography.bodyMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(colores.size) { index ->
                        val (colorHex, nombre) = colores[index]
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(parseColor(colorHex)), CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = if (color == colorHex) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { color = colorHex }
                        ) {
                            if (color == colorHex) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(12.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(curso.copy(
                    nombreCurso = nombreCurso,
                    profesor = profesor,
                    aula = tipoAulaSeleccionado,
                    creditos = creditos.toIntOrNull() ?: 0,
                    color = color,
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