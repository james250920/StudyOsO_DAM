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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.HsvColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarCursosScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var nombreCurso by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Red) }
    var profesor by remember { mutableStateOf("") }
    var creditos by remember { mutableStateOf("") }
    var aula by remember { mutableStateOf("") }
    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.AddToPhotos,
            contentDescription = "Agregar Curso",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Agregar Nuevo Curso",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        InputField(
            label = "Nombre del Curso",
            value = nombreCurso,
            onValueChange = { nombreCurso = it }
        )

        // Selector de color
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Seleccionar color del curso",
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

        InputField(
            label = "Aula",
            value = aula,
            onValueChange = { aula = it }
        )
        Button(
            onClick = { onScreenSelected("lisCurso") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Guardar Curso")
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            showDialog = true,
            initialColor = selectedColor,
            onColorSelected = { color ->
                selectedColor = color
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
    Column(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 4.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions
        )
    }
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
                Button(
                    onClick = {
                        onColorSelected(selectedColor)
                        onDismiss()
                    }
                ) {
                    Text("Aceptar")
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
private fun SimpleColorPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow,
        Color.Cyan,
        Color.Magenta,
        Color.Gray,
        Color.DarkGray,
        Color.LightGray,
        Color.Black,
        Color.White,
        Color(0xFF6200EE),
        Color(0xFF03DAC5),
        Color(0xFFFFC107),
        Color(0xFFFF5722),
        Color(0xFF9C27B0),
        Color(0xFF2196F3),
        Color(0xFF4CAF50),
        MaterialTheme.colorScheme.primary,
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