package com.menfroyt.studyoso.presentation.calificación

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.menfroyt.studyoso.domain.TipoPrueba

data class Curso(
    val id: Int = 0,
    val nombreCurso: String,
    val profesor: String,
    val aula: String,
    val pruebas: List<TipoPrueba> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AgregarCalificacionScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit
) {
    val cursos = remember {
        listOf(
            Curso(
                id = 1,
                nombreCurso = "Matemática",
                profesor = "Juan Pérez",
                aula = "A-101",
                pruebas = listOf(
                    TipoPrueba(1, "PCs", 3, 1, 0.3),
                    TipoPrueba(2, "CLs", 3, 1, 0.3),
                    TipoPrueba(3, "Ex.P", 1, 1, 0.2),
                    TipoPrueba(4, "Ex.F", 1, 1, 0.2)
                )
            ),
            Curso(
                id = 2,
                nombreCurso = "Física",
                profesor = "María García",
                aula = "B-203",
                pruebas = listOf(
                    TipoPrueba(5, "PCs", 4, 2, 0.4),
                    TipoPrueba(6, "CLs", 2, 2, 0.2),
                    TipoPrueba(7, "Ex.P", 1, 2, 0.2),
                    TipoPrueba(8, "Ex.F", 1, 2, 0.2)
                )
            ),
            Curso(
                id = 3,
                nombreCurso = "Química",
                profesor = "Carlos López",
                aula = "C-305",
                pruebas = listOf(
                    TipoPrueba(9, "PCs", 2, 3, 0.2),
                    TipoPrueba(10, "CLs", 2, 3, 0.2),
                    TipoPrueba(11, "Ex.P", 1, 3, 0.3),
                    TipoPrueba(12, "Ex.F", 1, 3, 0.3)
                )
            )
        )
    }

    var cursoSeleccionado by remember { mutableStateOf<Curso?>(null) }
    var pruebaSeleccionada by remember { mutableStateOf<TipoPrueba?>(null) }
    var numeroPruebaSeleccionado by remember { mutableStateOf<Int?>(null) }
    var expandidoCurso by remember { mutableStateOf(false) }
    var expandidoPrueba by remember { mutableStateOf(false) }
    var expandidoNumero by remember { mutableStateOf(false) }
    var calificacion by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Agregar Calificación",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
                    .menuAnchor()  // Agregado este modificador
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )

            ExposedDropdownMenu(
                expanded = expandidoCurso,
                onDismissRequest = { expandidoCurso = false }
            ) {
                cursos.forEach { curso ->
                    DropdownMenuItem(
                        text = { Text(curso.nombreCurso) },
                        onClick = {
                            cursoSeleccionado = curso
                            pruebaSeleccionada = null
                            numeroPruebaSeleccionado = null
                            expandidoCurso = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandidoPrueba,
            onExpandedChange = { expandidoPrueba = it }
        ) {
            OutlinedTextField(
                value = pruebaSeleccionada?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = cursoSeleccionado != null,
                label = { Text("Seleccionar Prueba") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoPrueba) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )

            ExposedDropdownMenu(
                expanded = expandidoPrueba,
                onDismissRequest = { expandidoPrueba = false }
            ) {
                cursoSeleccionado?.pruebas?.forEach { prueba ->
                    DropdownMenuItem(
                        text = { Text(prueba.nombre) },
                        onClick = {
                            pruebaSeleccionada = prueba
                            numeroPruebaSeleccionado = null
                            expandidoPrueba = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandidoNumero,
            onExpandedChange = { expandidoNumero = it }
        ) {
            OutlinedTextField(
                value = numeroPruebaSeleccionado?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = pruebaSeleccionada != null,
                label = { Text("Número de Prueba") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoNumero) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
                )
            )

            ExposedDropdownMenu(
                expanded = expandidoNumero,
                onDismissRequest = { expandidoNumero = false }
            ) {
                List(pruebaSeleccionada?.numPruebas ?: 0) { index ->
                    val numero = index + 1
                    DropdownMenuItem(
                        text = { Text(numero.toString()) },
                        onClick = {
                            numeroPruebaSeleccionado = numero
                            expandidoNumero = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = calificacion,
            onValueChange = { calificacion = it },
            label = { Text("Calificación") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onScreenSelected("ListCalificaciones") },
            modifier = Modifier.fillMaxWidth(),
            enabled = cursoSeleccionado != null &&
                    pruebaSeleccionada != null &&
                    numeroPruebaSeleccionado != null &&
                    calificacion.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Guardar Calificación")
        }
    }
}