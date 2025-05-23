package com.menfroyt.studyoso.presentation.calificación

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun ListCalificacionScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
) {
    var cursos by remember {
        mutableStateOf(
            listOf(
                Curso(
                    id = 1,
                    nombreCurso = "Programación",
                    profesor = "Juan Pérez",
                    pruebas = emptyList(),
                    aula = "205"
                ),
                Curso(
                    id = 2,
                    nombreCurso = "Programación",
                    profesor = "Juan Pérez",
                    pruebas = emptyList(),
                    aula = "205"
                ),
                Curso(
                    id = 3,
                    nombreCurso = "Programación",
                    profesor = "Juan Pérez",
                    pruebas = emptyList(),
                    aula = "205"
                ),
                Curso(
                    id = 4,
                    nombreCurso = "Programación",
                    profesor = "Juan Pérez",
                    pruebas = emptyList(),
                    aula = "205"
                ),
                Curso(
                    id = 5,
                    nombreCurso = "Programación",
                    profesor = "Juan Pérez",
                    pruebas = emptyList(),
                    aula = "205"
                ),
                Curso(
                    id = 6,
                    nombreCurso = "Programación",
                    profesor = "Juan Pérez",
                    pruebas = emptyList(),
                    aula = "205"
                ),
                Curso(
                    id = 7,
                    nombreCurso = "Programación",
                    profesor = "Juan Pérez",
                    pruebas = emptyList(),
                    aula = "205"
                )
            )
        )
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
                    //onCursoClick = onScreenSelected
                )
            }
        }

        FloatingActionButton(
            onClick = { onScreenSelected("AgregarCalificacion") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
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
    //onCursoClick: (String) -> Unit,
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
            key = { cursos[it].id }
        ) { index ->
            val curso = cursos[index]
            CursoItem(
                curso = curso,
                onClick = { onScreenSelected("Calificaciones") },
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
            tint = MaterialTheme.colorScheme.primary
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
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Book,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )
            Column {
                Text(
                    text = curso.nombreCurso,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = curso.profesor,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = curso.aula,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}