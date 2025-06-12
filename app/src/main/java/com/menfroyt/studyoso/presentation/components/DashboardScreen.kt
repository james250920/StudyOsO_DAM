package esan.mendoza.teststudyoso.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import esan.mendoza.teststudyoso.ViewModel.calificacion.CalificacionViewModel
import esan.mendoza.teststudyoso.ViewModel.calificacion.CalificacionViewModelFactory
import esan.mendoza.teststudyoso.ViewModel.curso.CursoViewModel
import esan.mendoza.teststudyoso.ViewModel.curso.CursoViewModelFactory
import esan.mendoza.teststudyoso.ViewModel.tarea.TareaViewModel
import esan.mendoza.teststudyoso.ViewModel.tarea.TareaViewModelFactory
import esan.mendoza.teststudyoso.data.db.AppDatabase
import esan.mendoza.teststudyoso.data.entities.Tarea
import esan.mendoza.teststudyoso.data.repositories.CalificacionRepository
import esan.mendoza.teststudyoso.data.repositories.CursoRepository
import esan.mendoza.teststudyoso.data.repositories.TareaRepository

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    usuarioId: Int
) {
    val context = LocalContext.current

    // Instanciar DB y repositorios
    val db = remember { AppDatabase.getInstance(context) }
    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val tareaRepository = remember { TareaRepository(db.TareaDao()) }
    val calificacionRepository = remember { CalificacionRepository(db.CalificacionDao()) }

    // Instanciar ViewModels con Factory
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))
    val tareaViewModel: TareaViewModel = viewModel(factory = TareaViewModelFactory(tareaRepository))
    val calificacionViewModel: CalificacionViewModel = viewModel(factory = CalificacionViewModelFactory(calificacionRepository))

    // Observar estados
    val cursos by cursoViewModel.cursos.collectAsState()
    val tareas by tareaViewModel.tareas.collectAsState()
    val calificaciones by calificacionViewModel.calificaciones.collectAsState()

    // Cargar datos al iniciar
    LaunchedEffect(usuarioId) {
        cursoViewModel.cargarCursos(usuarioId)
        tareaViewModel.cargarTareasPorUsuario(usuarioId)
        calificacionViewModel.cargarCalificacionesPorUsuario(usuarioId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardCard(
                    title = "Tareas Pendientes",
                    value = tareas.size.toString(),
                    icon = Icons.Filled.Assignment,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onScreenSelected("ListTaskScreen") }
                )
            }

            item {
                DashboardCard(
                    title = "Promedio General",
                    value = if (calificaciones.isNotEmpty()) {
                        val promedioGeneral = calificaciones.mapNotNull { it.calificacionObtenida }.average()
                        String.format("%.2f", promedioGeneral)
                    } else "0.0",
                    icon = Icons.Filled.Grade,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { onScreenSelected("ListCalificaciones") }
                )
            }

            item {
                DashboardCard(
                    title = "Cursos Activos",
                    value = cursos.size.toString(),
                    icon = Icons.Filled.School,
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = { onScreenSelected("lisCurso") }
                )
            }

            item {
                DashboardCard(
                    title = "Pomodoros Completados",
                    value = "0", // Aquí puedes agregar lógica para contar pomodoros si tienes datos
                    icon = Icons.Filled.Timer,
                    color = MaterialTheme.colorScheme.error,
                    onClick = { onScreenSelected("Pomodoro") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ProximasTareasCard(
            modifier = Modifier.fillMaxWidth(),
            tareas = tareas,
            onVerMasClick = { onScreenSelected("ListTaskScreen") }
        )
    }
}

@Composable
private fun DashboardCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ProximasTareasCard(
    modifier: Modifier = Modifier,
    tareas: List<Tarea>,
    onVerMasClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Próximas Tareas",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onVerMasClick) {
                    Text("Ver más")
                }
            }

            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                items(count = minOf(tareas.size, 3)) { index ->
                    val tarea = tareas[index]
                    ListItem(
                        headlineContent = { Text(text = tarea.descripcion) },
                        supportingContent = { Text(text = tarea.fechaVencimiento ?: "") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Checklist,
                                contentDescription = null
                            )
                        }
                    )
                }

            }
        }
    }
}
