package com.menfroyt.studyoso.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menfroyt.studyoso.R
import com.menfroyt.studyoso.presentation.utils.EisenhowerColors


@Composable
fun MatrizEisenhowerScreen(modifier: Modifier = Modifier, onScreenSelected: (String) -> Unit) {

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header mejorado
            Text(
                text = "Matriz de Eisenhower",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Subtítulo explicativo
            Text(
                text = "Organiza tus tareas por prioridad e importancia",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Botón mejorado con iconos
            ElevatedButton(
                onClick = { onScreenSelected("AddTaskScreen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Agregar Nueva Tarea",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Leyenda de colores
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Guía de prioridades:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LegendItem("Hacer ahora", EisenhowerColors.UrgenteImportante)
                        LegendItem("Planificar", EisenhowerColors.NoUrgenteImportante)
                        LegendItem("Delegar", EisenhowerColors.UrgenteNoImportante)
                        LegendItem("Eliminar", EisenhowerColors.NoUrgenteNoImportante)
                    }
                }
            }

            // Primera fila de cuadrantes con mejor espaciado
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cuadrante 1: Urgente e Importante
                CuadranteMatriz(
                    titulo = "Hacer Ahora",
                    descripcion = "Urgente e Importante",
                    color = EisenhowerColors.UrgenteImportante,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    imageVector = painterResource(id = R.drawable.c1),
                    onClick = { onScreenSelected("ListTaskScreen") }
                )

                // Cuadrante 2: No Urgente e Importante
                CuadranteMatriz(
                    titulo = "Planificar",
                    descripcion = "No Urgente e Importante",
                    color = EisenhowerColors.NoUrgenteImportante,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    imageVector = painterResource(id = R.drawable.c2),
                    onClick = { onScreenSelected("ListTaskScreen") }
                )
            }

            // Segunda fila de cuadrantes
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cuadrante 3: Urgente y No Importante
                CuadranteMatriz(
                    titulo = "Delegar",
                    descripcion = "Urgente y No Importante",
                    color = EisenhowerColors.UrgenteNoImportante,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    imageVector = painterResource(id = R.drawable.c3),
                    onClick = { onScreenSelected("ListTaskScreen") }
                )

                // Cuadrante 4: No Urgente y No Importante
                CuadranteMatriz(
                    titulo = "Eliminar",
                    descripcion = "No Urgente y No Importante",
                    color = EisenhowerColors.NoUrgenteNoImportante,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    imageVector = painterResource(id = R.drawable.c4),
                    onClick = { onScreenSelected("ListTaskScreen") }
                )
            }
        }
    }
}

@Composable
private fun LegendItem(text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CuadranteMatriz(
    titulo: String,
    descripcion: String,
    color: Color,
    modifier: Modifier = Modifier,
    imageVector: Painter,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable { onClick() }
            ,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(2.dp, color.copy(alpha = 0.3f)),

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título con mejor tipografía
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.25.sp
                ),
                color = color.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(6.dp))

            // Descripción mejorada
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Imagen con mejor presentación
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.1f)
            ) {
                Image(
                    painter = imageVector,
                    contentDescription = titulo,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}