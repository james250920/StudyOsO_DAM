package com.menfroyt.studyoso.presentation.calificacion

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
import kotlin.toString


@Composable
fun ListCalificacionScreen(
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
                )
            }
        }

        FloatingActionButton(
            onClick = { onScreenSelected("AgregarCalificacion") },
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
                onClick = { onScreenSelected("DetalleCalificaciones/${curso.idCurso}") }, // <--- Aquí va el cambio
                onDelete = { onDeleteCurso(curso) },
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
                    text = curso.nombreCurso,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = curso.profesor.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = curso.aula.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
    }
}
