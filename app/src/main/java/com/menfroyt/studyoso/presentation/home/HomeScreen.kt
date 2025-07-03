package com.menfroyt.studyoso.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.menfroyt.studyoso.navigation.DrawerContent
import kotlinx.coroutines.launch
import com.menfroyt.studyoso.presentation.calificacion.AgregarCalificacionScreen
import com.menfroyt.studyoso.R
import com.menfroyt.studyoso.presentation.calificacion.DetalleCalificacionesScreen
import com.menfroyt.studyoso.presentation.calificacion.ListCalificacionScreen
import com.menfroyt.studyoso.presentation.calificacion.SimuladorCalificacionesScreen
import com.menfroyt.studyoso.presentation.components.DashboardScreen
import com.menfroyt.studyoso.presentation.curso.AgregarCursosScreen
import com.menfroyt.studyoso.presentation.curso.DetalleCursoScreen
import com.menfroyt.studyoso.presentation.curso.ListCursoScreen
import com.menfroyt.studyoso.presentation.tarea.AddTaskScreen
import com.menfroyt.studyoso.presentation.tarea.ListTaskScreen
import com.menfroyt.studyoso.presentation.usuario.PerfilScreen
import com.menfroyt.studyoso.presentation.components.CalendarioScreen
import com.menfroyt.studyoso.presentation.components.GuiaScreen
import com.menfroyt.studyoso.presentation.components.MatrizEisenhowerScreen
import com.menfroyt.studyoso.presentation.components.pomodoro.PomodoroScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    usuarioId: Int,
    initialScreen: String = "Principal"
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedScreen by remember { mutableStateOf(initialScreen) }
    var isDarkTheme by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    
    // Animaciones para transiciones suaves
    val screenTransition by animateFloatAsState(
        targetValue = if (selectedScreen == "Principal") 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "screen_transition"
    )

    // Paleta de colores mejorada con Material Design 3
    MaterialTheme(
        colorScheme = if (isDarkTheme) {
            darkColorScheme(
                primary = Color(0xFF6750A4),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFEADDFF),
                onPrimaryContainer = Color(0xFF21005D),
                secondary = Color(0xFF625B71),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFE8DEF8),
                onSecondaryContainer = Color(0xFF1D192B),
                surface = Color(0xFF141218),
                onSurface = Color(0xFFE6E0E9),
                surfaceVariant = Color(0xFF49454F),
                onSurfaceVariant = Color(0xFFCAC4D0),
                background = Color(0xFF101014),
                onBackground = Color(0xFFE6E0E9),
                outline = Color(0xFF938F99),
                outlineVariant = Color(0xFF49454F)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF6750A4),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFEADDFF),
                onPrimaryContainer = Color(0xFF21005D),
                secondary = Color(0xFF625B71),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFE8DEF8),
                onSecondaryContainer = Color(0xFF1D192B),
                surface = Color(0xFFFEF7FF),
                onSurface = Color(0xFF1D1B20),
                surfaceVariant = Color(0xFFE7E0EC),
                onSurfaceVariant = Color(0xFF49454F),
                background = Color(0xFFFEF7FF),
                onBackground = Color(0xFF1D1B20),
                outline = Color(0xFF79747E),
                outlineVariant = Color(0xFFCAC4D0)
            )
        }
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.semantics {
                        contentDescription = "Menú de navegación principal"
                    }
                ) {
                    DrawerContent(
                        selectedScreen = selectedScreen,
                        onOptionSelected = { option ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            when (option) {
                                "Home" -> selectedScreen = "Principal"
                                "Guia" -> selectedScreen = "Guia"
                                "Perfil" -> selectedScreen = "Perfil"
                                "Dashboard" -> selectedScreen = "Dashboard"
                                "Calendario" -> selectedScreen = "Calendario"
                                "Pomodoro" -> selectedScreen = "Pomodoro"
                                "Cerrar Sesión" -> navController.navigate("login") { popUpTo(0) }
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { 
                            Text(
                                "StudyOsO", 
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            ) 
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { 
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    scope.launch { drawerState.open() } 
                                },
                                modifier = Modifier.semantics {
                                    contentDescription = "Abrir menú de navegación"
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = null, // Descripción ya en el botón
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        actions = {
                            // Botón de cambio de tema mejorado
                            IconButton(
                                onClick = { 
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    isDarkTheme = !isDarkTheme 
                                },
                                modifier = Modifier
                                    .semantics {
                                        contentDescription = if (isDarkTheme) "Cambiar a tema claro" else "Cambiar a tema oscuro"
                                    }
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Filled.WbSunny else Icons.Filled.Bedtime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.size(8.dp))
                            
                            // Botón de perfil mejorado
                            IconButton(
                                onClick = { 
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    selectedScreen = "Perfil" 
                                },
                                modifier = Modifier
                                    .semantics {
                                        contentDescription = "Ver perfil de usuario"
                                    }
                                    .background(
                                        if (selectedScreen == "Perfil") 
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        else 
                                            Color.Transparent,
                                        CircleShape
                                    )
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.semantics {
                            contentDescription = "Barra de navegación principal"
                        }
                    ) {
                        // Dashboard
                        NavigationBarItem(
                            selected = selectedScreen == "Dashboard",
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedScreen = "Dashboard" 
                            },
                            icon = {
                                val scale by animateFloatAsState(
                                    targetValue = if (selectedScreen == "Dashboard") 1.2f else 1f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "dashboard_scale"
                                )
                                Icon(
                                    imageVector = Icons.Filled.Analytics,
                                    contentDescription = "Dashboard",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .scale(scale)
                                )
                            },
                            label = { 
                                Text(
                                    "Dashboard",
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )

                        // Pomodoro
                        NavigationBarItem(
                            selected = selectedScreen == "Pomodoro",
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedScreen = "Pomodoro" 
                            },
                            icon = {
                                val scale by animateFloatAsState(
                                    targetValue = if (selectedScreen == "Pomodoro") 1.2f else 1f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "pomodoro_scale"
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.book_clock),
                                    contentDescription = "Pomodoro",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .scale(scale)
                                )
                            },
                            label = { 
                                Text(
                                    "Pomodoro",
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )

                        // Inicio (Principal)
                        NavigationBarItem(
                            selected = selectedScreen == "Principal",
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedScreen = "Principal" 
                            },
                            icon = {
                                val scale by animateFloatAsState(
                                    targetValue = if (selectedScreen == "Principal") 1.2f else 1f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "home_scale"
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.home),
                                    contentDescription = "Inicio",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .scale(scale)
                                )
                            },
                            label = { 
                                Text(
                                    "Inicio",
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )

                        // Calificaciones/Notas
                        NavigationBarItem(
                            selected = selectedScreen == "ListCalificaciones",
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedScreen = "ListCalificaciones" 
                            },
                            icon = {
                                val scale by animateFloatAsState(
                                    targetValue = if (selectedScreen == "ListCalificaciones") 1.2f else 1f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "grades_scale"
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.calificacion),
                                    contentDescription = "Calificaciones",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .scale(scale)
                                )
                            },
                            label = { 
                                Text(
                                    "Notas",
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )

                        // Tareas
                        NavigationBarItem(
                            selected = selectedScreen == "AddTaskScreen",
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedScreen = "AddTaskScreen" 
                            },
                            icon = {
                                val scale by animateFloatAsState(
                                    targetValue = if (selectedScreen == "AddTaskScreen") 1.2f else 1f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "tasks_scale"
                                )
                                Icon(
                                    imageVector = Icons.Filled.AddCircleOutline,
                                    contentDescription = "Tareas",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .scale(scale)
                                )
                            },
                            label = { 
                                Text(
                                    "Tareas",
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                // Contenedor con transiciones animadas entre pantallas
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                ) {
                    when {
                        selectedScreen.startsWith("DetalleCalificaciones") -> {
                            val cursoId = selectedScreen.substringAfter("DetalleCalificaciones/").toIntOrNull()
                            if (cursoId != null) {
                                DetalleCalificacionesScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    cursoId = cursoId,
                                    onScreenSelected = { screen -> selectedScreen = screen }
                                )
                            } else {
                                ListCalificacionScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onScreenSelected = { screen -> selectedScreen = screen },
                                    usuarioId = usuarioId
                                )
                            }
                        }
                        selectedScreen.startsWith("DetalleCurso") -> {
                            val cursoId = selectedScreen.substringAfter("DetalleCurso/").toIntOrNull()
                            if (cursoId != null) {
                                DetalleCursoScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onScreenSelected = { screen -> selectedScreen = screen },
                                    cursoId = cursoId
                                )
                            } else {
                                // ID inválido, muestra lista de cursos o pantalla por defecto
                                ListCursoScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onScreenSelected = { screen -> selectedScreen = screen },
                                    usuarioId = usuarioId
                                )
                            }
                        }
                        selectedScreen == "AgregarCalificacion" -> AgregarCalificacionScreen(
                            modifier = Modifier.padding(innerPadding),
                            onScreenSelected = { screen -> selectedScreen = screen },
                            usuarioId = usuarioId
                        )
                        selectedScreen == "AgregarCursos" -> AgregarCursosScreen(
                            modifier = Modifier.padding(innerPadding),
                            onScreenSelected = { screen -> selectedScreen = screen },
                            usuarioId = usuarioId
                        )

                        selectedScreen == "Calendario" -> CalendarioScreen(
                            modifier = Modifier.padding(innerPadding),
                        )
                        selectedScreen == "Dashboard" -> DashboardScreen(
                            modifier = Modifier.padding(innerPadding),
                            onScreenSelected = { screen -> selectedScreen = screen },
                            usuarioId = usuarioId
                        )
                        selectedScreen == "ListTaskScreen" -> ListTaskScreen(
                            modifier = Modifier.padding(innerPadding),
                            onScreenSelected = { screen -> selectedScreen = screen },
                            usuarioId = usuarioId
                        )
                        selectedScreen == "ListCalificaciones" -> ListCalificacionScreen(
                            modifier = Modifier.padding(innerPadding),
                            onScreenSelected = { screen -> selectedScreen = screen },
                            usuarioId = usuarioId
                        )
                        selectedScreen == "lisCurso" -> ListCursoScreen(
                            modifier = Modifier.padding(innerPadding),
                            onScreenSelected = { screen -> selectedScreen = screen },
                            usuarioId = usuarioId
                        )
                        selectedScreen == "MatrizEisenhower" -> MatrizEisenhowerScreen(
                            modifier = Modifier.padding(innerPadding)
                            , onScreenSelected = { screen -> selectedScreen = screen }
                        )
                        selectedScreen == "Perfil" -> PerfilScreen(
                            modifier = Modifier.padding(innerPadding),
                            onScreenSelected = { screen -> selectedScreen = screen },
                            usuarioId = usuarioId
                        )
                        selectedScreen == "Pomodoro" -> {
                            PomodoroScreen(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            )
                        }
                        selectedScreen == "Principal" -> PrincipalScreen(
                            modifier = Modifier.padding(innerPadding),
                            onScreenSelected = { screen -> selectedScreen = screen },
                        )
                        selectedScreen.startsWith("SimuladoCalificacion") -> {
                            val cursoId = selectedScreen.substringAfter("SimuladoCalificacion/").toIntOrNull()
                            if (cursoId != null) {
                                SimuladorCalificacionesScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    cursoId = cursoId,
                                    onScreenSelected = { screen -> selectedScreen = screen }
                                )
                            }
                        }
                        selectedScreen == "AddTaskScreen" -> AddTaskScreen(
                            modifier = Modifier.padding(innerPadding),
                            onScreenSelected = { screen -> selectedScreen = screen },
                            usuarioId = usuarioId
                        )
                        selectedScreen == "Guia" -> GuiaScreen(
                            modifier = Modifier.padding(innerPadding),
                        )

                    }
                }
            }
        }
    }
}




