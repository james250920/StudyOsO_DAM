package com.menfroyt.studyoso.presentation

import android.R.id.primary
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.menfroyt.studyoso.R
import kotlinx.coroutines.launch
import com.menfroyt.studyoso.navigation.DrawerContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedScreen by remember { mutableStateOf("Principal") }
    var isDarkTheme by remember { mutableStateOf(true) }
    var valueFondo by remember { mutableStateOf(Color.White) }

    MaterialTheme(
        colorScheme = if (isDarkTheme) {
            darkColorScheme(MaterialTheme.colorScheme.primary)
        } else {
            lightColorScheme(MaterialTheme.colorScheme.onPrimary)
        }
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerContent(
                        selectedScreen = selectedScreen, // Pasar selectedScreen
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

                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary

                        )
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,

                        ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                NavigationBarItem(
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
                                        selectedIconColor = Color(0xFFFFA500),
                                        unselectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        unselectedTextColor = Color.White
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
                                        selectedIconColor = Color(0xFFFFA500),
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
                                        selectedIconColor = Color(0xFFFFA500),
                                        unselectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        unselectedTextColor = Color.White
                                    )
                                )

                                NavigationBarItem(
                                    selected = selectedScreen == "Calificaciones",
                                    onClick = { selectedScreen = "Calificaciones" },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.calificacion),
                                            contentDescription = "calificaciones",
                                            modifier = Modifier.size(30.dp)
                                        )
                                    },
                                    label = { Text("Notas") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFFFFA500),
                                        unselectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        unselectedTextColor = Color.White
                                    )
                                )

                                NavigationBarItem(
                                    selected = selectedScreen == "ListaTareas",
                                    onClick = { selectedScreen = "ListaTareas" },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.AddCircleOutline,
                                            contentDescription = "lista_tareas",
                                            modifier = Modifier.size(30.dp)
                                        )
                                    },
                                    label = { Text("Lista Tareas") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFFFFA500),
                                        unselectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        unselectedTextColor = Color.White
                                    )
                                )
                            }

                        }
                    }
                }
            ) { innerPadding ->
                when (selectedScreen) {
                    "Principal" -> PrincipalScreen(
                        modifier = Modifier.padding(innerPadding),
                        onScreenSelected = { screen -> selectedScreen = screen }
                    )

                    "Dashboard" -> DashboardScreen(
                        modifier = Modifier.padding(innerPadding),
                        onScreenSelected = { screen -> selectedScreen = screen }
                    )

                    "Perfil" -> PerfilScreen(Modifier.padding(innerPadding))
                    "Configuración" -> ConfiguracionScreen(Modifier.padding(innerPadding))
                    "Pomodoro" -> PomodoroScreen(Modifier.padding(innerPadding))
                    "AgregarCursos" -> AgregarCursosScreen(Modifier.padding(innerPadding))
                    "Calificaciones" -> CalificacionesScreen(Modifier.padding(innerPadding))
                    "ListaTareas" -> ListaTareasScreen(Modifier.padding(innerPadding))
                    "MatrizEisenhower" -> MatrizEisenhowerScreen(Modifier.padding(innerPadding))
                    "Calendario" -> CalendarioScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }

}




