package com.menfroyt.studyoso.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.menfroyt.studyoso.presentation.config.ConfiguracionScreen
import com.menfroyt.studyoso.presentation.curso.AgregarCursosScreen
import com.menfroyt.studyoso.presentation.curso.DetalleCursoScreen
import com.menfroyt.studyoso.presentation.curso.ListCursoScreen
import com.menfroyt.studyoso.presentation.tarea.AddTaskScreen
import com.menfroyt.studyoso.presentation.tarea.ListTaskScreen
import com.menfroyt.studyoso.presentation.usuario.PerfilScreen
import com.menfroyt.studyoso.presentation.components.CalendarioScreen
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

    MaterialTheme(
        colorScheme = if (isDarkTheme) {
            darkColorScheme(
                primary = Color(0xFF003366),
                onPrimary = Color.White,
                surface = Color(0xFF121212),
                background = Color(0xFF121212),
                onBackground = Color.White,
                onSurface = Color.White
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF003366),
                onPrimary = Color.White,
                surface = Color.White,
                background = Color.White,
                onBackground = Color.Black,
                onSurface = Color.Black
            )
        }
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerContent(
                        selectedScreen = selectedScreen,
                        onOptionSelected = { option ->
                            when (option) {
                                "Perfil" -> selectedScreen = "Perfil"
                                "Dashboard" -> selectedScreen = "Dashboard"
                                "Configuración" -> selectedScreen = "Configuración"
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
                        title = { Text("StudyOsO", style = MaterialTheme.typography.titleLarge) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menú",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { isDarkTheme = !isDarkTheme }) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Filled.WbSunny else Icons.Filled.Bedtime,
                                    contentDescription = "cambiar tema",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                            IconButton(onClick = { selectedScreen = "Perfil" }) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = "Perfil",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF3355ff),
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                bottomBar = {

                    NavigationBar(
                        containerColor = Color(0xFF3355ff),

                    ) {
                        NavigationBarItem(
                            modifier = Modifier.background(color = Color(0xFF3355ff)),
                            selected = selectedScreen == "Dashboard",
                            onClick = { selectedScreen = "Dashboard" },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Analytics,
                                    contentDescription = "dashboard",
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            label = { Text("Dashboard") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF354FFC),
                                unselectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White,
                            )
                        )

                        NavigationBarItem(
                            selected = selectedScreen == "Pomodoro",
                            onClick = { selectedScreen = "Pomodoro" },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.book_clock),
                                    contentDescription = "pomodoro",
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            label = { Text("Pomodoro") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF354FFC),
                                unselectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White
                            )
                        )

                        NavigationBarItem(
                            selected = selectedScreen == "Principal",
                            onClick = { selectedScreen = "Principal" },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.home),
                                    contentDescription = "home",
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            label = { Text("Inicio") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF354FFC),
                                unselectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White
                            )
                        )

                        NavigationBarItem(
                            selected = selectedScreen == "ListCalificaciones",
                            onClick = { selectedScreen = "ListCalificaciones" },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.calificacion),
                                    contentDescription = "calificaciones",
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            label = { Text("Notas") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF354FFC),
                                unselectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White
                            )
                        )

                        NavigationBarItem(
                            selected = selectedScreen == "AddTaskScreen",
                            onClick = { selectedScreen = "AddTaskScreen" },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.AddCircleOutline,
                                    contentDescription = "lista_tareas",
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            label = { Text("Tareas") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF354FFC),
                                unselectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White
                            )
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
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
                    selectedScreen == "Configuración" -> ConfiguracionScreen(
                        modifier = Modifier.padding(innerPadding)
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
                }
            }
        }
    }
}




