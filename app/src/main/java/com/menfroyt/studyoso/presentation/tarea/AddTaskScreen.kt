package com.menfroyt.studyoso.presentation.tarea
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.ViewModel.tarea.TareaViewModel
import com.menfroyt.studyoso.ViewModel.tarea.TareaViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.entities.Tarea
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.TareaRepository

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            IconButton(
                onClick = { onScreenSelected("ListTaskScreen") },
                modifier = Modifier
                    .padding(start = 0.dp, end = 8.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF3355ff),

                    )


            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Regresar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Agregar Tarea",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )
        }
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
                        onClick = { esImportante = valor },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF3355ff),
                            unselectedColor = Color(0xFF3355ff).copy(alpha = 0.6f)
                        )
                        )
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
                    RadioButton(
                        selected = esUrgente == valor,
                        onClick = { esUrgente = valor },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF3355ff),
                            unselectedColor = Color(0xFF3355ff).copy(alpha = 0.6f)
                        )
                    )
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
                        contentDescription = "Seleccionar fecha",
                        tint = Color(0xFF3355ff)
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
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3355ff),
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Guardar")
        }
    }
}
