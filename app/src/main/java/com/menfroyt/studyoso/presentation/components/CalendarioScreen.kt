package com.menfroyt.studyoso.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.time.DayOfWeek
import java.util.Locale
import kotlin.text.get
import kotlin.toString
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.ViewModel.Horario.HorarioViewModel
import com.menfroyt.studyoso.ViewModel.Horario.HorarioViewModelFactory
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.entities.Horario
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.HorarioRepository
import com.menfroyt.studyoso.data.db.AppDatabase


enum class CalendarMode {
    MONTH, WEEK, DAY
}

@Composable
fun CalendarioScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    val horarioRepository = remember { HorarioRepository(db.HorarioDao()) }
    val horarioViewModel: HorarioViewModel = viewModel(factory = HorarioViewModelFactory(horarioRepository))
    val horarios by horarioViewModel.horarios.collectAsState()

    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(
        factory = CursoViewModelFactory(cursoRepository)
    )
    var curso by remember { mutableStateOf<Curso?>(null) }
    val colorCurso = curso?.color ?.toString()


    // Cargar todos los horarios al iniciar
    LaunchedEffect(key1 = Unit) {
        horarioViewModel.cargarTodosLosHorarios()
    }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var calendarMode by remember { mutableStateOf(CalendarMode.MONTH) }

    // Mapeador de días de la semana (inglés -> español)
    val diaMapper = mapOf(
        "MONDAY" to "LUNES",
        "TUESDAY" to "MARTES",
        "WEDNESDAY" to "MIÉRCOLES",
        "THURSDAY" to "JUEVES",
        "FRIDAY" to "VIERNES",
        "SATURDAY" to "SÁBADO",
        "SUNDAY" to "DOMINGO"
    )

    // Filtrar horarios según el día seleccionado
    val diaSeleccionadoEn = selectedDate.dayOfWeek.name
    val diaSeleccionadoEs = diaMapper[diaSeleccionadoEn] ?: diaSeleccionadoEn
    val horariosFiltrados = horarios.filter { it.diaSemana.equals(diaSeleccionadoEs, ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        CalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
            onModeChanged = { calendarMode = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (calendarMode) {
            CalendarMode.MONTH -> CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                horarios = horarios,
                diaMapper = diaMapper,
                cursoViewModel = cursoViewModel
            )
            CalendarMode.WEEK -> SemanaLista(
                horarios = horarios,
                diaMapper = diaMapper,
                cursoViewModel = cursoViewModel
            )
            CalendarMode.DAY -> {
                EventosDiarios(
                    selectedDate = selectedDate,
                    horarios = horariosFiltrados,
                    cursoViewModel = cursoViewModel
                )
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onModeChanged: (CalendarMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mes anterior")
        }
        Text(
            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es"))),
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Mes siguiente")
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onModeChanged(CalendarMode.MONTH)
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3355ff),
                contentColor = Color.White
            )
            ) {
            Text("Mes")
        }
        Button(onClick = { onModeChanged(CalendarMode.WEEK) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3355ff),
                contentColor = Color.White
            )) {
            Text("Semana")
        }
        Button(onClick = { onModeChanged(CalendarMode.DAY) },colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3355ff),
            contentColor = Color.White
        )) {
            Text("Día")
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val daysOfWeek = listOf("Dom","Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    horarios: List<Horario>,
    diaMapper: Map<String, String>,
    cursoViewModel : CursoViewModel
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val start = firstDayOfMonth.with(firstDayOfWeek)

    val daysInCalendar = mutableListOf<LocalDate>()
    var current = start
    while (current.isBefore(lastDayOfMonth.plusDays(1)) || daysInCalendar.size % 7 != 0) {
        daysInCalendar.add(current)
        current = current.plusDays(1)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {
        gridItems(daysInCalendar) { date ->
            DayCell(
                date = date,
                isSelected = date == selectedDate,
                isToday = date == LocalDate.now(),
                isCurrentMonth = date.month == currentMonth.month,
                onDateSelected = onDateSelected
            )
        }
    }

    // Filtrar horarios para el día seleccionado
    val diaSeleccionadoEn = selectedDate.dayOfWeek.name
    val diaSeleccionadoEs = diaMapper[diaSeleccionadoEn] ?: diaSeleccionadoEn
    val horariosFiltrados = horarios.filter { it.diaSemana.equals(diaSeleccionadoEs, ignoreCase = true) }

    EventosDiarios(
        selectedDate = selectedDate,
        horarios = horariosFiltrados,
        cursoViewModel = cursoViewModel

    )
}

@Composable
private fun SemanaLista(
    horarios: List<Horario>,
    diaMapper: Map<String, String>,
    cursoViewModel: CursoViewModel
) {
    val cursosMap = remember { mutableStateMapOf<Int, Curso?>() }
    val hoy = LocalDate.now()
    val inicioSemana = hoy.with(DayOfWeek.MONDAY)

    val diasSemana = (0..6).map { inicioSemana.plusDays(it.toLong()) }
    LaunchedEffect(horarios) {
        horarios.forEach { horario ->
            val curso = cursoViewModel.getCursoById(horario.idCurso)
            cursosMap[horario.idCurso] = curso
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(diasSemana) { dia ->
            val diaEn = dia.dayOfWeek.name
            val diaEs = diaMapper[diaEn] ?: diaEn
            val horariosDia = horarios.filter { it.diaSemana.equals(diaEs, ignoreCase = true) }

            Text(
                text = dia.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es"))),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )

            if (horariosDia.isEmpty()) {
                Text("No hay horarios registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            } else {
                horariosDia.forEach { horario ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(
                                width = 2.dp,
                                color = Color(cursosMap[horario.idCurso]?.color?.toString()?.toColorInt() ?: "#FFBB86FC".toColorInt()),
                                shape = MaterialTheme.shapes.small
                            )
                        ,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = cursosMap[horario.idCurso]?.nombreCurso ?: "Cargando...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${horario.horaInicio} - ${horario.horaFin}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Aula: ${horario.aula}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = ("Modalidad: " + cursosMap[horario.idCurso]?.aula),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    isCurrentMonth: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                when {
                    isSelected -> Color(0xFF2196F3)  // Azul más suave cuando está seleccionado
                    isToday -> Color(0xFFBBDEFB)     // Azul claro para el día actual
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            .clickable { onDateSelected(date) }
            .border(
                width = if (isSelected || isToday) 2.dp else 1.dp,
                color = when {
                    isSelected -> Color(0xFF1976D2)  // Borde más oscuro para el día seleccionado
                    isToday -> Color(0xFF2196F3)     // Borde azul para el día actual
                    else -> Color(0xFFE0E0E0)        // Gris claro para días normales
                },
                shape = CircleShape
            )
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                isToday -> MaterialTheme.colorScheme.onSecondary
                !isCurrentMonth -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                else -> MaterialTheme.colorScheme.onBackground
            }
        )
    }
}

@Composable
private fun EventosDiarios(
    selectedDate: LocalDate,
    horarios: List<Horario>,
    cursoViewModel: CursoViewModel
) {
    // Mapa para almacenar los cursos obtenidos
    val cursosMap = remember { mutableStateMapOf<Int, Curso?>() }
    // Obtener los cursos para todos los horarios mostrados
    LaunchedEffect(horarios) {
        horarios.forEach { horario ->
            val curso = cursoViewModel.getCursoById(horario.idCurso)
            cursosMap[horario.idCurso] = curso
        }
    }
    val colorCurso = cursosMap.values.firstOrNull()?.color?.toString()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Horarios para " + selectedDate.format(
                    DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es"))
                ),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (horarios.isEmpty()) {
                Text("No hay horarios registrados")
            } else {
                LazyColumn(
                    modifier = Modifier.height(200.dp)
                ) {
                    items(horarios) { horario ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color(cursosMap[horario.idCurso]?.color?.toString()?.toColorInt() ?: "#FFBB86FC".toColorInt()),
                                    shape = MaterialTheme.shapes.small
                                ),

                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = cursosMap[horario.idCurso]?.nombreCurso ?: "Cargando...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${horario.horaInicio} - ${horario.horaFin}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Aula: ${horario.aula}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = ("Modalidad: " + cursosMap[horario.idCurso]?.aula),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}