package com.menfroyt.studyoso.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.R
import com.menfroyt.studyoso.ViewModel.Horario.HorarioViewModel
import com.menfroyt.studyoso.ViewModel.Horario.HorarioViewModelFactory
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.HorarioRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


private val IconColor = Color(0xFF6750A4)

@Composable
fun PrincipalScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit
) {

    // Gradiente de fondo moderno
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta de eventos del día con diseño mejorado
            EventosDelDiaCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onViewAllClick = { onScreenSelected("Calendario") },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Menú de opciones con mejor espaciado y animaciones
            MenuOpciones(onScreenSelected)

            Spacer(modifier = Modifier.height(32.dp))

            // Imagen decorativa con mejor presentación
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.study),
                    contentDescription = "Ilustración de estudio",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EventosDelDiaCard(
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val haptic = LocalHapticFeedback.current

    // Repositorios y ViewModels
    val horarioRepository = remember { HorarioRepository(db.HorarioDao()) }
    val horarioViewModel: HorarioViewModel = viewModel(factory = HorarioViewModelFactory(horarioRepository))
    val horarios by horarioViewModel.horarios.collectAsState(initial = emptyList())

    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))

    // Estado para día seleccionado
    var selectedDay by remember { mutableIntStateOf(0) } // 0 = hoy, 1 = mañana

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

    // Tarjeta principal con diseño Material 3 mejorado
    Card(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header de la tarjeta mejorado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Horarios de Hoy",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = fechaSeleccionada.format(
                            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es"))
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onViewAllClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = "Ver todos los eventos en el calendario"
                    }
                ) {
                    Text(
                        text = "Ver todos",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de selección de día
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Botón Hoy
                    FilterChip(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedDay = 0 
                        },
                        label = {
                            Text(
                                text = "Hoy",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        selected = selectedDay == 0,
                        modifier = Modifier.weight(1f)
                    )

                    // Botón Mañana
                    FilterChip(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedDay = 1 
                        },
                        label = {
                            Text(
                                text = "Mañana",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        selected = selectedDay == 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido de horarios
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(),
                exit = fadeOut(animationSpec = tween(300)) + scaleOut()
            ) {
                if (horariosFiltrados.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No hay clases ${if (selectedDay == 0) "hoy" else "mañana"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        horariosFiltrados.take(3).forEach { horario ->
                            val curso = cursosMap[horario.idCurso]
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Indicador de color del curso
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(
                                                Color(curso?.color?.toColorInt() ?: 0xFF6750A4.toInt()),
                                                CircleShape
                                            )
                                    )
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = curso?.nombreCurso ?: "Curso desconocido",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${horario.horaInicio} - ${horario.horaFin}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                        )
                                        if (horario.aula.isNotBlank()) {
                                            Text(
                                                text = "Aula: ${horario.aula}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (horariosFiltrados.size > 3) {
                            Text(
                                text = "+ ${horariosFiltrados.size - 3} clases más",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuOpciones(onScreenSelected: (String) -> Unit) {
    val haptic = LocalHapticFeedback.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título de la sección
        Text(
            text = "Accesos rápidos",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp)
        )
        
        // Primera fila de opciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItemModern(
                icon = Icons.Filled.AddToPhotos,
                text = "Cursos",
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onScreenSelected("lisCurso") 
                },
                modifier = Modifier.weight(1f)
            )
            MenuItemModern(
                icon = Icons.Filled.CalendarMonth,
                text = "Calendario",
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onScreenSelected("Calendario") 
                },
                modifier = Modifier.weight(1f)
            )
            MenuItemModern(
                icon = Icons.Filled.FormatListNumbered,
                text = "Tareas",
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onScreenSelected("ListTaskScreen") 
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Segunda fila de opciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItemModern(
                icon = Icons.Filled.Timer,
                text = "Pomodoro",
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onScreenSelected("Pomodoro") 
                },
                modifier = Modifier.weight(1f)
            )
            MenuItemModern(
                icon = Icons.Filled.Dataset,
                text = "Matriz",
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onScreenSelected("MatrizEisenhower") 
                },
                modifier = Modifier.weight(1f)
            )
            // Espaciador para mantener la alineación
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun MenuItemModern(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "menu_item_scale"
    )

    Card(
        modifier = modifier
            .padding(4.dp)
            .scale(scale)
            .clickable { 
                onClick()
            }
            .semantics {
                contentDescription = "Navegar a $text"
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
