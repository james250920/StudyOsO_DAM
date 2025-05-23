package com.menfroyt.studyoso.presentation.calificación

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.menfroyt.studyoso.domain.TipoPrueba



@Composable
fun SimuladorCalificacionesScreen(modifier: Modifier = Modifier, onScreenSelected: (String) -> Unit) {
    var tipoPruebas = remember {
        listOf(
            TipoPrueba(
                id = 1,
                nombre = "PCs",
                numPruebas = 3,
                cursoId = 1,
                peso = 0.20
            ),
            TipoPrueba(
                id = 2,
                nombre = "CLs",
                numPruebas = 3,
                cursoId = 1,
                peso = 0.20
            ),
            TipoPrueba(
                id = 3,
                nombre = "Ex. Parcial",
                numPruebas = 1,
                cursoId = 1,
                peso = 0.30
            ),
            TipoPrueba(
                id = 4,
                nombre = "Ex. Final",
                numPruebas = 1,
                cursoId = 1,
                peso = 0.30
            )
        )
    }

    // Estado para almacenar los promedios de cada tipo de prueba
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
                        text = "Programación",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Simulador de notas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onScreenSelected("Calificaciones") },
                        shape = RoundedCornerShape(12.dp)
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
            tipoPruebas.forEach { tipoPrueba ->
                TipoPruebaCard(
                    tipoPrueba = tipoPrueba,
                    onPromedioCalculado = { promedio ->
                        promedios = promedios + (tipoPrueba.id to promedio)
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
    var notas by remember { mutableStateOf(List(tipoPrueba.numPruebas) { "" }) }

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
                    text = tipoPrueba.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${(tipoPrueba.peso * 100).toInt()}%",
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
                                onPromedioCalculado(calcularPromedio(notas, tipoPrueba.peso))
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

            val promedio = calcularPromedio(notas, tipoPrueba.peso)
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

private fun calcularPromedio(notas: List<String>, peso: Double): Double {
    val notasValidas = notas.mapNotNull { it.toFloatOrNull() }
    if (notasValidas.isEmpty()) return 0.0

    val promedioParcial = notasValidas.sum() / notasValidas.size
    return promedioParcial * peso
}