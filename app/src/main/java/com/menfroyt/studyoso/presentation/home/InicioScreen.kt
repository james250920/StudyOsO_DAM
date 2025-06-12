package com.menfroyt.studyoso.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(
                icon = Icons.Filled.AddToPhotos,
                text = "Cursos",
                onClick = { onScreenSelected("lisCurso") }
            )
            MenuItem(
                icon = Icons.Filled.EmojiEvents,
                text = "Calificaciones",
                onClick = { onScreenSelected("ListCalificaciones") }
            )
            MenuItem(
                icon = Icons.Filled.FormatListNumbered,
                text = "Tareas",
                onClick = { onScreenSelected("ListaTareas") }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = painterResource(id = R.drawable.study),
            contentDescription = "Logo Study Oso",
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
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
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(70.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF3355ff),
                modifier = Modifier.size(65.dp)
            )
        }
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}