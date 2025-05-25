package com.menfroyt.studyoso.presentation.tarea
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.menfroyt.studyoso.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(modifier: Modifier = Modifier) {

    val cursos = listOf("Matemática", "Física", "Química")
    var expandidoCurso by remember { mutableStateOf(false) }
    var cursoSeleccionado by remember { mutableStateOf("") }

    var descripcionTarea by remember { mutableStateOf("") }

    var esImportante by remember { mutableStateOf<Boolean?>(null) }
    var esUrgente by remember { mutableStateOf<Boolean?>(null) }

    val contexto = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start

    ) {
        Image(
            painter = painterResource(id = R.drawable.addtaskme),
            contentDescription = "Logo Study Oso",
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Dropdown para cursos
        Text(text = "Curso", style = MaterialTheme.typography.bodyLarge)
        ExposedDropdownMenuBox(
            expanded = expandidoCurso,
            onExpandedChange = { expandidoCurso = !expandidoCurso }
        ) {
            OutlinedTextField(
                value = cursoSeleccionado,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCurso) },
                label = { Text("Seleccionar") }
            )
            ExposedDropdownMenu(
                expanded = expandidoCurso,
                onDismissRequest = { expandidoCurso = false }
            ) {
                cursos.forEach { curso ->
                    DropdownMenuItem(
                        text = { Text(curso) },
                        onClick = {
                            cursoSeleccionado = curso
                            expandidoCurso = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción de la tarea
        Text(text = "Descripción de la tarea", style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = descripcionTarea,
            onValueChange = { descripcionTarea = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(text = "Tarea de desarrollo móvil") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ¿Es importante?
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
                        .selectable(selected = esImportante == valor, onClick = { esImportante = valor })
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = esImportante == valor, onClick = { esImportante = valor })
                    Text(text = texto)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ¿Es urgente?
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Lógica para agregar la tarea
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Agregar")
        }
    }
}
