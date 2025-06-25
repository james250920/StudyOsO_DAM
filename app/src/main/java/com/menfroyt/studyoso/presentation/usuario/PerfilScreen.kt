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
import androidx.compose.ui.graphics.Color
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil con colores actualizados
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(Color(0xFFE3F2FD), CircleShape)  // Azul muy claro
                .border(3.dp, Color(0xFF1976D2), CircleShape)  // Azul principal
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF1976D2)  // Azul principal
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "${usuario.nombre} ${usuario.apellido}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,  // Casi negro para mejor legibilidad
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Secciones con nuevos colores
        InfoSection(
            title = "Información Personal",
            items = listOf(
                InfoItem("Email", usuario.correo),
                InfoItem("Fecha de Nacimiento", usuario.fechaNacimiento.toString())
            ),
            iconTint = Color(0xFF1976D2)  // Azul principal
        )

        Spacer(modifier = Modifier.height(28.dp))

        InfoSection(
            title = "Estadísticas",
            items = listOf(
                InfoItem("Cursos Activos", "7"),
                InfoItem("Número de Créditos", "24"),
                InfoItem("Promedio General", "18.5")
            ),
            iconTint = Color(0xFF00897B)  // Verde azulado
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onScreenSelected("EditarPerfil") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3355ff),  // Azul principal
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Editar Perfil")
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    items: List<InfoItem>,
    iconTint: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = iconTint,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White  // Fondo blanco para las tarjetas
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                items.forEachIndexed { index, item ->
                    InfoRow(item)
                    if (index < items.size - 1) {
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFE0E0E0),  // Gris claro para divisores
                            thickness = 1.dp
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
            color = Color(0xFF757575)  // Gris medio para etiquetas
        )
        Text(
            text = item.value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF212121)  // Casi negro para valores
        )
    }
}

private data class InfoItem(
    val label: String,
    val value: String
)
