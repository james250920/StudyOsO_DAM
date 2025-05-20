package com.menfroyt.studyoso.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.menfroyt.studyoso.R
import com.menfroyt.studyoso.navigation.DrawerContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
) {
    var selectedOption by remember { mutableStateOf("Inicio") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    DrawerContent(
                        onOptionSelected = { option ->
                            selectedOption = option
                            scope.launch { drawerState.close() }
                        }
                    )
                }
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
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { selectedOption = "Perfil" }) {
                            Icon(
                                imageVector = Icons.Filled.WbSunny,
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(64.dp).padding(end = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        IconButton(onClick = { selectedOption = "Perfil" }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(64.dp).padding(end = 8.dp)
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
                    modifier = Modifier.height(100.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { selectedOption = "Inicio" },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Analytics,
                                contentDescription = "dashboard",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        IconButton(
                            onClick = { selectedOption = "Pomodoro" },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.book_clock),
                                contentDescription = "pomodoro",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        IconButton(
                            onClick = { selectedOption = "Inicio" },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = "home",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        IconButton(
                            onClick = { selectedOption = "Configuración" },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.calificacion),
                                contentDescription = "listcalificaciones",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        IconButton(
                            onClick = { selectedOption = "Inicio" },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddCircleOutline,
                                contentDescription = "addtarea",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            when (selectedOption) {
                "Inicio" -> PrincipalScreen(Modifier.padding(innerPadding))
                "Perfil" -> PerfilScreen(Modifier.padding(innerPadding))
                "Configuración" -> ConfiguracionScreen(Modifier.padding(innerPadding))
                "Pomodoro" -> PomodoroScreen(Modifier.padding(innerPadding))
            }
        }
    }
}



@Composable
fun PerfilScreen(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Perfil",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pantalla de Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ConfiguracionScreen(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Configuración",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pantalla de Configuración",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}