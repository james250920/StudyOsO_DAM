package com.menfroyt.studyoso.presentation.curso

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun DetalleCursoScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    cursoId: Int
) {
    val context = LocalContext.current
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

    // Estado para el curso cargado
    var curso by remember { mutableStateOf<Curso?>(null) }
    val scrollState = rememberScrollState()

    // Cargar curso cuando cambie cursoId
    LaunchedEffect(cursoId) {
        curso = cursoViewModel.getCursoById(cursoId)
        horarioViewModel.cargarHorarios(cursoId)
        tipoPruebaViewModel.cargarTiposPorCurso(cursoId)
    }

    curso?.let { cursoActual ->
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
                onScreenSelected = onScreenSelected,
                cursoNombre = cursoActual.nombreCurso,
                cursoColor = cursoActual.color ?: "#FFFFFF"
            )
            CursoInfo(
                profesor = cursoActual.profesor ?: "N/A",
                aula = cursoActual.aula ?: "N/A"
            )
            HorarioSection(
                horarios = horarios,
                onAgregarHorario = { horarioState ->
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
                    horarioViewModel.actualizarHorario(horario)
                },
                onEliminarHorario = { horario ->
                    horarioViewModel.eliminarHorario(horario)
                },
                modifier = Modifier.fillMaxWidth()
            )
            PruebasSection(
                tiposPrueba = tiposPrueba,
                onAgregarTipoPrueba = { pruebaDialogState ->
                    val nuevoTipoPrueba = TipoPrueba(
                        idCurso = cursoId,
                        nombreTipo = pruebaDialogState.tipo,
                        cantidadPruebas = pruebaDialogState.numero.toIntOrNull() ?: 0,
                        pesoTotal = pruebaDialogState.peso.toDoubleOrNull() ?: 0.0
                    )
                    tipoPruebaViewModel.agregarTipoPrueba(nuevoTipoPrueba)
                },
                onEditarTipoPrueba = { tipoPrueba ->
                    tipoPruebaViewModel.actualizarTipoPrueba(tipoPrueba)
                },
                onEliminarTipoPrueba = { tipoPrueba ->
                    tipoPruebaViewModel.eliminarTipoPrueba(tipoPrueba)
                },
                onScreenSelected = onScreenSelected,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    } ?: run {
        // Opcional: mientras carga o no existe curso, muestra algo
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Cargando información del curso...")
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        IconButton(
            onClick = { onScreenSelected("lisCurso") },
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Regresar",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.padding(end = 10.dp))

        Icon(
            imageVector = Icons.Filled.Book,
            contentDescription = "Icono del curso",
            tint = Color(parseColor(cursoColor)),
            modifier = Modifier.size(40.dp)
        )

        Text(
            text = cursoNombre,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                ,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )


    }
}

@Composable
private fun CursoInfo(
    profesor: String,
    aula: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                text = "Profesor: $profesor",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Modalidad: $aula",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}




///section tipo de pruebas
@Composable
internal fun PruebasSection(
    tiposPrueba: List<TipoPrueba>,
    onAgregarTipoPrueba: (PruebaDialogState) -> Unit,
    onEditarTipoPrueba: (TipoPrueba) -> Unit,
    onEliminarTipoPrueba: (TipoPrueba) -> Unit,
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var tipoPruebaSeleccionado by remember { mutableStateOf<TipoPrueba?>(null) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxWidth(),
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

        if (tiposPrueba.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay pruebas agregadas",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tiposPrueba.forEach { tipo ->
                    TipoPruebaCard(
                        tipoPrueba = tipo,
                        modifier = Modifier.width(200.dp),
                        onEdit = { tipoPruebaSeleccionado = tipo },
                        onDelete = { onEliminarTipoPrueba(tipo) }
                    )
                }
            }
        }

        Button(
            onClick = { onScreenSelected("AgregarCalificacion") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Agregar calificación")
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

//section horario

@Composable
internal fun HorarioSection(
    horarios: List<Horario>,
    onAgregarHorario: (HorarioDialogState) -> Unit,
    onEditarHorario: (Horario) -> Unit,
    onEliminarHorario: (Horario) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var horarioSeleccionado by remember { mutableStateOf<Horario?>(null) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Horario",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (horarios.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay horarios agregados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                horarios.forEach { horario ->
                    HorarioCard(
                        dia = horario.diaSemana,
                        horaInicio = horario.horaInicio,
                        horaFin = horario.horaFin,
                        aula = horario.aula,
                        modifier = Modifier.width(160.dp),
                        onEdit = { horarioSeleccionado = horario },
                        onDelete = { onEliminarHorario(horario) }
                    )
                }
            }
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

@Composable
private fun HorarioCard(
    dia: String,
    horaInicio: String,
    horaFin: String,
    aula: String,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp),
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
                Text(
                    text = aula,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
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
            }
            )
            {
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
                            onCancel = { showTimePickerInicio = false },
                            onConfirm = {
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
                            },
                            content = { TimePicker(state = timePickerState) }
                        )
                    }

                    if (showTimePickerFin) {
                        val timePickerState = rememberTimePickerState()
                        TimePickerDialog(
                            onCancel = { showTimePickerFin = false },
                            onConfirm = {
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
                            },
                            content = { TimePicker(state = timePickerState) }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(state) },
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
}

@ExperimentalMaterial3Api
@Composable
private fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
        },
        text = { content() }
    )
}

