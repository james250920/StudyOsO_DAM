package com.menfroyt.studyoso.presentation.curso

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Curso
import com.menfroyt.studyoso.data.repositories.CursoRepository
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarCursosScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit,
    usuarioId: Int
) {

    val modalidades = listOf(
        "Presencial", "Virtual", "Híbrido"
    )
    var expandedModalidad by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(
        factory = CursoViewModelFactory(cursoRepository)
    )

    val scrollState = rememberScrollState()
    var nombreCurso by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Red) }
    var profesor by remember { mutableStateOf("") }
    var creditos by remember { mutableStateOf("") }
    var aula by remember { mutableStateOf("") }
    var showColorPicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(showError) {
        if (showError) {
            snackbarHostState.showSnackbar(errorMessage)
            showError = false
        }
    }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                IconButton(
                onClick = { onScreenSelected("lisCurso") },
                modifier = Modifier
                    .padding(start = 0.dp, end = 8.dp),
                enabled = !isLoading,
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
                    text = "Agregar Curso",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )}


            Spacer(modifier = Modifier.height(4.dp))

            InputField(
                label = "Nombre del Curso",
                value = nombreCurso,
                onValueChange = { nombreCurso = it }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Color del curso",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(selectedColor, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable { showColorPicker = true }
                )
            }

            InputField(
                label = "Profesor",
                value = profesor,
                onValueChange = { profesor = it }
            )

            InputField(
                label = "Créditos",
                value = creditos,
                onValueChange = { creditos = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            ExposedDropdownMenuBox(
                expanded = expandedModalidad,
                onExpandedChange = { expandedModalidad = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = aula,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedModalidad) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    label = { Text("Modalidad") }
                )
                ExposedDropdownMenu(
                    expanded = expandedModalidad,
                    onDismissRequest = { expandedModalidad = false }
                ) {
                    modalidades.forEach { modalidad ->
                        DropdownMenuItem(
                            text = { Text(modalidad) },
                            onClick = {
                                aula = modalidad
                                expandedModalidad = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (nombreCurso.isBlank()) {
                        errorMessage = "El nombre del curso es obligatorio"
                        showError = true
                        return@Button
                    }

                    val creditosInt = creditos.toIntOrNull()
                    if (creditosInt == null && creditos.isNotEmpty()) {
                        errorMessage = "Los créditos deben ser un número válido"
                        showError = true
                        return@Button
                    }

                    isLoading = true
                    try {
                        val curso = Curso(
                            nombreCurso = nombreCurso.trim(), // Cambiar nombreCurso por nombre
                            color = "#" + Integer.toHexString(selectedColor.toArgb())
                                .uppercase().substring(2),
                            profesor = profesor.trim(),
                            creditos = creditosInt ?: 0,
                            aula = aula.trim(),
                            idUsuario = usuarioId
                        )

                        cursoViewModel.agregarCurso(curso)
                        onScreenSelected("lisCurso")
                    } catch (e: Exception) {
                        errorMessage = "Error al guardar el curso"
                        showError = true
                    } finally {
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3355ff),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar Curso")
                }
            }
        }

        if (showColorPicker) {
            ColorPickerDialog(
                showDialog = true,
                initialColor = selectedColor,
                onColorSelected = {
                    selectedColor = it
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }
    }




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        keyboardOptions = keyboardOptions
    )
}

@Composable
private fun ColorPickerDialog(
    showDialog: Boolean,
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        var selectedColor by remember { mutableStateOf(initialColor) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Seleccionar color") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(selectedColor)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(8.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SimpleColorPicker(
                        initialColor = selectedColor,
                        onColorSelected = { color ->
                            selectedColor = color
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onColorSelected(selectedColor)
                        onDismiss()
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SimpleColorPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color.Red, Color.Blue, Color.Green,
        Color(0xFFf60a87), Color.Cyan, Color.Magenta,
        Color.Gray, Color.DarkGray, Color.LightGray,
        Color(0xFF6200EE), Color(0xFF03DAC5), Color(0xFFFFC107),
        Color(0xFFFF5722), Color(0xFF9C27B0), Color(0xFF2196F3),
        Color(0xFF4CAF50), MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(colors.size) { index ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colors[index], CircleShape)
                    .border(
                        width = if (colors[index] == initialColor) 2.dp else 1.dp,
                        color = if (colors[index] == initialColor)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(colors[index]) }
            )
        }
    }
}