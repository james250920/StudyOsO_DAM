package com.menfroyt.studyoso.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModelFactory
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.ViewModel.tarea.TareaViewModel
import com.menfroyt.studyoso.ViewModel.tarea.TareaViewModelFactory
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Calificacion
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.entities.Tarea
import com.menfroyt.studyoso.data.repositories.CalificacionRepository
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.TareaRepository
import com.menfroyt.studyoso.data.repositories.UsuarioRepository
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModel
import com.menfroyt.studyoso.data.entities.Usuario

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    usuarioId: Int
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Estados para animaciones
    var isVisible by remember { mutableStateOf(false) }
    var selectedCard by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Instanciar DB y repositorios
    val db = remember { AppDatabase.getInstance(context) }
    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val tareaRepository = remember { TareaRepository(db.TareaDao()) }
    val calificacionRepository = remember { CalificacionRepository(db.CalificacionDao()) }
    val usuarioRepository = remember { UsuarioRepository(db.UsuarioDao()) }
    // Instanciar ViewModels con Factory
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))
    val tareaViewModel: TareaViewModel = viewModel(factory = TareaViewModelFactory(tareaRepository))
    val calificacionViewModel: CalificacionViewModel = viewModel(factory = CalificacionViewModelFactory(calificacionRepository))
    val usuarioViewModel: UsuarioViewModel = viewModel(factory = UsuarioViewModelFactory(usuarioRepository))
    // Observar estados
    val cursos by cursoViewModel.cursos.collectAsState()
    val tareas by tareaViewModel.tareas.collectAsState()
    val calificaciones by calificacionViewModel.calificaciones.collectAsState()
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Cargar datos al iniciar
    LaunchedEffect(usuarioId) {
        cursoViewModel.cargarCursos(usuarioId)
        tareaViewModel.cargarTareasPorUsuario(usuarioId)
        calificacionViewModel.cargarCalificacionesPorUsuario(usuarioId)

        usuarioViewModel.getUsuarioAutenticado(
            id = usuarioId,
            onSuccess = {
                usuario = it
                loading = false
            },
            onError = {
                errorMessage = it
                loading = false
            }
        )

    }

    // Gradiente de fondo
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (isLandscape) 32.dp else 20.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header animado
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(600))
                ) {
                    DashboardHeader(
                        isLandscape = isLandscape,
                        usuario = usuario?.nombre ?: "Usuario",

                    )
                }
            }

            // Cards de estadísticas animadas
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(animationSpec = tween(700, 200))
                ) {
                    ModernStatsGrid(
                        cursos = cursos,
                        tareas = tareas,
                        calificaciones = calificaciones,
                        onScreenSelected = onScreenSelected,
                        selectedCard = selectedCard,
                        onCardSelected = { selectedCard = it },
                        isLandscape = isLandscape
                    )
                }
            }

            // Sección de progreso académico
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(animationSpec = tween(800, 400))
                ) {
                    AcademicProgressCard(
                        cursos = cursos,
                        calificaciones = calificaciones,
                        isLandscape = isLandscape
                    )
                }
            }

            // Próximas tareas mejoradas
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(animationSpec = tween(900, 600))
                ) {
                    ModernProximasTareasCard(
                        tareas = tareas,
                        onVerMasClick = { onScreenSelected("ListTaskScreen") },
                        onTareaClick = { /* Navegar a detalle de tarea */ },
                        isLandscape = isLandscape
                    )
                }
            }

            // Acciones rápidas
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(animationSpec = tween(1000, 800))
                ) {
                    QuickActionsDashboard(
                        onScreenSelected = onScreenSelected,
                        isLandscape = isLandscape
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    isLandscape: Boolean,
    usuario: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Saludo personalizado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "¡Hola! ${usuario}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Bienvenido a tu dashboard",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Indicador de fecha
            Surface(
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Today,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hoy",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernStatsGrid(
    cursos: List<Curso>,
    tareas: List<Tarea>,
    calificaciones: List<Calificacion>,
    onScreenSelected: (String) -> Unit,
    selectedCard: String?,
    onCardSelected: (String) -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val promedioGeneral = if (calificaciones.isNotEmpty()) {
        calificaciones.mapNotNull { it.calificacionObtenida }.average()
    } else 0.0

    val tareasUrgentes = tareas.filter {

        // Aquí podrías filtrar por fecha de vencimiento próxima
        true 
    }.size

    val statsData = listOf(
        StatsCardData(
            id = "tareas",
            title = "Tareas Pendientes",
            value = tareas.size.toString(),
            subtitle = if (tareasUrgentes > 0) "${tareasUrgentes} urgentes" else "Al día",
            icon = Icons.Filled.Assignment,
            gradient = listOf(
                Color(0xFF2196F3), 
                Color(0xFF21CBF3)
            ),
            route = "ListTaskScreen"
        ),
        StatsCardData(
            id = "promedio",
            title = "Promedio General",
            value = String.format("%.1f", promedioGeneral),
            subtitle = when {
                promedioGeneral >= 18.0 -> "Excelente"
                promedioGeneral >= 16.0 -> "Muy bueno"
                promedioGeneral >= 14.0 -> "Bueno"
                else -> "Mejorando"
            },
            icon = Icons.Filled.Grade,
            gradient = listOf(
                Color(0xFFFF9800), 
                Color(0xFFFFB74D)
            ),
            route = "ListCalificaciones"
        ),
        StatsCardData(
            id = "cursos",
            title = "Cursos Activos",
            value = cursos.size.toString(),
            subtitle = "${cursos.size} materias",
            icon = Icons.Filled.School,
            gradient = listOf(
                Color(0xFF9C27B0), 
                Color(0xFFBA68C8)
            ),
            route = "lisCurso"
        ),
        StatsCardData(
            id = "progreso",
            title = "Progreso Semanal",
            value = "85%",
            subtitle = "Objetivo cumplido",
            icon = Icons.Filled.TrendingUp,
            gradient = listOf(
                Color(0xFF4CAF50), 
                Color(0xFF81C784)
            ),
            route = "progreso"
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(if (isLandscape) 4 else 2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(if (isLandscape) 180.dp else 320.dp)
    ) {
        items(statsData.size) { index ->
            val stats = statsData[index]
            
            var cardScale by remember { mutableStateOf(0.8f) }
            
            LaunchedEffect(Unit) {
                delay(index * 100L)
                cardScale = 1f
            }
            
            ModernStatsCard(
                data = stats,
                isSelected = selectedCard == stats.id,
                onCardClick = {
                    onCardSelected(stats.id)
                    if (stats.route != "progreso") {
                        onScreenSelected(stats.route)
                    }
                },
                modifier = Modifier.scale(
                    animateFloatAsState(
                        targetValue = cardScale,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ).value
                )
            )
        }
    }
}

@Composable
private fun ModernStatsCard(
    data: StatsCardData,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onCardClick() }
            .semantics { contentDescription = "${data.title}: ${data.value}" },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 12.dp else 6.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Gradiente de fondo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = data.gradient.map { it.copy(alpha = 0.1f) }
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icono
                Surface(
                    modifier = Modifier.size(40.dp),
                    color = data.gradient[0].copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(8.dp),
                        tint = data.gradient[0]
                    )
                }
                
                // Contenido
                Column {
                    Text(
                        text = data.value,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = data.gradient[0]
                    )
                    
                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = data.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Indicador de selección
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(8.dp)
                        .background(
                            data.gradient[0],
                            CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun AcademicProgressCard(
    cursos: List<Curso>,
    calificaciones: List<Calificacion>,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Analytics,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Progreso Académico",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Rendimiento general",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progreso visual
            if (calificaciones.isNotEmpty()) {
                val promedio = calificaciones.mapNotNull { it.calificacionObtenida }.average()
                val progresoNormalizado = (promedio / 20.0).coerceIn(0.0, 1.0)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Promedio: ${String.format("%.1f", promedio)}/20",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = progresoNormalizado.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = when {
                                promedio >= 18.0 -> Color(0xFF4CAF50)
                                promedio >= 16.0 -> Color(0xFF2196F3)
                                promedio >= 14.0 -> Color(0xFFFF9800)
                                else -> Color(0xFFE91E63)
                            },
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            } else {
                Text(
                    text = "No hay calificaciones registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ModernProximasTareasCard(
    tareas: List<Tarea>,
    onVerMasClick: () -> Unit,
    onTareaClick: (Tarea) -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Assignment,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Próximas Tareas",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "${tareas.size} tareas pendientes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                FilledTonalButton(
                    onClick = onVerMasClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver todas")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de tareas
            if (tareas.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.height(if (isLandscape) 180.dp else 240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = tareas.take(5)
                    ) { tarea ->
                        ModernTareaItem(
                            tarea = tarea,
                            onClick = { onTareaClick(tarea) }
                        )
                    }
                    
                    if (tareas.size > 3) {
                        item {
                            Text(
                                text = "Y ${tareas.size - 3} tareas más...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { onVerMasClick() }
                            )
                        }
                    }
                }
            } else {
                // Estado vacío
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AssignmentTurnedIn,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "¡Todas las tareas completadas!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Excelente trabajo manteniéndote al día",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernTareaItem(
    tarea: com.menfroyt.studyoso.data.entities.Tarea,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de prioridad/estado
            Surface(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contenido de la tarea
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tarea.descripcion,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (!tarea.fechaVencimiento.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = tarea.fechaVencimiento!!,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Flecha indicadora
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun QuickActionsDashboard(
    onScreenSelected: (String) -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Speed,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botones de acción
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickActions.forEach { action ->
                        QuickActionButton(
                            action = action,
                            onAction = { onScreenSelected(action.route) },
                            modifier = Modifier.weight(1f),
                            isCompact = true
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickActions.forEach { action ->
                        QuickActionButton(
                            action = action,
                            onAction = { onScreenSelected(action.route) },
                            modifier = Modifier.fillMaxWidth(),
                            isCompact = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    action: QuickActionData,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Surface(
        modifier = modifier
            .clickable { onAction() }
            .semantics { contentDescription = "Botón de ${action.title}" },
        color = action.color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            action.color.copy(alpha = 0.2f)
        )
    ) {
        if (isCompact) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = action.color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = action.color,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    color = action.color.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(8.dp),
                        tint = action.color
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = action.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// Clases de datos para el dashboard moderno
private data class StatsCardData(
    val id: String,
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val route: String
)

private data class QuickActionData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

// Acciones rápidas disponibles
private val quickActions = listOf(
    QuickActionData(

        title = "Agregar Tarea",
        description = "Crear nueva tarea",
        icon = Icons.Filled.Add,
        color = Color(0xFF2196F3),
        route = "AddTaskScreen"
    ),
    QuickActionData(
        title = "Nuevo Curso",
        description = "Registrar curso",
        icon = Icons.Filled.LibraryAdd,
        color = Color(0xFF9C27B0),
        route = "AgregarCursos"
    ),
    QuickActionData(
        title = "Calificación",
        description = "Registrar nota",
        icon = Icons.Filled.Grade,
        color = Color(0xFFFF9800),
        route = "AgregarCalificacion"
    ),
    QuickActionData(
        title = "Calendario",
        description = "Ver horarios",
        icon = Icons.Filled.CalendarMonth,
        color = Color(0xFF4CAF50),
        route = "Calendario"
    )
)
