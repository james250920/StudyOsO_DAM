package com.menfroyt.studyoso.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menfroyt.studyoso.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiaScreen(
    modifier: Modifier = Modifier,

) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "¿Cómo usar esta aplicación?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sigue estos pasos para aprovechar todas las funciones",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val pasos = listOf(
                Paso(
                    numero = 1,
                    titulo = "Ingresar al curso",
                    descripcion = "Selecciona la opción \"Cursos\" en el menú principal para ver tus cursos.",
                    icono = Icons.Default.Menu
                ),
                Paso(
                    numero = 2,
                    titulo = "Agregar un nuevo curso",
                    descripcion = "Presiona el botón + que aparece en la esquina inferior derecha para añadir un nuevo curso.",
                    icono = Icons.Default.Add
                ),
                Paso(
                    numero = 3,
                    titulo = "Seleccionar un curso",
                    descripcion = "El curso aparecerá en la lista. Tócalo para ver todos los detalles.",
                    icono = Icons.Default.List
                ),
                Paso(
                    numero = 4,
                    titulo = "Configurar el horario",
                    descripcion = "En la vista de detalles, configura primero el horario del curso.",
                    icono = Icons.Default.Schedule
                ),
                Paso(
                    numero = 5,
                    titulo = "Agregar pruebas",
                    descripcion = "Después de configurar el horario, agrega las pruebas correspondientes al curso.",
                    icono = Icons.Default.Assignment
                ),
                Paso(
                    numero = 6,
                    titulo = "Registrar calificaciones",
                    descripcion = "Finalmente, añade las calificaciones para cada prueba para completar el registro.",
                    icono = Icons.Default.Grade
                )
            )

            items(pasos) { paso ->
                PasoItem(paso)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Información",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "¡Importante!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Para un funcionamiento óptimo, asegúrate de seguir el orden sugerido: primero configura el horario, luego agrega las pruebas y finalmente registra las calificaciones.",
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }


data class Paso(
    val numero: Int,
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector
)

@Composable
fun PasoItem(paso: Paso) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = paso.numero.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = paso.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = paso.descripcion,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = paso.icono,
            contentDescription = paso.titulo,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
        )
    }

    if (paso.numero < 6) {
        Box(
            modifier = Modifier
                .padding(start = 24.dp)
                .height(32.dp)
                .width(2.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}