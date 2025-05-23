package com.menfroyt.studyoso.presentation.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(modifier: Modifier = Modifier, onScreenSelected: (String) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardCard(
                    title = "Tareas Pendientes",
                    value = "5",
                    icon = Icons.Filled.Assignment,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onScreenSelected("ListaTareas") }
                )
            }

            item {
                DashboardCard(
                    title = "Promedio General",
                    value = "4.2",
                    icon = Icons.Filled.Grade,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { onScreenSelected("ListCalificaciones") }
                )
            }

            item {
                DashboardCard(
                    title = "Cursos Activos",
                    value = "7",
                    icon = Icons.Filled.School,
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = { onScreenSelected("lisCurso") }
                )
            }

            item {
                DashboardCard(
                    title = "Tiempo Estudiado",
                    value = "2h 30m",
                    icon = Icons.Filled.Timer,
                    color = MaterialTheme.colorScheme.error,
                    onClick = { onScreenSelected("Pomodoro") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ProximasTareasCard(
            modifier = Modifier.fillMaxWidth(),
            onVerMasClick = { onScreenSelected("ListaTareas") }
        )
    }
}

@Composable
private fun DashboardCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ProximasTareasCard(
    modifier: Modifier = Modifier,
    onVerMasClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Próximas Tareas",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onVerMasClick) {
                    Text("Ver más")
                }
            }

            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                items(3) {
                    ListItem(
                        headlineContent = { Text("Tarea ${it + 1}") },
                        supportingContent = { Text("Fecha de entrega") },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Assignment,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}