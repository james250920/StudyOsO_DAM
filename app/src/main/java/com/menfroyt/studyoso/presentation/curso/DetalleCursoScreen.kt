package com.menfroyt.studyoso.presentation.curso

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.material3.FilledIconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.Horario.HorarioViewModel
import com.menfroyt.studyoso.ViewModel.Horario.HorarioViewModelFactory
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModel
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.entities.Horario
import com.menfroyt.studyoso.data.entities.TipoPrueba
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.HorarioRepository
import com.menfroyt.studyoso.data.repositories.TipoPruebaRepository



data class HorarioDialogState(
    val dia: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val aula: String = ""
) {
    fun isValid() = dia.isNotBlank() && horaInicio.isNotBlank() && horaFin.isNotBlank()
}

data class PruebaDialogState(
    val tipo: String = "",
    val numero: String = "",
    val peso: String = ""
) {
    fun isValid() = tipo.isNotBlank() && numero.isNotBlank() && peso.isNotBlank()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCursoScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    cursoId: Int
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val db = remember { AppDatabase.getInstance(context) }
    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(
        factory = CursoViewModelFactory(cursoRepository)
    )

    val horarioRepository = remember { HorarioRepository(db.HorarioDao()) }
    val horarioViewModel: HorarioViewModel = viewModel(factory = HorarioViewModelFactory(horarioRepository))
    val horarios by horarioViewModel.horarios.collectAsState()

    val tipoPruebaRepository = remember { TipoPruebaRepository(db.TipoPruebaDao()) }
    val tipoPruebaViewModel: TipoPruebaViewModel = viewModel(factory = TipoPruebaViewModelFactory(tipoPruebaRepository))
    val tiposPrueba by tipoPruebaViewModel.tiposPrueba.collectAsState()

    // Validación extra: filtrar visualmente por cursoId
    val horariosFiltrados = horarios.filter { it.idCurso == cursoId }
    val tiposPruebaFiltrados = tiposPrueba.filter { it.idCurso == cursoId }
    val hayDatosFueraDeCurso = horarios.any { it.idCurso != cursoId } || tiposPrueba.any { it.idCurso != cursoId }
    // Estados para animaciones
    var isVisible by remember { mutableStateOf(false) }
    var curso by remember { mutableStateOf<Curso?>(null) }
    val scrollState = rememberScrollState()

    // Animaciones de entrada
    val headerScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // Cargar curso cuando cambie cursoId
    LaunchedEffect(cursoId) {
        curso = cursoViewModel.getCursoById(cursoId)
        horarioViewModel.cargarHorarios(cursoId)
        tipoPruebaViewModel.cargarTiposPorCurso(cursoId)
        isVisible = true
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface
        )
    )

    curso?.let { cursoActual ->
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(500)
            ) + fadeIn(tween(500)),
            exit = slideOutVertically() + fadeOut()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundGradient)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .semantics {
                        contentDescription = "Detalles del curso ${cursoActual.nombreCurso}"
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CursoHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(headerScale),
                    onScreenSelected = { screen ->
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onScreenSelected(screen)
                    },
                    cursoNombre = cursoActual.nombreCurso,
                    cursoColor = cursoActual.color ?: "#FFFFFF"
                )

                CursoInfo(
                    profesor = cursoActual.profesor ?: "N/A",
                    aula = cursoActual.aula ?: "N/A"
                )

                HorarioSection(
                    horarios = horariosFiltrados,
                    onAgregarHorario = { horarioState ->
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        val nuevoHorario = Horario(
                            idCurso = cursoId,
                            diaSemana = horarioState.dia,
                            horaInicio = horarioState.horaInicio,
                            horaFin = horarioState.horaFin,
                            aula = horarioState.aula
                        )
                        horarioViewModel.agregarHorario(nuevoHorario)
                    },
                    onEditarHorario = { horario ->
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        horarioViewModel.actualizarHorario(horario)
                    },
                    onEliminarHorario = { horario ->
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        horarioViewModel.eliminarHorario(horario)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                PruebasSection(
                    tiposPrueba = tiposPruebaFiltrados,
                    onAgregarTipoPrueba = { pruebaDialogState ->
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        val nuevoTipoPrueba = TipoPrueba(
                            idCurso = cursoId,
                            nombreTipo = pruebaDialogState.tipo,
                            cantidadPruebas = pruebaDialogState.numero.toIntOrNull() ?: 0,
                            pesoTotal = pruebaDialogState.peso.toDoubleOrNull() ?: 0.0
                        )
                        tipoPruebaViewModel.agregarTipoPrueba(nuevoTipoPrueba)
                    },
                    onEditarTipoPrueba = { tipoPrueba ->
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        tipoPruebaViewModel.actualizarTipoPrueba(tipoPrueba)
                    },
                    onEliminarTipoPrueba = { tipoPrueba ->
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        tipoPruebaViewModel.eliminarTipoPrueba(tipoPrueba)
                    },
                    onScreenSelected = onScreenSelected,
                    modifier = Modifier.fillMaxWidth()
                )

                if (hayDatosFueraDeCurso) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "¡Advertencia: Se detectaron datos que no corresponden al curso seleccionado!",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    } ?: run {
        // Estado de carga mejorado
        Box(
            modifier = modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = "Cargando información del curso"
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Cargando información del curso...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CursoHeader(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    cursoNombre: String,
    cursoColor: String = "#FFFFFF"
) {
    val hapticFeedback = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            hoveredElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón de regreso con feedback háptico
            FilledIconButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onScreenSelected("lisCurso")
                },
                modifier = Modifier
                    .semantics {
                        contentDescription = "Regresar a la lista de cursos"
                    },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = null
                )
            }

            // Icono del curso con color personalizado
            Card(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color(parseColor(cursoColor)).copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Book,
                        contentDescription = "Icono del curso",
                        tint = Color(parseColor(cursoColor)),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Nombre del curso con mejor tipografía
            Column(
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription = "Curso: $cursoNombre"
                    }
            ) {
                Text(
                    text = "DETALLES DEL CURSO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = cursoNombre,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CursoInfo(
    profesor: String,
    aula: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            hoveredElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Información del curso. Profesor: $profesor, Modalidad: $aula"
                },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "INFORMACIÓN DEL CURSO",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold
            )

            // Información del profesor
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Profesor",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = profesor,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer( modifier = Modifier.weight(0.7f) )

                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Modalidad",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = aula,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

        }
    }
}

