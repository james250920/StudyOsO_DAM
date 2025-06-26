package com.menfroyt.studyoso.presentation.calificacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModel
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.TipoPrueba
import com.menfroyt.studyoso.data.repositories.TipoPruebaRepository


@Composable
fun SimuladorCalificacionesScreen(
    modifier: Modifier = Modifier,
    cursoId: Int,
    onScreenSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val tipoPruebaRepository = remember { TipoPruebaRepository(db.TipoPruebaDao()) }
    val tipoPruebaViewModel: TipoPruebaViewModel = viewModel(factory = TipoPruebaViewModelFactory(tipoPruebaRepository))
    val tiposPrueba by tipoPruebaViewModel.tiposPrueba.collectAsState()

    // Cargar los tipos de prueba reales del curso
    LaunchedEffect(cursoId) {
        tipoPruebaViewModel.cargarTiposPorCurso(cursoId)
    }

    var promedios by remember { mutableStateOf(mapOf<Int, Double>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Simulador de Notas",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onScreenSelected("DetalleCalificaciones/$cursoId") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3355ff),
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                    ) {
                        Icon(
                            Icons.Filled.ArrowBackIosNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Volver",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tiposPrueba.forEach { tipoPrueba ->
                TipoPruebaCard(
                    tipoPrueba = tipoPrueba,
                    onPromedioCalculado = { promedio ->
                        promedios = promedios + (tipoPrueba.idTipoPrueba to promedio)
                    }
                )
            }

            PromedioFinalCard(promedios = promedios.values.sum())
        }
    }
}

@Composable
private fun TipoPruebaCard(
    tipoPrueba: TipoPrueba,
    onPromedioCalculado: (Double) -> Unit
) {
    var notas by remember { mutableStateOf(List(tipoPrueba.cantidadPruebas) { "" }) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tipoPrueba.nombreTipo,
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${tipoPrueba.pesoTotal}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                notas.forEachIndexed { index, nota ->
                    OutlinedTextField(
                        value = nota,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || (newValue.toFloatOrNull() != null && newValue.toFloat() <= 20)) {
                                notas = notas.toMutableList().also { it[index] = newValue }
                                onPromedioCalculado(calcularPromedio(notas, tipoPrueba.pesoTotal, tipoPrueba.cantidadPruebas))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Nota ${index + 1}") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            val promedio = calcularPromedio(notas, tipoPrueba.pesoTotal, tipoPrueba.cantidadPruebas)
            if (promedio > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Promedio: ${"%.2f".format(promedio)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PromedioFinalCard(promedios: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Promedio Final",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                "%.2f".format(promedios),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// Se asume pesoTotal en porcentaje (ej. 20 para 20%).
private fun calcularPromedio(notas: List<String>, pesoTotal: Double, cantidadPruebas: Int): Double {
    val notasValidas = notas.mapNotNull { it.toFloatOrNull() }
    if (notasValidas.isEmpty() || cantidadPruebas == 0) return 0.0
    val promedioParcial = notasValidas.sum() / cantidadPruebas
    return promedioParcial * (pesoTotal / 100)
}
