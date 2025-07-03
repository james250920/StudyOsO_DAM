package com.menfroyt.studyoso.presentation.usuario

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModelFactory
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModel
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Calificacion
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.entities.Usuario
import com.menfroyt.studyoso.data.repositories.CalificacionRepository
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.UsuarioRepository


@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    usuarioId: Int,
    onScreenSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    val calificacionRepository = remember { CalificacionRepository(db.CalificacionDao()) }
    val calificacionViewModel: CalificacionViewModel = viewModel(factory = CalificacionViewModelFactory(calificacionRepository))

    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))

    val usuarioRepository = remember { UsuarioRepository(db.UsuarioDao()) }
    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(usuarioRepository)
    )

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var curso by remember { mutableStateOf<List<Curso>>(emptyList()) }
    var calificacion by remember { mutableStateOf<List<Calificacion>>(emptyList()) }
    var creditoscurso by remember { mutableStateOf<Int?>(null) }

    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(usuarioId) {
        try {

            usuarioViewModel.getUsuarioAutenticado(
                id = usuarioId,
                onSuccess = { usuario = it },
                onError = { errorMessage = it }
            )

            // Cargar cursos
            cursoViewModel.cargarCursos(usuarioId)

            // Cargar calificaciones
            calificacionViewModel.cargarCalificacionesPorUsuario(usuarioId)

            // Obtener créditos de forma asíncrona
            try {
                creditoscurso = cursoViewModel.getTotalCreditosByUsuario(usuarioId)
            } catch (e: Exception) {
                creditoscurso = 0
            }

        } catch (e: Exception) {

        } finally {
            loading = false
        }
    }

    // Observar cambios en cursos
    LaunchedEffect(Unit) {
        cursoViewModel.cursos.collect { cursos ->
            curso = cursos
        }
    }

    // Observar cambios en calificaciones
    LaunchedEffect(Unit) {
        calificacionViewModel.calificaciones.collect { calificaciones ->
            calificacion = calificaciones
        }
    }

    // UI
    when {
        loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando perfil...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        usuario == null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: "Usuario no encontrado",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            loading = true
                            errorMessage = null
                        }
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }

        else -> {
            usuario?.let { usuarioNoNulo ->
                PerfilContent(
                    cursos = curso,
                    calificaciones = calificacion,
                    usuario = usuarioNoNulo,
                    modifier = modifier,
                    onScreenSelected = onScreenSelected
                )
            }
        }
    }
}

