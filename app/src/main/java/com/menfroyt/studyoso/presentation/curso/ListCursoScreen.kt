package com.menfroyt.studyoso.presentation.curso

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.repositories.CursoRepository
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun ListCursoScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    usuarioId: Int
) {
    val context = LocalContext.current

    // Instancia DB, repo y ViewModel
    val db = remember { AppDatabase.getInstance(context) }
    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(
        factory = CursoViewModelFactory(cursoRepository)
    )

    // Observa los cursos del ViewModel
    val cursos by cursoViewModel.cursos.collectAsState()

    // Carga cursos para el usuarioId
    LaunchedEffect(usuarioId) {
        cursoViewModel.cargarCursos(usuarioId)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (cursos.isEmpty()) {
                EmptyState()
            } else {
                CursoList(
                    cursos = cursos,
                    onScreenSelected = onScreenSelected,
                    onDeleteCurso = { curso ->
                        cursoViewModel.eliminarCurso(curso)
                    },
                    onUpdateCurso = { curso ->
                        cursoViewModel.actualizarCurso(curso)
                    },
                )
            }
        }

        FloatingActionButton(
            onClick = { onScreenSelected("AgregarCursos") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF3355ff),
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Agregar Curso",
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
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            count = cursos.size,
            key = { cursos[it].idCurso }
        ) { index ->
            val curso = cursos[index]
            CursoItem(
                curso = curso,
                onClick = { onScreenSelected("DetalleCurso/${curso.idCurso}") },
                onDelete = { onDeleteCurso(curso) },
                onUpdate = onUpdateCurso,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.AddToPhotos,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF3355ff)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay cursos agregados",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Book,
                contentDescription = null,
                tint = Color(parseColor(curso.color)),
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = curso.nombreCurso.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = curso.aula.toString(),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = curso.creditos.toString(),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Icon(
                imageVector = Icons.Filled.Create,
                contentDescription = "Actualizar curso",
                modifier = Modifier
                    .clickable { mostrarDialogoActualizar = true }
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Eliminar curso",
                modifier = Modifier
                    .clickable(onClick = onDelete)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.error
            )
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