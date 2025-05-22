package com.menfroyt.studyoso.presentation.curso

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FormatListNumberedRtl
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class HorarioDialogState(
    val dia: String = "",
    val horaInicio: String = "",
    val horaFin: String = ""
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

@Composable
fun DetalleCursoScreen(modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
                       ) {
    val scrollState = rememberScrollState()


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CursoHeader(
            modifier = Modifier.fillMaxWidth(),
            onScreenSelected = onScreenSelected
        )
        CursoInfo()
        HorarioSection()
        PruebasSection()
        PromedioTotal()
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CursoHeader(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Book,
            contentDescription = "Icono del curso",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Programación",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { onScreenSelected("lisCurso") },
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.FormatListNumberedRtl,
                contentDescription = "Regresar",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

    }
}

@Composable
private fun CursoInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Aula: 205",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Profesor: Juan Pérez",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun HorarioSection() {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),

    ) {
        Text(
            text = "Horario",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HorarioCard("Lunes", "08:00", "10:00", Modifier.weight(1f))
            HorarioCard("Martes", "08:00", "10:00", Modifier.weight(1f))
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Agregar horario")
        }

        DialogoAgregarHorario(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onConfirm = { horario ->
                showDialog = false
            }
        )
    }
}

@Composable
private fun HorarioCard(
    dia: String,
    horaInicio: String,
    horaFin: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp),  // Altura fija para consistencia
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dia,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = horaInicio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = " - ",
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = horaFin,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun PruebasSection() {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pruebas",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Agregar Prueba")
            }
        }

        PruebaCard()

        DialogoAgregarPrueba(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onConfirm = { prueba ->
                showDialog = false
            }
        )
    }
}

@Composable
private fun PruebaCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TipoPrueba(
                titulo = "PCs",
                numeros = listOf("1", "2", "3")
            )

            TipoPrueba(
                titulo = "CLs",
                numeros = listOf("1", "2", "3")
            )

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Agregar calificación")
            }
        }
    }
}

@Composable
private fun TipoPrueba(
    titulo: String,
    numeros: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                numeros.forEach { numero ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = numero,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PromedioTotal() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Promedio Total",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Card(
                modifier = Modifier
                    .size(150.dp)
                    .aspectRatio(1f),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "5.4/20",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogoAgregarHorario(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (HorarioDialogState) -> Unit
) {
    if (showDialog) {
        var state by remember { mutableStateOf(HorarioDialogState()) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Agregar Horario") },
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
                        label = { Text("Hora inicio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.horaFin,
                        onValueChange = { state = state.copy(horaFin = it) },
                        label = { Text("Hora fin") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { onConfirm(state) }) {
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
                Button(onClick = { onConfirm(state) }) {
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
}