package esan.mendoza.teststudyoso.presentation.components.pomodoro.music

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties


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
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.95f),
            properties = DialogProperties(
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color(0xFF3355ff),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Lista de Música",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (errorMessage != null) {
                        ErrorMessage(errorMessage!!)
                    } else if (audioFiles.isEmpty()) {
                        EmptyState()
                    } else {
                        audioFiles.forEachIndexed { index, fileName ->
                            MusicItem(
                                fileName = fileName,
                                isSelected = selectedFile == fileName,
                                isPlaying = isPlaying && selectedFile == fileName,
                                onItemClick = { playFile(fileName, index) },
                                onPlayPauseClick = {
                                    if (isPlaying) mediaPlayer?.pause() else mediaPlayer?.start()
                                    onIsPlayingChange(!isPlaying)
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            mediaPlayer?.apply {
                                stop()
                                release()
                            }
                            onMediaPlayerChange(null)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3355ff).copy(alpha = 0.8f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar", style = MaterialTheme.typography.labelLarge)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3355ff),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Aceptar", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        )
    }
}

@Composable
private fun MusicItem(
    fileName: String,
    isSelected: Boolean,
    isPlaying: Boolean,
    onItemClick: () -> Unit,
    onPlayPauseClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF3355ff).copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fileName.removeSuffix(".mp3"),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected)
                    Color(0xFF3355ff)
                else
                    MaterialTheme.colorScheme.onSurface
            )
            if (isSelected) {
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFF3355ff).copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isPlaying)
                            Icons.Default.Pause
                        else
                            Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        tint = Color(0xFF3355ff)
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MusicOff,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No se encontraron archivos MP3",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}