package com.menfroyt.studyoso.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
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
    
    // Estados de la interfaz
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var calendarMode by remember { mutableStateOf(CalendarMode.MONTH) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Configuración de animaciones
    val transition = updateTransition(
        targetState = calendarMode, 
        label = "calendarModeTransition"
    )
    
    val slideAnimation by transition.animateFloat(
        transitionSpec = { 
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "slideAnimation"
    ) { mode ->
        when (mode) {
            CalendarMode.MONTH -> 0f
            CalendarMode.WEEK -> 1f
            CalendarMode.DAY -> 2f
        }
    }

    // Cargar todos los horarios al iniciar
    LaunchedEffect(key1 = Unit) {
        horarioViewModel.cargarTodosLosHorarios()
        delay(500) // Simular carga para mostrar loading
        isLoading = false
    }

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

    // Estado de configuración
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        if (isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = if (isTablet) 24.dp else 16.dp,
                        vertical = 16.dp
                    )
            ) {
                CalendarHeader(
                    currentMonth = currentMonth,
                    calendarMode = calendarMode,
                    isTablet = isTablet,
                    onPreviousMonth = { 
                        currentMonth = currentMonth.minusMonths(1)
                    },
                    onNextMonth = { 
                        currentMonth = currentMonth.plusMonths(1)
                    },
                    onModeChanged = { mode -> 
                        calendarMode = mode
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedContent(
                    targetState = calendarMode,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> 
                                if (targetState.ordinal > initialState.ordinal) fullWidth else -fullWidth 
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { fullWidth -> 
                                if (targetState.ordinal > initialState.ordinal) -fullWidth else fullWidth 
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    },
                    label = "calendar_content"
                ) { mode ->
                    when (mode) {
                        CalendarMode.MONTH -> CalendarGrid(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it },
                            horarios = horarios,
                            diaMapper = diaMapper,
                            cursoViewModel = cursoViewModel,
                            isTablet = isTablet
                        )
                        CalendarMode.WEEK -> SemanaLista(
                            horarios = horarios,
                            diaMapper = diaMapper,
                            cursoViewModel = cursoViewModel,
                            selectedDate = selectedDate,
                            isTablet = isTablet
                        )
                        CalendarMode.DAY -> EventosDiarios(
                            selectedDate = selectedDate,
                            horarios = horariosFiltrados,
                            cursoViewModel = cursoViewModel,
                            isTablet = isTablet
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    calendarMode: CalendarMode,
    isTablet: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onModeChanged: (CalendarMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Row para navegación de mes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .size(if (isTablet) 48.dp else 40.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    )
                    .semantics { contentDescription = "Mes anterior" }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentMonth.format(
                        DateTimeFormatter.ofPattern("MMMM", Locale("es"))
                    ).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currentMonth.year.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .size(if (isTablet) 48.dp else 40.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    )
                    .semantics { contentDescription = "Mes siguiente" }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de modo de vista mejorado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(12.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalendarModeButton(
                mode = CalendarMode.MONTH,
                currentMode = calendarMode,
                icon = Icons.Filled.CalendarMonth,
                text = "Mes",
                isTablet = isTablet,
                onModeChanged = onModeChanged
            )
            CalendarModeButton(
                mode = CalendarMode.WEEK,
                currentMode = calendarMode,
                icon = Icons.Filled.CalendarViewWeek,
                text = "Semana",
                isTablet = isTablet,
                onModeChanged = onModeChanged
            )
            CalendarModeButton(
                mode = CalendarMode.DAY,
                currentMode = calendarMode,
                icon = Icons.Filled.Today,
                text = "Día",
                isTablet = isTablet,
                onModeChanged = onModeChanged
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Encabezados de días de la semana mejorados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
            daysOfWeek.forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.CalendarModeButton(
    mode: CalendarMode,
    currentMode: CalendarMode,
    icon: ImageVector,
    text: String,
    isTablet: Boolean,
    onModeChanged: (CalendarMode) -> Unit
) {
    val isSelected = mode == currentMode
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale_animation"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .scale(animatedScale)
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onModeChanged(mode) }
            .padding(
                horizontal = if (isTablet) 16.dp else 12.dp,
                vertical = if (isTablet) 12.dp else 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(if (isTablet) 20.dp else 16.dp)
            )
            if (isTablet) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Text(
                text = "Cargando calendario...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    cursoViewModel: CursoViewModel,
    isTablet: Boolean
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Espacios en blanco para el primer día del mes
        items(firstDayOfWeek) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(2.dp)
            )
        }

        // Días del mes
        items(daysInMonth) { dayIndex ->
            val day = dayIndex + 1
            val date = currentMonth.atDay(day)
            val isSelected = date == selectedDate
            val isToday = date == LocalDate.now()
            
            // Obtener eventos para este día
            val diaEnString = date.dayOfWeek.name
            val diaEspanol = diaMapper[diaEnString] ?: diaEnString
            val eventosDelDia = horarios.filter { 
                it.diaSemana.equals(diaEspanol, ignoreCase = true) 
            }
            
            CalendarDayCell(
                day = day,
                isSelected = isSelected,
                isToday = isToday,
                hasEvents = eventosDelDia.isNotEmpty(),
                eventCount = eventosDelDia.size,
                isTablet = isTablet,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasEvents: Boolean,
    eventCount: Int,
    isTablet: Boolean,
    onClick: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "day_scale"
    )

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(animatedScale)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isToday && !isSelected) 2.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isTablet) 16.sp else 14.sp
                ),
                color = textColor
            )
            
            if (hasEvents) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    repeat(minOf(eventCount, 3)) {
                        Box(
                            modifier = Modifier
                                .size(if (isTablet) 6.dp else 4.dp)
                                .background(
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    shape = CircleShape
                                )
                        )
                    }
                    if (eventCount > 3) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            fontSize = if (isTablet) 10.sp else 8.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SemanaLista(
    horarios: List<Horario>,
    diaMapper: Map<String, String>,
    cursoViewModel: CursoViewModel,
    selectedDate: LocalDate,
    isTablet: Boolean
) {
    val weekStart = selectedDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
    val diasSemana = (0..6).map { weekStart.plusDays(it.toLong()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(diasSemana) { fecha ->
            val diaEnIngles = fecha.dayOfWeek.name
            val diaEspanol = diaMapper[diaEnIngles] ?: diaEnIngles
            val horariosDia = horarios.filter { 
                it.diaSemana.equals(diaEspanol, ignoreCase = true) 
            }
            
            WeekDayCard(
                fecha = fecha,
                horarios = horariosDia,
                cursoViewModel = cursoViewModel,
                isTablet = isTablet,
                isSelected = fecha == selectedDate
            )
        }
    }
}

@Composable
private fun WeekDayCard(
    fecha: LocalDate,
    horarios: List<Horario>,
    cursoViewModel: CursoViewModel,
    isTablet: Boolean,
    isSelected: Boolean
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = tween(300),
        label = "card_elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
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
                    text = fecha.format(
                        DateTimeFormatter.ofPattern("EEEE d", Locale("es"))
                    ).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                if (horarios.isNotEmpty()) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = horarios.size.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            if (horarios.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                horarios.forEach { horario ->
                    HorarioItem(
                        horario = horario,
                        cursoViewModel = cursoViewModel,
                        isTablet = isTablet
                    )
                }
            } else {
                Text(
                    text = "Sin eventos programados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EventosDiarios(
    selectedDate: LocalDate,
    horarios: List<Horario>,
    cursoViewModel: CursoViewModel,
    isTablet: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Header del día
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = selectedDate.format(
                        DateTimeFormatter.ofPattern("EEEE", Locale("es"))
                    ).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = selectedDate.format(
                        DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", Locale("es"))
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (horarios.isNotEmpty()) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.scale(1.2f)
                ) {
                    Text(
                        text = "${horarios.size} evento${if (horarios.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de eventos
        if (horarios.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(horarios.sortedBy { it.horaInicio }) { horario ->
                    EnhancedHorarioCard(
                        horario = horario,
                        cursoViewModel = cursoViewModel,
                        isTablet = isTablet
                    )
                }
            }
        } else {
            EmptyStateCard(isTablet = isTablet)
        }
    }
}

@Composable
private fun HorarioItem(
    horario: Horario,
    cursoViewModel: CursoViewModel,
    isTablet: Boolean
) {
    var curso by remember { mutableStateOf<Curso?>(null) }
    
    LaunchedEffect(horario.idCurso) {
        curso = cursoViewModel.getCursoById(horario.idCurso)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicador de color del curso
        Box(
            modifier = Modifier
                .size(if (isTablet) 16.dp else 12.dp)
                .background(
                    color = try {
                        Color(curso?.color?.toInt() ?: 0xFF6200EE.toInt())
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.primary
                    },
                    shape = CircleShape
                )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = curso?.nombreCurso ?: "Curso desconocido",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${horario.horaInicio} - ${horario.horaFin}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (horario.aula.isNotBlank()) {
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = horario.aula,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}

@Composable
private fun EnhancedHorarioCard(
    horario: Horario,
    cursoViewModel: CursoViewModel,
    isTablet: Boolean
) {
    var curso by remember { mutableStateOf<Curso?>(null) }
    
    LaunchedEffect(horario.idCurso) {
        curso = cursoViewModel.getCursoById(horario.idCurso)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra lateral de color
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(if (isTablet) 60.dp else 48.dp)
                    .background(
                        color = try {
                            Color(curso?.color?.toInt() ?: 0xFF6200EE.toInt())
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        },
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = curso?.nombreCurso ?: "Curso desconocido",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${horario.horaInicio} - ${horario.horaFin}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (horario.aula.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = horario.aula,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Indicador de tiempo
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val now = java.time.LocalTime.now()
                val startTime = java.time.LocalTime.parse(horario.horaInicio)
                val endTime = java.time.LocalTime.parse(horario.horaFin)
                
                val status = when {
                    now.isBefore(startTime) -> "Próximo"
                    now.isAfter(endTime) -> "Finalizado"
                    else -> "En curso"
                }
                
                val statusColor = when (status) {
                    "Próximo" -> MaterialTheme.colorScheme.primary
                    "En curso" -> Color(0xFF4CAF50)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                }
                
                Badge(
                    containerColor = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(isTablet: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 32.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.EventBusy,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(if (isTablet) 64.dp else 48.dp)
            )
            Text(
                text = "Sin eventos para hoy",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = "¡Perfecto momento para relajarse o planificar nuevas actividades!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}