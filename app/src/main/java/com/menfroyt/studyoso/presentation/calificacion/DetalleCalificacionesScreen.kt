package com.menfroyt.studyoso.presentation.calificacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModelFactory
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModel
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.TipoPrueba
import com.menfroyt.studyoso.data.repositories.CalificacionRepository
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.TipoPruebaRepository

data class PruebaDialogState(
    val tipo: String = "",
    val numero: String = "",
    val peso: String = ""
) {
    fun isValid() = tipo.isNotBlank() && numero.isNotBlank() && peso.isNotBlank()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCalificacionesScreen(
    modifier: Modifier = Modifier,
    cursoId: Int,
    onScreenSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val tipoPruebaRepo = remember { TipoPruebaRepository(db.TipoPruebaDao()) }
    val tipoPruebaViewModel: TipoPruebaViewModel = viewModel(factory = TipoPruebaViewModelFactory(tipoPruebaRepo))
    val tiposPrueba by tipoPruebaViewModel.tiposPrueba.collectAsState()
    val calificacionRepo = remember { CalificacionRepository(db.CalificacionDao()) }
    val calificacionViewModel: CalificacionViewModel = viewModel(factory = CalificacionViewModelFactory(calificacionRepo))
    val calificaciones by calificacionViewModel.calificaciones.collectAsState()
    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))
    var nombreCurso by remember { mutableStateOf<String>("") }
    var showDialogTipoPrueba by remember { mutableStateOf(false) }

    LaunchedEffect(cursoId) {
        tipoPruebaViewModel.cargarTiposPorCurso(cursoId)
        calificacionViewModel.cargarCalificacionesPorCurso(cursoId)
        val curso = cursoViewModel.getCursoById(cursoId)
        nombreCurso = curso?.nombreCurso ?: ""
    }

    val calPorTipo = tiposPrueba.associateWith { tipo ->
        calificaciones.filter { it.idTipoPrueba == tipo.idTipoPrueba }
    }

    val promediosPorTipo = tiposPrueba.associate { tipo ->
        tipo.nombreTipo to (calPorTipo[tipo]?.mapNotNull { it.calificacionObtenida }?.average()?.takeIf { !it.isNaN() } ?: 0.0)
    }

    val promedioFinal = tiposPrueba.sumOf { tipo ->
        val promedioTipo = promediosPorTipo[tipo.nombreTipo] ?: 0.0
        promedioTipo * (tipo.pesoTotal / 100.0)
    }
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier = modifier.fillMaxSize()
        .background(backgroundGradient)
    ) {
        Scaffold(
            floatingActionButton = {
                if (tiposPrueba.isNotEmpty()) {  // Solo muestra el FAB si hay tipos de prueba
                    FloatingActionButton(
                        onClick = { onScreenSelected("AgregarCalificacion") },
                        containerColor = Color(0xFF3355ff),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Agregar Calificación",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundGradient)
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onScreenSelected("ListCalificaciones") },
                            modifier = Modifier
                                .size(40.dp)  // Aumentado para mejor toque
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = "Regresar a lista",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)  // Tamaño del icono más visible
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))  // Más espacio entre elementos

                        Text(
                            text = nombreCurso,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row {
                                Text(
                                    "Distribución de Pruebas",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { showDialogTipoPrueba = true },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Añadir tipo de prueba",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                tiposPrueba.forEach { tipo ->
                                    PesoItem(tipo.nombreTipo, "${tipo.pesoTotal.toInt()}%")
                                }
                            }
                        }
                    }

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Calificaciones Actuales", style = MaterialTheme.typography.titleMedium)
                            tiposPrueba.forEach { tipo ->
                                NotaItem(
                                    tipo.nombreTipo,
                                    *(calPorTipo[tipo]?.map { (it.calificacionObtenida?.toString() ?: "-") }?.toTypedArray() ?: arrayOf()),
                                    emoji = "⭐"
                                )
                            }
                            Button(
                                onClick = { onScreenSelected("SimuladoCalificacion/$cursoId") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3355ff),
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, Color(0xFF3355ff))
                            ) {
                                Text("Simulador de notas")
                            }
                        }
                    }

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Promedios por Tipo", style = MaterialTheme.typography.titleMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                promediosPorTipo.forEach { (tipo, promedio) ->
                                    PromedioItem(tipo, String.format("%.2f", promedio))
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Promedio Final",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                String.format("%.2f", promedioFinal),
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialogTipoPrueba) {
        DialogoAgregarTipoPrueba(
            showDialog = showDialogTipoPrueba,
            onDismiss = { showDialogTipoPrueba = false },
            onConfirm = { pruebaState ->
                val nuevoTipoPrueba = TipoPrueba(
                    idCurso = cursoId,
                    nombreTipo = pruebaState.tipo,
                    cantidadPruebas = pruebaState.numero.toIntOrNull() ?: 0,
                    pesoTotal = pruebaState.peso.toDoubleOrNull() ?: 0.0
                )
                tipoPruebaViewModel.agregarTipoPrueba(nuevoTipoPrueba)
                showDialogTipoPrueba = false
            }
        )
    }
}

@Composable
private fun DialogoAgregarTipoPrueba(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (PruebaDialogState) -> Unit
) {
    if (showDialog) {
        var state by remember { mutableStateOf(PruebaDialogState()) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Agregar Tipo de Prueba") },
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
                        label = { Text("Peso de la prueba (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
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

@Composable
private fun PesoItem(titulo: String, peso: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.bodyMedium
        )
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                peso,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun NotaItem(titulo: String, vararg notas: String, emoji: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                emoji,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            notas.forEachIndexed { index, nota ->
                ElevatedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp)
                    , colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Nota ${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF3355ff)
                        )
                        Text(
                            nota,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromedioItem(titulo: String, promedio: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                promedio,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}