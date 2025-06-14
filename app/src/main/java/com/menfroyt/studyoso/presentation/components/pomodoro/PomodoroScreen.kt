package com.menfroyt.studyoso.presentation.components.pomodoro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MatrizEisenhowerScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Matriz de Eisenhower",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Cuadrante 1: Urgente e Importante
            CuadranteMatriz(
                titulo = "Hacer",
                descripcion = "Urgente e Importante",
                color = Color(0xFFFF6B6B),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )

            // Cuadrante 2: No Urgente e Importante
            CuadranteMatriz(
                titulo = "Programar",
                descripcion = "No Urgente e Importante",
                color = Color(0xFF4ECDC4),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Cuadrante 3: Urgente y No Importante
            CuadranteMatriz(
                titulo = "Delegar",
                descripcion = "Urgente y No Importante",
                color = Color(0xFFFFBE0B),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )

            // Cuadrante 4: No Urgente y No Importante
            CuadranteMatriz(
                titulo = "Eliminar",
                descripcion = "No Urgente y No Importante",
                color = Color(0xFF95A5A6),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun CuadranteMatriz(
    titulo: String,
    descripcion: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}