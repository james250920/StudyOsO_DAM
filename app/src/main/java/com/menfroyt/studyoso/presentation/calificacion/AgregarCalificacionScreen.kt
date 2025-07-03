package com.menfroyt.studyoso.presentation.calificacion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModelFactory
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModel
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Calificacion
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.entities.TipoPrueba
import com.menfroyt.studyoso.data.repositories.CalificacionRepository
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.TipoPruebaRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarCalificacionScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    usuarioId: Int
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val haptic = LocalHapticFeedback.current

    // Instancias de repos y viewmodels
    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))

    val tipoPruebaRepository = remember { TipoPruebaRepository(db.TipoPruebaDao()) }
    val tipoPruebaViewModel: TipoPruebaViewModel = viewModel(factory = TipoPruebaViewModelFactory(tipoPruebaRepository))

    val calificacionRepository = remember { CalificacionRepository(db.CalificacionDao()) }
    val calificacionViewModel: CalificacionViewModel = viewModel(factory = CalificacionViewModelFactory(calificacionRepository))

    // Estados de la UI con animaciones
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Estados para manejo de UI
    var cursos by remember { mutableStateOf(listOf<Curso>()) }
    var cursoSeleccionado by remember { mutableStateOf<Curso?>(null) }

    var tiposPrueba by remember { mutableStateOf(listOf<TipoPrueba>()) }
    var tipoPruebaSeleccionada by remember { mutableStateOf<TipoPrueba?>(null) }

    var numeroPruebaSeleccionado by remember { mutableStateOf<Int?>(null) }
    var calificacionTexto by remember { mutableStateOf("") }

    var expandidoCurso by remember { mutableStateOf(false) }
    var expandidoTipoPrueba by remember { mutableStateOf(false) }
    var expandidoNumeroPrueba by remember { mutableStateOf(false) }

    // Animaciones de escala para feedback visual
    val buttonScale by animateFloatAsState(
        targetValue = if (isLoading) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    // LaunchedEffects para feedback
    LaunchedEffect(showError) {
        if (showError) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            snackbarHostState.showSnackbar(errorMessage)
            showError = false
        }
    }

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            snackbarHostState.showSnackbar("¡Calificación guardada exitosamente!")
            showSuccess = false
        }
    }

    // Cargar cursos del usuario al inicio
    LaunchedEffect(usuarioId) {
        cursoViewModel.cargarCursos(usuarioId)
        cursoViewModel.cursos.collect { lista ->
            cursos = lista
        }
    }

    // Cuando cambia curso seleccionado, carga los tipos de prueba para ese curso
    LaunchedEffect(cursoSeleccionado) {
        cursoSeleccionado?.let {
            tipoPruebaViewModel.cargarTiposPorCurso(it.idCurso)
            tipoPruebaViewModel.tiposPrueba.collect { lista ->
                tiposPrueba = lista
            }
        } ?: run {
            tiposPrueba = emptyList()
        }
        tipoPruebaSeleccionada = null
        numeroPruebaSeleccionado = null
    }

    // Contenedor principal con diseño mejorado
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header mejorado con diseño moderno
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onScreenSelected("ListCalificaciones") 
                        },
                        modifier = Modifier.semantics { 
                            contentDescription = "Regresar a la lista de calificaciones" 
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Nueva Calificación",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Registra tus resultados académicos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Sección del curso con diseño mejorado
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300)) + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Curso",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            ExposedDropdownMenuBox(
                                expanded = expandidoCurso,
                                onExpandedChange = { expandidoCurso = it }
                            ) {
                                OutlinedTextField(
                                    value = cursoSeleccionado?.nombreCurso ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Seleccionar Curso") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCurso) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandidoCurso,
                                    onDismissRequest = { expandidoCurso = false }
                                ) {
                                    cursos.forEach { curso ->
                                        DropdownMenuItem(
                                            text = { Text(curso.nombreCurso) },
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                cursoSeleccionado = curso
                                                expandidoCurso = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Sección del tipo de prueba
                AnimatedVisibility(
                    visible = cursoSeleccionado != null,
                    enter = fadeIn(animationSpec = tween(300)) + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tipo de Prueba",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            ExposedDropdownMenuBox(
                                expanded = expandidoTipoPrueba,
                                onExpandedChange = { expandidoTipoPrueba = it }
                            ) {
                                OutlinedTextField(
                                    value = tipoPruebaSeleccionada?.nombreTipo ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = cursoSeleccionado != null,
                                    label = { Text("Seleccionar Tipo de Prueba") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoTipoPrueba) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandidoTipoPrueba,
                                    onDismissRequest = { expandidoTipoPrueba = false }
                                ) {
                                    tiposPrueba.forEach { tipoPrueba ->
                                        DropdownMenuItem(
                                            text = { Text(tipoPrueba.nombreTipo) },
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                tipoPruebaSeleccionada = tipoPrueba
                                                numeroPruebaSeleccionado = null
                                                expandidoTipoPrueba = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Sección número de prueba y calificación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Número de prueba
                    AnimatedVisibility(
                        visible = tipoPruebaSeleccionada != null,
                        enter = fadeIn(animationSpec = tween(300)) + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "N° Prueba",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                ExposedDropdownMenuBox(
                                    expanded = expandidoNumeroPrueba,
                                    onExpandedChange = { expandidoNumeroPrueba = it }
                                ) {
                                    OutlinedTextField(
                                        value = numeroPruebaSeleccionado?.toString() ?: "",
                                        onValueChange = {},
                                        readOnly = true,
                                        enabled = tipoPruebaSeleccionada != null,
                                        label = { Text("#") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoNumeroPrueba) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandidoNumeroPrueba,
                                        onDismissRequest = { expandidoNumeroPrueba = false }
                                    ) {
                                        val cantidad = tipoPruebaSeleccionada?.cantidadPruebas ?: 0
                                        (1..cantidad).forEach { numero ->
                                            DropdownMenuItem(
                                                text = { Text(numero.toString()) },
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    numeroPruebaSeleccionado = numero
                                                    expandidoNumeroPrueba = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Calificación
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Grade,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Nota",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            OutlinedTextField(
                                value = calificacionTexto,
                                onValueChange = { calificacionTexto = it },
                                label = { Text("0.0 - 20.0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                // Botón de guardar con animaciones
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        
                        // Validaciones básicas
                        if (cursoSeleccionado == null || tipoPruebaSeleccionada == null ||
                            numeroPruebaSeleccionado == null || calificacionTexto.isBlank()
                        ) {
                            errorMessage = "Completa todos los campos"
                            showError = true
                            return@Button
                        }

                        val calificacionValor = calificacionTexto.toDoubleOrNull()
                        if (calificacionValor == null) {
                            errorMessage = "Ingresa una calificación válida"
                            showError = true
                            return@Button
                        }

                        if (calificacionValor < 0 || calificacionValor > 20) {
                            errorMessage = "La calificación debe estar entre 0 y 20"
                            showError = true
                            return@Button
                        }

                        isLoading = true
                        
                        // Crear objeto Calificacion
                        val nuevaCalificacion = Calificacion(
                            idCurso = cursoSeleccionado!!.idCurso,
                            idTipoPrueba = tipoPruebaSeleccionada!!.idTipoPrueba,
                            numeroPrueba = numeroPruebaSeleccionado!!,
                            calificacionObtenida = calificacionValor
                        )

                        // Insertar calificación a través del ViewModel
                        calificacionViewModel.insertarCalificacion(nuevaCalificacion)
                        showSuccess = true
                        
                        // Navegar a lista de calificaciones
                        onScreenSelected("ListCalificaciones")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(buttonScale),
                    enabled = !isLoading && cursoSeleccionado != null &&
                            tipoPruebaSeleccionada != null &&
                            numeroPruebaSeleccionado != null &&
                            calificacionTexto.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    AnimatedVisibility(
                        visible = !isLoading,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Grade,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Guardar Calificación",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    
                    AnimatedVisibility(
                        visible = isLoading,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
        
        // SnackbarHost para mostrar mensajes
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

