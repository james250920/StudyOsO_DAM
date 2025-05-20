package com.menfroyt.studyoso.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.menfroyt.studyoso.R

@Composable
fun PrincipalScreen(
    modifier: Modifier = Modifier,
    onScreenSelected: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp).padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(
                icon = Icons.Filled.AddToPhotos,
                text = "Agregar cursos",
                onClick = { onScreenSelected("AgregarCursos") }
            )
            MenuItem(
                icon = Icons.Filled.EmojiEvents,
                text = "Calificaciones",
                onClick = { onScreenSelected("Calificaciones") }
            )
            MenuItem(
                icon = Icons.Filled.FormatListNumbered,
                text = "Lista de tareas",
                onClick = { onScreenSelected("ListaTareas") }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.study),
            contentDescription = "Logo Study Oso",
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp ).padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(
                icon = Icons.Filled.Timer,
                text = "Pomodoro",
                onClick = { onScreenSelected("Pomodoro") }
            )
            MenuItem(
                icon = Icons.Filled.Dataset,
                text = "Matriz de Eisenhower",
                onClick = { onScreenSelected("MatrizEisenhower") }
            )
            MenuItem(
                icon = Icons.Filled.CalendarMonth,
                text = "Calendario",
                onClick = { onScreenSelected("Calendario") }
            )
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF83B5FF),
                modifier = Modifier.size(64.dp)
            )
        }
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}