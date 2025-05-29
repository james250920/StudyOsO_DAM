package com.menfroyt.studyoso.presentation.tarea

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class Tarea(
    val titulo: String,
    val descripcion: String,
    val prioridad: Prioridad
)

enum class Prioridad {
    MUY_URGENTE, URGENTE, PROXIMO_A_VENCER, NO_URGENTE
}

@Composable
fun ListTaskScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
) {
    val listaTareas by remember {
        mutableStateOf(
            listOf(
                Tarea("Entregar informe", "Informe de gestión mensual", Prioridad.MUY_URGENTE),
                Tarea("Leer capítulo 3", "Libro de física cuántica", Prioridad.URGENTE),
                Tarea("Estudiar para el examen", "Examen final en 3 días", Prioridad.PROXIMO_A_VENCER),
                Tarea("Organizar escritorio", "Limpiar y archivar documentos", Prioridad.NO_URGENTE)
            )
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (listaTareas.isEmpty()) {
                EmptyTaskState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(listaTareas) { tarea ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = colorPorPrioridad(tarea.prioridad)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                /*Text(
                                    text = tarea.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black
                                ) */
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tarea.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { onScreenSelected("AddTaskScreen") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Agregar Tarea",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun EmptyTaskState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No hay tareas registradas",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun colorPorPrioridad(prioridad: Prioridad): Color {
    return when (prioridad) {
        Prioridad.MUY_URGENTE -> Color(0xFFFF8A80)       // rojo claro
        Prioridad.URGENTE -> Color(0xFFFFCC80)            // naranja
        Prioridad.PROXIMO_A_VENCER -> Color(0xFFFFF176)   // amarillo
        Prioridad.NO_URGENTE -> Color(0xFFA5D6A7)         // verde
    }
}
