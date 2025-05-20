package com.menfroyt.studyoso.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.menfroyt.studyoso.R

@Composable
fun PrincipalScreen(
    modifier: Modifier = Modifier,
    ) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(icon = Icons.Filled.AddToPhotos, text = "Agregar cursos")
            MenuItem(icon = Icons.Filled.EmojiEvents, text = "Calificaciones")
            MenuItem(icon = Icons.Filled.FormatListNumbered, text = "Lista de tareas")
        }

        Image(
            painter = painterResource(id = R.drawable.study),
            contentDescription = "Logo Study Oso",
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(icon = Icons.Filled.Timer, text = "Pomodoro")
            MenuItem(icon = Icons.Filled.ViewModule, text = "Matriz de Eisenhower")
            MenuItem(icon = Icons.Filled.CalendarToday, text = "Calendario")
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, text: String, ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 40.dp, bottom = 40.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF83B5FF),
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
            style = TextStyle(
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                fontSize = 16.sp
            )


        )
    }
}