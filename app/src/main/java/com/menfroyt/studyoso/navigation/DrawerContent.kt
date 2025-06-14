package com.menfroyt.studyoso.navigation
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.menfroyt.studyoso.presentation.auth.SessionManager


@Composable
fun DrawerContent(selectedScreen: String,onOptionSelected: (String) -> Unit,
                  ) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Menú",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        DrawerOption(
            text = "Perfil",
            icon = Icons.Filled.AccountCircle,
            selectedScreen = selectedScreen,
            onClick = { onOptionSelected("Perfil") }
        )

        DrawerOption(
            text = "Dashboard",
            icon = Icons.Filled.Analytics,
            selectedScreen = selectedScreen,
            onClick = { onOptionSelected("Dashboard") }
        )

        DrawerOption(
            text = "Configuración",
            icon = Icons.Filled.Settings,
            selectedScreen = selectedScreen,
            onClick = { onOptionSelected("Configuración") }
        )
        DrawerOption(
            text = "Pomodoro",
            icon = Icons.Filled.Timer,
            selectedScreen = selectedScreen,
            onClick = { onOptionSelected("Pomodoro") }
        )
        DrawerOption(
            text = "Cerrar Sesión",
            icon = Icons.Filled.Output,
            selectedScreen = selectedScreen,
            onClick = {
                sessionManager.endSession()
                onOptionSelected("Cerrar Sesión")
            }
        )

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Versión 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun DrawerOption(
    text: String,
    icon: ImageVector,
    selectedScreen: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Ir a $text",
            tint = when (text) {
                "Home" -> if (selectedScreen == "Principal") Color(0xFF33c1ff) else Color(0xFF3355ff)
                "Dashboard" -> if (selectedScreen == "Dashboard") Color(0xFF33c1ff) else Color(0xFF3355ff)
                "Perfil" -> if (selectedScreen == "Perfil") Color(0xFF33c1ff) else Color(0xFF3355ff)
                "Configuración" -> if (selectedScreen == "Configuración") Color(0xFF33c1ff) else Color(0xFF3355ff)
                "Pomodoro" -> if (selectedScreen == "Pomodoro") Color(0xFF33c1ff) else Color(0xFF3355ff)
                "Cerrar Sesión" -> Color(0xFF3355ff) // No resaltamos "Cerrar Sesión"
                else -> MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = when (text) {
                "Home" -> if (selectedScreen == "Principal") Color(0xFF33c1ff) else MaterialTheme.colorScheme.onSurface
                "Dashboard" -> if (selectedScreen == "Dashboard") Color(0xFF33c1ff) else MaterialTheme.colorScheme.onSurface
                "Perfil" -> if (selectedScreen == "Perfil") Color(0xFF33c1ff) else MaterialTheme.colorScheme.onSurface
                "Configuración" -> if (selectedScreen == "Configuración") Color(0xFF33c1ff) else MaterialTheme.colorScheme.onSurface
                "Pomodoro" -> if (selectedScreen == "Pomodoro") Color(0xFF33c1ff) else MaterialTheme.colorScheme.onSurface
                "Cerrar Sesión" -> MaterialTheme.colorScheme.onSurface // No resaltamos "Cerrar Sesión"
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
