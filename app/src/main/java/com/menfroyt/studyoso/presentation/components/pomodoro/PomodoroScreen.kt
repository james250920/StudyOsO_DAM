package com.menfroyt.studyoso.presentation.components.pomodoro

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import esan.mendoza.teststudyoso.presentation.components.pomodoro.music.MusicListDialog
import esan.mendoza.teststudyoso.presentation.components.pomodoro.music.pomodoroSettingDialog
import kotlinx.coroutines.delay


@Composable
fun PomodoroScreen(
    modifier: Modifier = Modifier
) {
    val showMusicDialog = rememberSaveable { mutableStateOf(false) }
    val showPomodoroDialog = rememberSaveable { mutableStateOf(false) }

    // Estados del temporizador
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var currentSeconds by rememberSaveable { mutableStateOf(25 * 60) } // 25 minutos por defecto
    var focusTime by rememberSaveable { mutableStateOf(25) }
    var shortBreak by rememberSaveable { mutableStateOf(5) }
    var longBreak by rememberSaveable { mutableStateOf(15) }
    var longBreakInterval by rememberSaveable { mutableStateOf(4) }
    var sessionCount by rememberSaveable { mutableStateOf(0) }
    var isBreak by rememberSaveable { mutableStateOf(false) }

    // Efecto para el temporizador
    LaunchedEffect(isRunning) {
        while (isRunning && currentSeconds > 0) {
            delay(1000L)
            currentSeconds--

            // Cuando el temporizador llega a 0
            if (currentSeconds == 0) {
                isRunning = false
                sessionCount++

                // Determinar el siguiente intervalo
                if (!isBreak) {
                    isBreak = true
                    currentSeconds = if (sessionCount % longBreakInterval == 0) {
                        longBreak * 60
                    } else {
                        shortBreak * 60
                    }
                } else {
                    isBreak = false
                    currentSeconds = focusTime * 60
                }
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sesión ${sessionCount + 1}",
                    style = typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.padding(top = 16.dp))

                Box {
                    Column(
                        modifier = Modifier
                            .size(250.dp)
                            .clip(CircleShape)
                            .border(width = 3.dp, color = Color(0xFFE95D17), shape = CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${currentSeconds / 60}:${String.format("%02d", currentSeconds % 60)}",
                            style = typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        IconButton(
                            onClick = { isRunning = !isRunning },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Filled.Stop else Icons.Filled.PlayCircleFilled,
                                contentDescription = if (isRunning) "Stop" else "Play",
                                tint = Color(0xFF3355ff),
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 120.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                isRunning = false
                                currentSeconds = focusTime * 60
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(width = 3.dp, color = Color(0xFFE95D17), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop",
                                tint = Color(0xFF3355ff)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = {
                                isRunning = false
                                currentSeconds = focusTime * 60
                                sessionCount = 0
                                isBreak = false
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(width = 3.dp, color = Color(0xFFE95D17), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RestartAlt,
                                contentDescription = "Reset",
                                tint = Color(0xFF3355ff)
                            )
                        }
                    }
                }
            }

            // Botones flotantes y diálogos
            IconButton(
                onClick = {
                    showMusicDialog.value = true
                    showPomodoroDialog.value = false
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 3.dp, color = Color(0xFFE95D17), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = "Music",
                    tint = Color(0xFF3355ff)
                )
            }

            IconButton(
                onClick = {
                    showPomodoroDialog.value = true
                    showMusicDialog.value = false
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 16.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 3.dp, color = Color(0xFFE95D17), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ViewModule,
                    contentDescription = "Pomodoro Settings",
                    tint = Color(0xFF3355ff)
                )
            }
            var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
            var selectedFile by remember { mutableStateOf<String?>(null) }
            var isPlaying by remember { mutableStateOf(false) }
            MusicListDialog(
                context = LocalContext.current,
                show = showMusicDialog.value,
                mediaPlayer = mediaPlayer,
                onMediaPlayerChange = { mediaPlayer = it },
                isPlaying = isPlaying,
                onIsPlayingChange = { isPlaying = it },
                selectedFile = selectedFile,
                onSelectedFileChange = { selectedFile = it },
                onDismiss = { showMusicDialog.value = false },
                onConfirm = { showMusicDialog.value = false }
            )

            pomodoroSettingDialog(
                show = showPomodoroDialog.value,
                focusTime = focusTime,
                shortBreak = shortBreak,
                longBreak = longBreak,
                longBreakInterval = longBreakInterval,
                onDismiss = { showPomodoroDialog.value = false },
                onConfirm = { focus, short, long, interval ->
                    focusTime = focus
                    shortBreak = short
                    longBreak = long
                    longBreakInterval = interval
                    currentSeconds = focus * 60
                    showPomodoroDialog.value = false
                }
            )
        }
    }
}

