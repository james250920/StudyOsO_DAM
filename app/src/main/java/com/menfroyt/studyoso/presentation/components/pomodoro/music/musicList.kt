package esan.mendoza.teststudyoso.presentation.components.pomodoro.music

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MusicListDialog(
    context: Context,
    show: Boolean,
    mediaPlayer: MediaPlayer?,
    onMediaPlayerChange: (MediaPlayer?) -> Unit,
    isPlaying: Boolean,
    onIsPlayingChange: (Boolean) -> Unit,
    selectedFile: String?,
    onSelectedFileChange: (String?) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show) {
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var currentIndex by remember { mutableStateOf(0) }

        val audioFiles = remember {
            try {
                val files = context.assets.list("")
                files?.filter { it.endsWith(".mp3") } ?: emptyList()
            } catch (e: Exception) {
                errorMessage = "Error al cargar archivos: ${e.message}"
                emptyList()
            }
        }

        // Función para reproducir el siguiente archivo
        fun playNextFile() {
            if (audioFiles.isEmpty()) return

            try {
                mediaPlayer?.apply {
                    stop()
                    release()
                }

                currentIndex = (currentIndex + 1) % audioFiles.size
                val fileName = audioFiles[currentIndex]

                val newMediaPlayer = MediaPlayer().apply {
                    context.assets.openFd(fileName).use { descriptor ->
                        setDataSource(
                            descriptor.fileDescriptor,
                            descriptor.startOffset,
                            descriptor.length
                        )
                        prepare()
                        start()
                    }
                    // Configurar el listener para cuando termine la canción
                    setOnCompletionListener {
                        playNextFile()
                    }
                }
                onMediaPlayerChange(newMediaPlayer)
                onSelectedFileChange(fileName)
                onIsPlayingChange(true)
            } catch (e: Exception) {
                errorMessage = "Error al reproducir: ${e.message}"
            }
        }

        // Función para reproducir un archivo específico
        fun playFile(fileName: String, index: Int) {
            try {
                mediaPlayer?.apply {
                    stop()
                    release()
                }

                currentIndex = index
                val newMediaPlayer = MediaPlayer().apply {
                    context.assets.openFd(fileName).use { descriptor ->
                        setDataSource(
                            descriptor.fileDescriptor,
                            descriptor.startOffset,
                            descriptor.length
                        )
                        prepare()
                        start()
                    }
                    // Configurar el listener para cuando termine la canción
                    setOnCompletionListener {
                        playNextFile()
                    }
                }
                onMediaPlayerChange(newMediaPlayer)
                onSelectedFileChange(fileName)
                onIsPlayingChange(true)
            } catch (e: Exception) {
                errorMessage = "Error al reproducir: ${e.message}"
            }
        }

        // Resto del código del AlertDialog...
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Lista de Música") },
            text = {
                Column {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    } else if (audioFiles.isEmpty()) {
                        Text(text = "No se encontraron archivos MP3")
                    } else {
                        audioFiles.forEachIndexed { index, fileName ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { playFile(fileName, index) }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = fileName,
                                    color = if (selectedFile == fileName)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                if (selectedFile == fileName) {
                                    IconButton(
                                        onClick = {
                                            if (isPlaying) {
                                                mediaPlayer?.pause()
                                            } else {
                                                mediaPlayer?.start()
                                            }
                                            onIsPlayingChange(!isPlaying)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying)
                                                Icons.Default.Pause
                                            else
                                                Icons.Default.PlayArrow,
                                            contentDescription = if (isPlaying) "Pausar" else "Reproducir"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    mediaPlayer?.apply {
                        stop()
                        release()
                    }
                    onMediaPlayerChange(null)
                    onDismiss()
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}