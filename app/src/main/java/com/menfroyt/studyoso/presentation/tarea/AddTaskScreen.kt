package com.menfroyt.studyoso.presentation.tarea

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import esan.mendoza.teststudyoso.ViewModel.curso.CursoViewModel
import esan.mendoza.teststudyoso.ViewModel.curso.CursoViewModelFactory
import esan.mendoza.teststudyoso.ViewModel.tarea.TareaViewModel
import esan.mendoza.teststudyoso.ViewModel.tarea.TareaViewModelFactory
import esan.mendoza.teststudyoso.data.db.AppDatabase
import esan.mendoza.teststudyoso.data.entities.Curso
import esan.mendoza.teststudyoso.data.entities.Tarea
import esan.mendoza.teststudyoso.data.repositories.CursoRepository
import esan.mendoza.teststudyoso.data.repositories.TareaRepository
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    usuarioId: Int
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))

    val tareaRepository = remember { TareaRepository(db.TareaDao()) }
    val tareaViewModel: TareaViewModel = viewModel(factory = TareaViewModelFactory(tareaRepository))

    var cursos by remember { mutableStateOf(emptyList<Curso>()) }
    var cursoSeleccionado by remember { mutableStateOf<Curso?>(null) }

    var expandidoCurso by remember { mutableStateOf(false) }
    var descripcionTarea by remember { mutableStateOf("") }
    var esImportante by remember { mutableStateOf<Boolean?>(null) }
    var esUrgente by remember { mutableStateOf<Boolean?>(null) }
    var fechaVencimiento by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(showError) {
        if (showError) {
            snackbarHostState.showSnackbar(errorMessage)
            showError = false
        }
    }

    // Cargar cursos para el usuario
    LaunchedEffect(usuarioId) {
        cursoViewModel.cargarCursos(usuarioId)
        cursoViewModel.cursos.collect { lista ->
            cursos = lista
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Permite seleccionar fechas desde hoy en adelante
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter =
                            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        fechaVencimiento = formatter.format(java.util.Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Agregar Tarea",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )
        // Dropdown Cursos
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
                    .menuAnchor()
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
                            expandidoCurso = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descripcionTarea,
            onValueChange = { descripcionTarea = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(text = "Descripción de la tarea") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "¿Es importante?", style = MaterialTheme.typography.bodyLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val opcionesImportante = listOf("Sí" to true, "No" to false)
            opcionesImportante.forEach { (texto, valor) ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = esImportante == valor,
                            onClick = { esImportante = valor })
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = esImportante == valor,
                        onClick = { esImportante = valor })
                    Text(text = texto)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "¿Es urgente?", style = MaterialTheme.typography.bodyLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val opcionesUrgente = listOf("Sí" to true, "No" to false)
            opcionesUrgente.forEach { (texto, valor) ->
                Row(
                    modifier = Modifier
                        .selectable(selected = esUrgente == valor, onClick = { esUrgente = valor })
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = esUrgente == valor, onClick = { esUrgente = valor })
                    Text(text = texto)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fechaVencimiento,
            onValueChange = { fechaVencimiento = it },
            label = { Text("Fecha de vencimiento") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    cursoSeleccionado == null -> {
                        errorMessage = "Selecciona un curso"
                        showError = true
                    }

                    descripcionTarea.isBlank() -> {
                        errorMessage = "La descripción es obligatoria"
                        showError = true
                    }

                    esImportante == null -> {
                        errorMessage = "Indica si la tarea es importante"
                        showError = true
                    }

                    esUrgente == null -> {
                        errorMessage = "Indica si la tarea es urgente"
                        showError = true
                    }

                    fechaVencimiento.isBlank() -> {
                        errorMessage = "Selecciona la fecha de vencimiento"
                        showError = true
                    }

                    else -> {
                        val nuevaTarea = Tarea(
                            descripcion = descripcionTarea.trim(),
                            esImportante = esImportante!!,
                            esUrgente = esUrgente!!,
                            fechaVencimiento = fechaVencimiento,
                            fechaCreacion = LocalDate.now().toString(),
                            estado = "Pendiente",
                            idUsuario = usuarioId,
                            idCurso = cursoSeleccionado!!.idCurso
                        )
                        tareaViewModel.agregarTarea(nuevaTarea)
                        onScreenSelected("ListTaskScreen")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Guardar")
        }
    }
}