@Composable
private fun PerfilContent(
    cursos: List<Curso>,
    calificaciones: List<Calificacion>,
    usuario: Usuario,
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit
) {
    val totalCreditos = remember(cursos) {
        cursos.sumOf { it.creditos ?: 0 }
    }

    val promedioGeneral = if (calificaciones.isNotEmpty()) {
        calificaciones.mapNotNull { it.calificacionObtenida }.average()
    } else 0.0
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val density = LocalDensity.current
    
    // Estados para animaciones
    var isVisible by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    // Gradiente de fondo moderno
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = if (isLandscape) 32.dp else 20.dp,
                    vertical = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header con perfil animado
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
                ProfileHeaderCard(
                    usuario = usuario,
                    onEditClick = { showEditDialog = true },
                    isLandscape = isLandscape
                )
            }
            
            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 24.dp))
            
            // Información personal animada
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
                ModernInfoSection(
                    title = "Información Personal",
                    icon = Icons.Filled.Person,
                    items = listOf(
                        InfoItem("Email", usuario.correo, Icons.Outlined.Email),
                        InfoItem("Fecha de Nacimiento", usuario.fechaNacimiento.toString(), Icons.Outlined.CalendarMonth)
                    ),
                    isLandscape = isLandscape
                )
            }
            
            Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 20.dp))
            
            // Estadísticas animadas
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
                ModernInfoSection(
                    title = "Estadísticas Académicas",
                    icon = Icons.Filled.Analytics,
                    items = listOf(
                        InfoItem("Cursos Activos", "${cursos.size}", Icons.Outlined.School),
                        InfoItem("Número de Créditos", "$totalCreditos", Icons.Outlined.Star),
                        InfoItem("Promedio General", String.format("%.1f", promedioGeneral), Icons.Outlined.TrendingUp)
                    ),
                    isLandscape = isLandscape,
                    colorScheme = "success"
                )
            }
            
            Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 20.dp))
            
            // Acciones rápidas animadas
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
                QuickActionsCard(
                    onActionSelected = { action ->
                        selectedAction = action
                        when (action) {
                            "editar" -> showEditDialog = true
                            "configuracion" -> onScreenSelected("configuracion")
                            "calificaciones" -> onScreenSelected("ListCalificaciones")
                            "cursos" -> onScreenSelected("lisCurso")
                        }
                    },
                    isLandscape = isLandscape
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Diálogo de edición
        if (showEditDialog) {
            ProfileEditDialog(
                usuario = usuario,
                onDismiss = { showEditDialog = false },
                onConfirm = { 
                    showEditDialog = false
                    // Aquí iría la lógica para actualizar el perfil
                }
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    usuario: Usuario,
    onEditClick: () -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 20.dp else 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar con animación de escala
            var avatarScale by remember { mutableStateOf(0.8f) }
            
            LaunchedEffect(Unit) {
                avatarScale = 1f
            }
            
            Box(
                modifier = Modifier
                    .size(if (isLandscape) 100.dp else 120.dp)
                    .scale(
                        animateFloatAsState(
                            targetValue = avatarScale,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ).value
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable { onEditClick() }
                    .semantics { 
                        contentDescription = "Foto de perfil de ${usuario.nombre} ${usuario.apellido}. Toca para editar"
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(if (isLandscape) 70.dp else 85.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                // Indicador de edición
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .size(28.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            CircleShape
                        )
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar perfil",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 20.dp))
            
            // Nombre del usuario
            Text(
                text = "${usuario.nombre} ${usuario.apellido}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    contentDescription = "Nombre del usuario: ${usuario.nombre} ${usuario.apellido}"
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Badge de estado
            Surface(
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Estudiante Activo",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernInfoSection(
    title: String,
    icon: ImageVector,
    items: List<InfoItem>,
    isLandscape: Boolean,
    colorScheme: String = "primary",
    modifier: Modifier = Modifier
) {
    val sectionColor = when (colorScheme) {
        "success" -> Color(0xFF2E7D32)
        "warning" -> Color(0xFFEF6C00)
        else -> MaterialTheme.colorScheme.primary
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        // Header de la sección
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = sectionColor.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(8.dp),
                    tint = sectionColor
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = sectionColor
            )
        }
        
        // Card con información
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = if (isLandscape) 16.dp else 20.dp,
                    vertical = if (isLandscape) 12.dp else 16.dp
                )
            ) {
                items.forEachIndexed { index, item ->
                    ModernInfoRow(
                        item = item,
                        sectionColor = sectionColor,
                        isLandscape = isLandscape
                    )
                    
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = if (isLandscape) 8.dp else 12.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernInfoRow(
    item: InfoItem,
    sectionColor: Color,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = if (isLandscape) 2.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono del campo
        Surface(
            modifier = Modifier.size(32.dp),
            color = sectionColor.copy(alpha = 0.1f),
            shape = CircleShape
        ) {
            Icon(
                imageVector = item.icon ?: Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .padding(7.dp),
                tint = sectionColor
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Información
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = item.value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun QuickActionsCard(
    onActionSelected: (String) -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = if (isLandscape) 16.dp else 20.dp,
                vertical = if (isLandscape) 12.dp else 16.dp
            )
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Dashboard,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            // Botones de acción
            if (isLandscape) {
                // Layout horizontal para landscape
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        ActionButton("Editar Perfil", Icons.Filled.Edit, "editar"),
                        ActionButton("Configuración", Icons.Filled.Settings, "configuracion"),
                        ActionButton("Calificaciones", Icons.Filled.Grade, "calificaciones"),
                        ActionButton("Mis Cursos", Icons.Filled.School, "cursos")
                    ).forEach { action ->
                        ActionButtonCard(
                            title = action.title,
                            icon = action.icon,
                            onAction = { onActionSelected(action.action) },
                            modifier = Modifier.weight(1f),
                            isCompact = true
                        )
                    }
                }
            } else {
                // Layout vertical para portrait
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        ActionButton("Editar Perfil", Icons.Filled.Edit, "editar"),
                        ActionButton("Configuración", Icons.Filled.Settings, "configuracion"),
                        ActionButton("Calificaciones", Icons.Filled.Grade, "calificaciones"),
                        ActionButton("Mis Cursos", Icons.Filled.School, "cursos")
                    ).forEach { action ->
                        ActionButtonCard(
                            title = action.title,
                            icon = action.icon,
                            onAction = { onActionSelected(action.action) },
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
private fun ActionButtonCard(
    title: String,
    icon: ImageVector,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Surface(
        modifier = modifier
            .clickable { onAction() }
            .semantics { contentDescription = "Botón de $title" },
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        if (isCompact) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ProfileEditDialog(
    usuario: Usuario,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editar Perfil",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "La funcionalidad de edición de perfil estará disponible próximamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Funciones próximas:",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        listOf(
                            "• Cambiar foto de perfil",
                            "• Actualizar información personal",
                            "• Modificar preferencias",
                            "• Gestionar notificaciones"
                        ).forEach { item ->
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Entendido")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cerrar")
            }
        },
        shape = RoundedCornerShape(24.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(16.dp)
    )
}

private data class InfoItem(
    val label: String,
    val value: String,
    val icon: ImageVector? = null
)

private data class ActionButton(
    val title: String,
    val icon: ImageVector,
    val action: String
)
