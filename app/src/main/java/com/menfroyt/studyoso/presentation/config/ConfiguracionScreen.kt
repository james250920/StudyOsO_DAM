package com.menfroyt.studyoso.presentation.config

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ConfiguracionScreen(
    modifier: Modifier = Modifier
) {
    var darkMode by remember { mutableStateOf(false) }
    var notificaciones by remember { mutableStateOf(true) }
    var sonido by remember { mutableStateOf(true) }
    var idiomaSeleccionado by remember { mutableStateOf("Español") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Sección de Apariencia
        ConfiguracionSeccion(titulo = "Apariencia") {
            ConfiguracionSwitch(
                titulo = "Modo oscuro",
                descripcion = "Cambiar entre tema claro y oscuro",
                icono = Icons.Default.DarkMode,
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Sección de Notificaciones
        ConfiguracionSeccion(titulo = "Notificaciones") {
            ConfiguracionSwitch(
                titulo = "Notificaciones push",
                descripcion = "Recibir alertas de tareas y eventos",
                icono = Icons.Default.Notifications,
                checked = notificaciones,
                onCheckedChange = { notificaciones = it }
            )

            ConfiguracionSwitch(
                titulo = "Sonidos",
                descripcion = "Activar sonidos de la aplicación",
                icono = Icons.Default.VolumeUp,
                checked = sonido,
                onCheckedChange = { sonido = it }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Sección de Idioma
        ConfiguracionSeccion(titulo = "Idioma") {
            var expandedMenu by remember { mutableStateOf(false) }
            val idiomas = listOf("Español", "English", "Português")

            ListItem(
                headlineContent = { Text("Idioma de la aplicación") },
                supportingContent = { Text(idiomaSeleccionado) },
                leadingContent = {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = null
                    )
                },
                trailingContent = {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        idiomas.forEach { idioma ->
                            DropdownMenuItem(
                                text = { Text(idioma) },
                                onClick = {
                                    idiomaSeleccionado = idioma
                                    expandedMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Sección de Cuenta
        ConfiguracionSeccion(titulo = "Cuenta") {
            ListItem(
                headlineContent = { Text("Cerrar sesión") },
                leadingContent = {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier.clickable { /* Implementar cierre de sesión */ }
            )
        }
    }
}

@Composable
private fun ConfiguracionSeccion(
    titulo: String,
    content: @Composable () -> Unit
) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    content()
}

@Composable
private fun ConfiguracionSwitch(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(titulo) },
        supportingContent = { Text(descripcion) },
        leadingContent = { Icon(icono, contentDescription = null) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}