// Section tipo de pruebas
@Composable
private fun PruebasSection(
    tiposPrueba: List<TipoPrueba>,
    onAgregarTipoPrueba: (PruebaDialogState) -> Unit,
    onEditarTipoPrueba: (TipoPrueba) -> Unit,
    onEliminarTipoPrueba: (TipoPrueba) -> Unit,
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    var showDialog by remember { mutableStateOf(false) }
    var tipoPruebaSeleccionado by remember { mutableStateOf<TipoPrueba?>(null) }
    var isVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(400, delayMillis = 200)
        ) + fadeIn(tween(400, delayMillis = 200)),
        exit = slideOutVertically() + fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                hoveredElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .semantics {
                        contentDescription = if (tiposPrueba.isEmpty()) {
                            "Sección de pruebas, sin pruebas agregadas"
                        } else {
                            "Sección de pruebas, ${tiposPrueba.size} pruebas agregadas"
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Assignment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "PRUEBAS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledIconButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            showDialog = true
                        },
                        modifier = Modifier
                            .semantics {
                                contentDescription = "Agregar nueva prueba"
                            },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null
                        )
                    }
                }

                if (tiposPrueba.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Assignment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No hay pruebas agregadas",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Toca + para agregar una",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(scrollState)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        tiposPrueba.forEachIndexed { index, tipo ->
                            AnimatedVisibility(
                                visible = true,
                                enter = scaleIn(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialScale = 0.8f
                                ) + fadeIn(tween(300, delayMillis = index * 50))
                            ) {
                                TipoPruebaCard(
                                    tipoPrueba = tipo,
                                    modifier = Modifier.width(180.dp),
                                    onEdit = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        tipoPruebaSeleccionado = tipo
                                    },
                                    onDelete = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onEliminarTipoPrueba(tipo)
                                    }
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { onScreenSelected("AgregarCalificacion") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    enabled = tiposPrueba.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tiposPrueba.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        contentColor = if (tiposPrueba.isNotEmpty()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = if (tiposPrueba.isNotEmpty()) "Agregar calificación" else "Agregue una prueba primero",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }

                DialogoAgregarPrueba(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    onConfirm = { prueba ->
                        onAgregarTipoPrueba(prueba)
                        showDialog = false
                    }
                )

                tipoPruebaSeleccionado?.let { tipo ->
                    DialogoEditarPrueba(
                        tipoPrueba = tipo,
                        onDismiss = { tipoPruebaSeleccionado = null },
                        onConfirm = {
                            onEditarTipoPrueba(it)
                            tipoPruebaSeleccionado = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TipoPruebaCard(
    tipoPrueba: TipoPrueba,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tipoPrueba.nombreTipo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Peso: ${tipoPrueba.pesoTotal}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Cards de pruebas individuales
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(tipoPrueba.cantidadPruebas) { index ->
                    PruebaIndividualCard(
                        numeroPrueba = index + 1,
                        nota = "-", // Aquí irá la nota cuando la implementemos
                        pesoIndividual = tipoPrueba.pesoTotal / tipoPrueba.cantidadPruebas
                    )
                }
            }

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun PruebaIndividualCard(
    numeroPrueba: Int,
    nota: String,
    pesoIndividual: Double
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "#$numeroPrueba",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nota,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format("%.0f%%", pesoIndividual),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DialogoEditarPrueba(
    tipoPrueba: TipoPrueba,
    onDismiss: () -> Unit,
    onConfirm: (TipoPrueba) -> Unit
) {
    var state by remember { mutableStateOf(
        PruebaDialogState(
            tipo = tipoPrueba.nombreTipo,
            numero = tipoPrueba.cantidadPruebas.toString(),
            peso = tipoPrueba.pesoTotal.toString()
        )
    )}

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Prueba") },
        text = {
            Column {
                OutlinedTextField(
                    value = state.tipo,
                    onValueChange = { state = state.copy(tipo = it) },
                    label = { Text("Tipo de prueba") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.numero,
                    onValueChange = { state = state.copy(numero = it) },
                    label = { Text("Número de pruebas") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.peso,
                    onValueChange = { state = state.copy(peso = it) },
                    label = { Text("Peso de la prueba") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(TipoPrueba(
                        idCurso = tipoPrueba.idCurso,
                        nombreTipo = state.tipo,
                        cantidadPruebas = state.numero.toIntOrNull() ?: 0,
                        pesoTotal = state.peso.toDoubleOrNull() ?: 0.0
                    ))
                },
                enabled = state.isValid()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DialogoAgregarPrueba(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (PruebaDialogState) -> Unit
) {
    if (showDialog) {
        var state by remember { mutableStateOf(PruebaDialogState()) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Agregar Prueba") },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.tipo,
                        onValueChange = { state = state.copy(tipo = it) },
                        label = { Text("Tipo de prueba") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.numero,
                        onValueChange = { state = state.copy(numero = it) },
                        label = { Text("Número de pruebas") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.peso,
                        onValueChange = { state = state.copy(peso = it) },
                        label = { Text("Peso de la prueba") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { onConfirm(state) },
                    enabled = state.isValid(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3355ff),
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

//section horario

@Composable
private fun HorarioSection(
    horarios: List<Horario>,
    onAgregarHorario: (HorarioDialogState) -> Unit,
    onEditarHorario: (Horario) -> Unit,
    onEliminarHorario: (Horario) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    var showDialog by remember { mutableStateOf(false) }
    var horarioSeleccionado by remember { mutableStateOf<Horario?>(null) }
    var isVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(400, delayMillis = 200)
        ) + fadeIn(tween(400, delayMillis = 200)),
        exit = slideOutVertically() + fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                hoveredElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .semantics {
                        contentDescription = if (horarios.isEmpty()) {
                            "Sección de horarios, sin horarios agregados"
                        } else {
                            "Sección de horarios, ${horarios.size} horarios agregados"
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header con botón de agregar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "HORARIOS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledIconButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            showDialog = true
                        },
                        modifier = Modifier
                            .semantics {
                                contentDescription = "Agregar nuevo horario"
                            },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null
                        )
                    }
                }

                if (horarios.isEmpty()) {
                    // Estado vacío mejorado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No hay horarios agregados",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Toca + para agregar uno",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(scrollState)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        horarios.forEachIndexed { index, horario ->
                            AnimatedVisibility(
                                visible = true,
                                enter = scaleIn(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialScale = 0.8f
                                ) + fadeIn(tween(300, delayMillis = index * 50))
                            ) {
                                HorarioCard(
                                    dia = horario.diaSemana,
                                    horaInicio = horario.horaInicio,
                                    horaFin = horario.horaFin,
                                    aula = horario.aula,
                                    modifier = Modifier.width(180.dp),
                                    onEdit = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        horarioSeleccionado = horario
                                    },
                                    onDelete = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onEliminarHorario(horario)
                                    }
                                )
                            }
                        }
                    }
                }

                DialogoAgregarHorario(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    onConfirm = { horarioState ->
                        onAgregarHorario(horarioState)
                        showDialog = false
                    }
                )

                horarioSeleccionado?.let { horario ->
                    DialogoEditarHorario(
                        horario = horario,
                        onDismiss = { horarioSeleccionado = null },
                        onConfirm = { onEditarHorario(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun HorarioCard(
    dia: String,
    horaInicio: String,
    horaFin: String,
    aula: String,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .height(140.dp)
            .semantics {
                contentDescription = "Horario de $dia de $horaInicio a $horaFin en $aula"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            hoveredElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header con día
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = dia.uppercase(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Información de tiempo y aula
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$horaInicio - $horaFin",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                if (aula.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = aula,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Botones de acción
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onEdit()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .semantics {
                            contentDescription = "Editar horario de $dia"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDelete()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .semantics {
                            contentDescription = "Eliminar horario de $dia"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogoEditarHorario(
    horario: Horario,
    onDismiss: () -> Unit,
    onConfirm: (Horario) -> Unit
) {
    var state by remember { mutableStateOf(HorarioDialogState(horario.diaSemana, horario.horaInicio, horario.horaFin, horario.aula)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Horario") },
        text = {
            Column {
                OutlinedTextField(
                    value = state.dia,
                    onValueChange = { state = state.copy(dia = it) },
                    label = { Text("Día") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.horaInicio,
                    onValueChange = { state = state.copy(horaInicio = it) },
                    label = { Text("Hora Inicio") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.horaFin,
                    onValueChange = { state = state.copy(horaFin = it) },
                    label = { Text("Hora Fin") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.aula,
                    onValueChange = { state = state.copy(aula = it) },
                    label = { Text("Aula") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    Horario(
                        idCurso = horario.idCurso,
                        idHorario = horario.idHorario,
                        diaSemana = state.dia,
                        horaInicio = state.horaInicio,
                        horaFin = state.horaFin,
                        aula = state.aula
                    )
                )
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoAgregarHorario(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (HorarioDialogState) -> Unit
) {
    val dias = listOf(
        "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    )

    if (showDialog) {
        var state by remember { mutableStateOf(HorarioDialogState()) }
        var showTimePickerInicio by remember { mutableStateOf(false) }
        var showTimePickerFin by remember { mutableStateOf(false) }
        var expandedDias by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Agregar Horario") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    ExposedDropdownMenuBox(
                        expanded = expandedDias,
                        onExpandedChange = { expandedDias = it }
                    ) {
                        OutlinedTextField(
                            value = state.dia,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Día") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDias)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedDias,
                            onDismissRequest = { expandedDias = false }
                        ) {
                            dias.forEach { dia ->
                                DropdownMenuItem(
                                    text = { Text(dia) },
                                    onClick = {
                                        state = state.copy(dia = dia)
                                        expandedDias = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.horaInicio,
                        onValueChange = { },
                        label = { Text("Agregar hora inicio") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showTimePickerInicio = true }) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Seleccionar hora",
                                    tint = Color(0xFFf60a0a)
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = state.horaFin,
                        onValueChange = { },
                        label = { Text("Agregar hora fin") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showTimePickerFin = true }) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Seleccionar hora",
                                    tint = Color(0xFFf60a0a)
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        value = state.aula,
                        onValueChange = { state = state.copy(aula = it) },
                        label = { Text("Aula") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showTimePickerInicio) {
                        val timePickerState = rememberTimePickerState()
                        TimePickerDialog(
                            onDismissRequest = { showTimePickerInicio = false },
                            confirmButton = {
                                Button(onClick = {
                                    val hora = timePickerState.hour
                                    val minuto = timePickerState.minute.toString().padStart(2, '0')
                                    val periodo = if (hora < 12) "AM" else "PM"
                                    val hora12 = when (hora) {
                                        0 -> "12"
                                        in 1..12 -> hora.toString()
                                        else -> (hora - 12).toString()
                                    }.padStart(2, '0')
                                    state = state.copy(horaInicio = "$hora12:$minuto $periodo")
                                    showTimePickerInicio = false
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showTimePickerInicio = false }) {
                                    Text("Cancelar")
                                }
                            },
                            title = { Text("Seleccionar hora de inicio") },
                            content = { TimePicker(state = timePickerState) }
                        )
                    }

                    if (showTimePickerFin) {
                        val timePickerState = rememberTimePickerState()
                        TimePickerDialog(
                            onDismissRequest = { showTimePickerFin = false },
                            confirmButton = {
                                Button(onClick = {
                                    val hora = timePickerState.hour
                                    val minuto = timePickerState.minute.toString().padStart(2, '0')
                                    val periodo = if (hora < 12) "AM" else "PM"
                                    val hora12 = when (hora) {
                                        0 -> "12"
                                        in 1..12 -> hora.toString()
                                        else -> (hora - 12).toString()
                                    }.padStart(2, '0')
                                    state = state.copy(horaFin = "$hora12:$minuto $periodo")
                                    showTimePickerFin = false
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showTimePickerFin = false }) {
                                    Text("Cancelar")
                                }
                            },
                            title = { Text("Seleccionar hora de fin") },
                            content = { TimePicker(state = timePickerState) }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(state) },
                    enabled = state.isValid(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3355ff),
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFf60a0a),
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@ExperimentalMaterial3Api
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = title,
        text = content
    )
}
