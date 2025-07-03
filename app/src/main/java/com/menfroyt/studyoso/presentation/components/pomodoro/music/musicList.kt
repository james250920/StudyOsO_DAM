package esan.mendoza.teststudyoso.presentation.components.pomodoro.music

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import esan.mendoza.teststudyoso.presentation.components.pomodoro.music.MusicItem
import esan.mendoza.teststudyoso.presentation.components.pomodoro.music.NowPlayingCard

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
        var searchQuery by remember { mutableStateOf("") }
        val musicColor = Color(0xFF3355ff)
        
        val audioFiles = remember {
            try {
                val files = context.assets.list("")
                files?.filter { it.endsWith(".mp3") } ?: emptyList()
            } catch (e: Exception) {
                errorMessage = "Error al cargar archivos: ${e.message}"
                emptyList()
            }
        }
        
        val filteredAudioFiles = remember(searchQuery, audioFiles) {
            if (searchQuery.isEmpty()) audioFiles
            else audioFiles.filter { 
                it.removeSuffix(".mp3").contains(searchQuery, ignoreCase = true)
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

        // Diálogo mejorado
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Encabezado
                    MusicDialogHeader(musicColor)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Barra de búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = musicColor,
                            cursorColor = musicColor,
                            focusedLeadingIconColor = musicColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        placeholder = { Text("Buscar música") },
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.Search, 
                                contentDescription = "Buscar"
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Limpiar búsqueda"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Información de reproducción actual
                    if (selectedFile != null && isPlaying) {
                        NowPlayingCard(
                            fileName = selectedFile,
                            isPlaying = isPlaying,
                            onPlayPauseClick = {
                                if (isPlaying) mediaPlayer?.pause() else mediaPlayer?.start()
                                onIsPlayingChange(!isPlaying)
                            },
                            musicColor = musicColor
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Lista de archivos de música
                    if (errorMessage != null) {
                        ErrorMessage(errorMessage!!)
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            if (filteredAudioFiles.isEmpty()) {
                                if (audioFiles.isEmpty()) {
                                    EmptyState()
                                } else {
                                    NoSearchResultsState(searchQuery)
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    filteredAudioFiles.forEachIndexed { index, fileName ->
                                        MusicItem(
                                            fileName = fileName,
                                            isSelected = selectedFile == fileName,
                                            isPlaying = isPlaying && selectedFile == fileName,
                                            onItemClick = { playFile(fileName, index) },
                                            onPlayPauseClick = {
                                                if (isPlaying) mediaPlayer?.pause() else mediaPlayer?.start()
                                                onIsPlayingChange(!isPlaying)
                                            },
                                            musicColor = musicColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Botones de acción
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                mediaPlayer?.apply {
                                    stop()
                                    release()
                                }
                                onMediaPlayerChange(null)
                                onSelectedFileChange(null)
                                onIsPlayingChange(false)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.5.dp, 
                                color = musicColor.copy(alpha = 0.5f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = musicColor
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicOff,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Detener Música",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                        
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = musicColor,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aceptar",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MusicDialogHeader(musicColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            musicColor.copy(alpha = 0.2f),
                            musicColor.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                contentDescription = null,
                tint = musicColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = "Biblioteca de Música",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Selecciona el sonido que deseas escuchar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NowPlayingCard(
    fileName: String,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    musicColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveSize by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave animation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = musicColor.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de música que pulsa
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = musicColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.width(24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Barras de ecualizador animadas
                        if (isPlaying) {
                            for (i in 1..3) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height((20 * (0.5 + (waveSize * 0.5 * (i % 3)))).dp)
                                        .background(
                                            musicColor, 
                                            RoundedCornerShape(4.dp)
                                        )
                                )
                                if (i < 3) Spacer(modifier = Modifier.width(2.dp))
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = musicColor
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Reproduciendo ahora",
                        style = MaterialTheme.typography.labelSmall,
                        color = musicColor
                    )
                    Text(
                        text = fileName.removeSuffix(".mp3"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = musicColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = musicColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun MusicItem(
    fileName: String,
    isSelected: Boolean,
    isPlaying: Boolean,
    onItemClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    musicColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                musicColor.copy(alpha = 0.08f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isSelected) 
                                musicColor.copy(alpha = 0.15f) 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isSelected) musicColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = fileName.removeSuffix(".mp3"),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isSelected)
                        musicColor
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (isSelected) {
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = musicColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isPlaying)
                            Icons.Default.Pause
                        else
                            Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        tint = musicColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NoSearchResultsState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No se encontraron resultados para \"$query\"",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Intenta con otra palabra o frase",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
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
        shape = RoundedCornerShape(16.dp)
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
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
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
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No se encontraron archivos MP3",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Agrega archivos MP3 a la carpeta de assets de la aplicación",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}