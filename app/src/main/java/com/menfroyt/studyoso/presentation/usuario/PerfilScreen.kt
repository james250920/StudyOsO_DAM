package com.menfroyt.studyoso.presentation.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModel
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Usuario
import com.menfroyt.studyoso.data.repositories.UsuarioRepository


@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    usuarioId: Int,
    onScreenSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val usuarioRepository = remember { UsuarioRepository(db.UsuarioDao()) }
    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(usuarioRepository)
    )

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(usuarioId) {
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

    if (loading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (usuario == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage ?: "Usuario no encontrado")
        }
    } else {
        usuario?.let { usuarioNoNulo ->
            PerfilContent(
                usuario = usuarioNoNulo,
                modifier = modifier,
                onScreenSelected = onScreenSelected
            )
        }
    }
}

@Composable
private fun PerfilContent(
    usuario: Usuario,
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre completo
        Text(
            text = "${usuario.nombre} ${usuario.apellido}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Información personal
        InfoSection(
            title = "Información Personal",
            items = listOf(
                InfoItem("Email", usuario.correo),
                InfoItem("Fecha de Nacimiento", usuario.fechaNacimiento.toString())
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Estadísticas académicas - puedes mejorar aquí para obtener datos reales
        InfoSection(
            title = "Estadísticas",
            items = listOf(
                InfoItem("Cursos Activos", "7"), // Ejemplo, reemplazar si tienes datos reales
                InfoItem("Número de Créditos", "24"), // Ejemplo, reemplazar si tienes datos reales
                InfoItem("Promedio General", "18.5") // Ejemplo, reemplazar si tienes datos reales
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de editar perfil
        Button(
            onClick = { onScreenSelected("EditarPerfil") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Editar Perfil")
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    items: List<InfoItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                items.forEachIndexed { index, item ->
                    InfoRow(item)
                    if (index < items.size - 1) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(item: InfoItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = item.value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private data class InfoItem(
    val label: String,
    val value: String
)
