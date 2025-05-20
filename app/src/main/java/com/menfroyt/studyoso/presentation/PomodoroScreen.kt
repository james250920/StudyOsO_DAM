package com.menfroyt.studyoso.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun PomodoroScreen( modifier: Modifier) {
    var timeLeft by remember { mutableStateOf(25 * 60) } // 25 minutos en segundos
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pomodoro",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "Pausar" else "Iniciar")
            }
            Button(onClick = { timeLeft = 25 * 60; isRunning = false }) {
                Text("Reiniciar")
            }
        }
    }
}