package com.menfroyt.studyoso.presentation.calificacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    // Instancias de repos y viewmodels
    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))

    val tipoPruebaRepository = remember { TipoPruebaRepository(db.TipoPruebaDao()) }
    val tipoPruebaViewModel: TipoPruebaViewModel = viewModel(factory = TipoPruebaViewModelFactory(tipoPruebaRepository))

    val calificacionRepository = remember { CalificacionRepository(db.CalificacionDao()) }
    val calificacionViewModel: CalificacionViewModel = viewModel(factory = CalificacionViewModelFactory(calificacionRepository))

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

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown Tipo de Prueba
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
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandidoTipoPrueba,
                onDismissRequest = { expandidoTipoPrueba = false }
            ) {
                tiposPrueba.forEach { tipoPrueba ->
                    DropdownMenuItem(
                        text = { Text(tipoPrueba.nombreTipo) },
                        onClick = {
                            tipoPruebaSeleccionada = tipoPrueba
                            numeroPruebaSeleccionado = null
                            expandidoTipoPrueba = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown Número de Prueba
        ExposedDropdownMenuBox(
            expanded = expandidoNumeroPrueba,
            onExpandedChange = { expandidoNumeroPrueba = it }
        ) {
            OutlinedTextField(
                value = numeroPruebaSeleccionado?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = tipoPruebaSeleccionada != null,
                label = { Text("Número de Prueba") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoNumeroPrueba) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
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
                            numeroPruebaSeleccionado = numero
                            expandidoNumeroPrueba = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = calificacionTexto,
            onValueChange = { calificacionTexto = it },
            label = { Text("Calificación") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Validaciones básicas
                if (cursoSeleccionado == null || tipoPruebaSeleccionada == null ||
                    numeroPruebaSeleccionado == null || calificacionTexto.isBlank()
                ) {
                    // Aquí puedes mostrar un Snackbar o Toast con error
                    return@Button
                }

                val calificacionValor = calificacionTexto.toDoubleOrNull()
                if (calificacionValor == null) {
                    // Mostrar error de número inválido
                    return@Button
                }

                // Crear objeto Calificacion
                val nuevaCalificacion = Calificacion(
                    idCurso = cursoSeleccionado!!.idCurso,
                    idTipoPrueba = tipoPruebaSeleccionada!!.idTipoPrueba,
                    numeroPrueba = numeroPruebaSeleccionado!!,
                    calificacionObtenida = calificacionValor
                )

                // Insertar calificación a través del ViewModel
                calificacionViewModel.insertarCalificacion(nuevaCalificacion)

                // Navegar a lista de calificaciones o pantalla deseada
                onScreenSelected("ListCalificaciones")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = cursoSeleccionado != null &&
                    tipoPruebaSeleccionada != null &&
                    numeroPruebaSeleccionado != null &&
                    calificacionTexto.isNotBlank(),
        ) {
            Text("Guardar Calificación")
        }
    }
}

