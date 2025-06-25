package com.menfroyt.studyoso.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import java.util.Locale
import androidx.core.graphics.toColorInt
import kotlin.collections.get
import kotlin.text.get
import com.menfroyt.studyoso.R
import com.menfroyt.studyoso.ViewModel.Horario.HorarioViewModel
import com.menfroyt.studyoso.ViewModel.Horario.HorarioViewModelFactory
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.HorarioRepository

// Constante para el color de los íconos
private val IconColor = Color(0xFF3355ff)


@Composable
fun PrincipalScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
        ,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        EventosDelDiaCard(
            modifier = Modifier.fillMaxWidth(),
            onViewAllClick = { onScreenSelected("Calendario") },
        )

        Spacer(modifier = Modifier.height(16.dp))
        MenuOpciones(onScreenSelected)

        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.study),
            contentDescription = "Imagen de fondo",
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun EventosDelDiaCard(
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    // Repositorios y ViewModels
    val horarioRepository = remember { HorarioRepository(db.HorarioDao()) }
    val horarioViewModel: HorarioViewModel = viewModel(factory = HorarioViewModelFactory(horarioRepository))
    val horarios by horarioViewModel.horarios.collectAsState(initial = emptyList())

    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))

    // Estado para día seleccionado y expansión
    var selectedDay by remember { mutableIntStateOf(0) } // 0 = hoy, 1 = mañana
    var expandido by remember { mutableStateOf(false) }

    // Obtener fechas
    val hoy = LocalDate.now()
    val manana = hoy.plusDays(1)
    val fechaSeleccionada = if (selectedDay == 0) hoy else manana

    // Mapeo de días de la semana
    val diaMapper = mapOf(
        "MONDAY" to "LUNES",
        "TUESDAY" to "MARTES",
        "WEDNESDAY" to "MIÉRCOLES",
        "THURSDAY" to "JUEVES",
        "FRIDAY" to "VIERNES",
        "SATURDAY" to "SÁBADO",
        "SUNDAY" to "DOMINGO"
    )

    // Obtener día en español
    val diaEn = fechaSeleccionada.dayOfWeek.name
    val diaEs = diaMapper[diaEn] ?: diaEn

    // Filtrar horarios para el día seleccionado
    val horariosFiltrados = horarios.filter {
        it.diaSemana.equals(diaEs, ignoreCase = true)
    }.sortedBy { it.horaInicio }

    // Estado para almacenar cursos
    val cursosMap = remember { mutableStateMapOf<Int, Curso?>() }

    // Cargar todos los horarios al iniciar
    LaunchedEffect(Unit) {
        horarioViewModel.cargarTodosLosHorarios()
    }

    // Cargar cursos para los horarios filtrados
    LaunchedEffect(horariosFiltrados) {
        horariosFiltrados.forEach { horario ->
            if (!cursosMap.containsKey(horario.idCurso)) {
                val curso = cursoViewModel.getCursoById(horario.idCurso)
                cursosMap[horario.idCurso] = curso
            }
        }
    }

    // Calcular la altura dinámica
    val alturaBase = 5.dp  // Altura mínima para el encabezado y botones
    val alturaMaxima = 168.dp  // Altura máxima permitida
    val alturaItem = 76.dp  // Altura aproximada de cada item de horario

    // Calcular altura según contenido
    val alturaContenido = when {
        horariosFiltrados.isEmpty() -> if (expandido) 90.dp else 10.dp
        else -> minOf(alturaMaxima, alturaBase + (horariosFiltrados.size * alturaItem.value).dp)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { expandido = !expandido },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            // Encabezado de la tarjeta
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Eventos del día",
                    tint = Color(0xFF3355ff),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Horario para ${if (selectedDay == 0) "hoy" else "mañana"}:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = onViewAllClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "ver todos",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF3355ff),
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Ver todos los eventos",
                        tint = Color(0xFF3355ff)
                    )
                }
            }

            // Botones de selección de día (solo visibles si está expandido o hay horarios)
            AnimatedVisibility(visible = expandido || horariosFiltrados.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Botón Hoy
                    Button(
                        onClick = { selectedDay = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDay == 0)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (selectedDay == 0)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Hoy, " + hoy.format(
                                DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es"))
                            ),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Botón Mañana
                    Button(
                        onClick = { selectedDay = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDay == 1)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (selectedDay == 1)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Mañana, " + manana.format(
                                DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es"))
                            ),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido dinámico
            AnimatedVisibility(visible = expandido || horariosFiltrados.isNotEmpty()) {
                if (horariosFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No hay horarios para ${if (selectedDay == 0) "hoy" else "mañana"}",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(alturaContenido - alturaBase)
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(horariosFiltrados.size) { index ->
                            val horario = horariosFiltrados[index]
                            val curso = cursosMap[horario.idCurso]
                            val colorCurso = curso?.color ?.toString()

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .border(
                                        width = 2.dp,
                                        color = Color(colorCurso?.toColorInt() ?: "#FFBB86FC".toColorInt()),
                                        shape = MaterialTheme.shapes.small
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Horario
                                    Column(
                                        modifier = Modifier.width(90.dp)
                                    ) {
                                        Text(
                                            text = horario.horaInicio,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = horario.horaFin,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // Información del curso
                                    Column(
                                        modifier = Modifier.weight(1f)

                                    ) {
                                        Text(
                                            text = curso?.nombreCurso ?: "Cargando...",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                            ,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Modalidad: ${curso?.aula ?: "Desconocida"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Text(
                                            text = "Aula: ${horario.aula}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun MenuOpciones(onScreenSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MenuItem(
            icon = Icons.Filled.AddToPhotos,
            text = "Cursos",
            onClick = { onScreenSelected("lisCurso") }
        )
        MenuItem(
            icon = Icons.Filled.CalendarMonth,
            text = "Calendario",
            onClick = { onScreenSelected("Calendario") }
        )
        MenuItem(
            icon = Icons.Filled.FormatListNumbered,
            text = "Tareas",
            onClick = { onScreenSelected("ListTaskScreen") }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MenuItem(
            icon = Icons.Filled.Timer,
            text = "Pomodoro",
            onClick = { onScreenSelected("Pomodoro") }
        )
        MenuItem(
            icon = Icons.Filled.Dataset,
            text = "Matriz Eisenhower",
            onClick = { onScreenSelected("MatrizEisenhower") }
        )
        MenuItem(
            icon = Icons.Filled.AllInclusive,
            text = "...",
            onClick = { "" }
        )
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(65.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = IconColor,
                modifier = Modifier.size(60.dp)
            )
        }
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}