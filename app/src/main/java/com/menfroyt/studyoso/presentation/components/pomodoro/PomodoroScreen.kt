package com.menfroyt.studyoso.presentation.components.pomodoro

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.menfroyt.studyoso.utils.PomodoroNotificationWorker
import esan.mendoza.teststudyoso.presentation.components.pomodoro.music.MusicListDialog
import esan.mendoza.teststudyoso.presentation.components.pomodoro.music.pomodoroSettingDialog
import kotlinx.coroutines.delay


@Composable
fun PomodoroScreen(
    modifier: Modifier = Modifier
) {
    val showMusicDialog = rememberSaveable { mutableStateOf(false) }
    val showPomodoroDialog = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Estados del temporizador
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var currentSeconds by rememberSaveable { mutableStateOf(25 * 60) } // 25 minutos por defecto
    var focusTime by rememberSaveable { mutableStateOf(25) }
    var shortBreak by rememberSaveable { mutableStateOf(5) }
    var longBreak by rememberSaveable { mutableStateOf(15) }
    var longBreakInterval by rememberSaveable { mutableStateOf(4) }
    var sessionCount by rememberSaveable { mutableStateOf(0) }
    var isBreak by rememberSaveable { mutableStateOf(false) }

    // Colores para los diferentes estados
    val focusColor = Color(0xFF3355FF)
    val shortBreakColor = Color(0xFF4CAF50)
    val longBreakColor = Color(0xFFFF9800)
    
    // Determinar el color actual basado en el estado
    val currentColor = if (!isBreak) {
        focusColor
    } else if (sessionCount % longBreakInterval == 0) {
        longBreakColor
    } else {
        shortBreakColor
    }
    
    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = if (isRunning) 1f else 0.95f,
        targetValue = if (isRunning) 1.05f else 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), 
        label = "scale"
    )
    
    val progress = 1f - (currentSeconds.toFloat() / (if (!isBreak) focusTime * 60f else 
                         if (sessionCount % longBreakInterval == 0) longBreak * 60f else shortBreak * 60f))

    // Efecto para el temporizador
    LaunchedEffect(isRunning) {
        while (isRunning && currentSeconds > 0) {
            delay(1000L)
            currentSeconds--

            // Cuando el temporizador llega a 0
            if (currentSeconds == 0) {
                isRunning = false
                sessionCount++

                // Programar notificación
                val notificationData = workDataOf(
                    "isBreak" to isBreak,
                    "sessionCount" to sessionCount
                )

                val notificationWork = OneTimeWorkRequestBuilder<PomodoroNotificationWorker>()
                    .setInputData(notificationData)
                    .build()

                WorkManager.getInstance(context)
                    .enqueue(notificationWork)

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

    // Estado de la sesión actual
    val sessionStateText = if (!isBreak) {
        "Tiempo de Enfoque"
    } else if (sessionCount % longBreakInterval == 0) {
        "Descanso Largo"
    } else {
        "Descanso Corto"
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            // Fondo con gradiente sutil
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tarjeta de estado
                Card(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth(0.5f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = currentColor.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = sessionStateText,
                                style = typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = currentColor
                            )
                            Text(
                                text = "Sesión ${sessionCount + 1}",
                                style = typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = currentColor.copy(alpha = 0.15f),
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = if (!isBreak) "${focusTime}m"
                                       else if (sessionCount % longBreakInterval == 0) "${longBreak}m"
                                       else "${shortBreak}m",
                                style = typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = currentColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Temporizador principal
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(280.dp)
                        .graphicsLayer {
                            scaleX = if (isRunning) scale else 1f
                            scaleY = if (isRunning) scale else 1f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Círculo exterior (progreso)
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .clip(CircleShape)
                            .border(
                                width = 8.dp,
                                brush = Brush.sweepGradient(
                                    0f to currentColor.copy(alpha = 0.3f),
                                    progress to currentColor,
                                    progress + 0.1f to currentColor.copy(alpha = 0.3f),
                                    1f to currentColor.copy(alpha = 0.1f)
                                ),
                                shape = CircleShape
                            )
                    )

                    // Círculo interior (contenido)
                    Surface(
                        modifier = Modifier
                            .size(260.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape,
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${currentSeconds / 60}:${String.format("%02d", currentSeconds % 60)}",
                                    style = typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                                    color = currentColor
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Botón de Play/Pause
                                FilledIconButton(
                                    onClick = { isRunning = !isRunning },
                                    modifier = Modifier.size(70.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = currentColor.copy(alpha = 0.1f),
                                        contentColor = currentColor
                                    ),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = if (isRunning) "Pausar" else "Iniciar",
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Controles de temporizador
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ControlButton(
                        icon = Icons.Filled.Stop,
                        description = "Detener",
                        color = currentColor,
                        onClick = {
                            isRunning = false
                            currentSeconds = if (!isBreak) focusTime * 60
                                            else if (sessionCount % longBreakInterval == 0) longBreak * 60
                                            else shortBreak * 60
                        }
                    )

                    ControlButton(
                        icon = Icons.Filled.RestartAlt,
                        description = "Reiniciar sesiones",
                        color = currentColor,
                        onClick = {
                            isRunning = false
                            currentSeconds = focusTime * 60
                            sessionCount = 0
                            isBreak = false
                        }
                    )

                    ControlButton(
                        icon = Icons.Filled.SkipNext,
                        description = "Siguiente intervalo",
                        color = currentColor,
                        onClick = {
                            isRunning = false
                            if (!isBreak) {
                                isBreak = true
                                currentSeconds = if (sessionCount % longBreakInterval == 0) longBreak * 60 else shortBreak * 60
                            } else {
                                isBreak = false
                                currentSeconds = focusTime * 60
                                sessionCount++
                            }
                        }
                    )
                }
            }

            // Panel flotante de controles
            FloatingControlPanel(
                onMusicClick = {
                    showMusicDialog.value = true
                    showPomodoroDialog.value = false
                },
                onSettingsClick = {
                    showPomodoroDialog.value = true
                    showMusicDialog.value = false
                },
                currentColor = currentColor
            )

            var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
            var selectedFile by remember { mutableStateOf<String?>(null) }
            var isPlaying by remember { mutableStateOf(false) }
            // Diálogos
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

@Composable
private fun FloatingControlPanel(
    onMusicClick: () -> Unit,
    onSettingsClick: () -> Unit,
    currentColor: Color
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .offset(y = (-16).dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            onClick = onMusicClick,
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = currentColor,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Filled.LibraryMusic,
                contentDescription = "Música",
                modifier = Modifier.size(24.dp)
            )
        }
        
        Divider(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .width(32.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            thickness = 1.dp
        )
        
        FloatingActionButton(
            onClick = onSettingsClick,
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = currentColor,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Configuración",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = color.copy(alpha = 0.5f)
        ),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = color
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            modifier = Modifier.size(24.dp)
        )
    }
}

