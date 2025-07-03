package esan.mendoza.teststudyoso.presentation.components.pomodoro.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun pomodoroSettingDialog(
    show: Boolean,
    focusTime: Int,
    shortBreak: Int,
    longBreak: Int,
    longBreakInterval: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Int, Int) -> Unit
) {
    var focus by remember { mutableStateOf(focusTime.toFloat()) }
    var short by remember { mutableStateOf(shortBreak.toFloat()) }
    var long by remember { mutableStateOf(longBreak.toFloat()) }
    var interval by remember { mutableStateOf(longBreakInterval.toFloat()) }

    if (show) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    // Header con icono y título
                    HeaderSection()
                    
                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )
                    
                    // Secciones de configuración
                    SettingSection(
                        icon = Icons.Default.Timer,
                        title = "Tiempo de Enfoque",
                        value = focus.toInt(),
                        unit = "minutos",
                        color = Color(0xFF3355ff),
                        description = "Duración de las sesiones de trabajo"
                    ) {
                        PomodoroSlider(
                            value = focus,
                            onValueChange = { focus = it },
                            valueRange = 15f..60f,
                            steps = 45,
                            color = Color(0xFF3355ff)
                        )
                    }

                    SettingSection(
                        icon = Icons.Default.Coffee,
                        title = "Descanso Corto",
                        value = short.toInt(),
                        unit = "minutos",
                        color = Color(0xFF4CAF50),
                        description = "Pausa entre sesiones de trabajo"
                    ) {
                        PomodoroSlider(
                            value = short,
                            onValueChange = { short = it },
                            valueRange = 5f..30f,
                            steps = 25,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    SettingSection(
                        icon = Icons.Default.RestaurantMenu,
                        title = "Descanso Largo",
                        value = long.toInt(),
                        unit = "minutos",
                        color = Color(0xFFFF9800),
                        description = "Pausa extendida cada cierto número de sesiones"
                    ) {
                        PomodoroSlider(
                            value = long,
                            onValueChange = { long = it },
                            valueRange = 15f..60f,
                            steps = 45,
                            color = Color(0xFFFF9800)
                        )
                    }

                    SettingSection(
                        icon = Icons.Default.Repeat,
                        title = "Intervalo de Descanso",
                        value = interval.toInt(),
                        unit = "sesiones",
                        color = Color(0xFF9C27B0),
                        description = "Número de sesiones antes del descanso largo"
                    ) {
                        PomodoroSlider(
                            value = interval,
                            onValueChange = { interval = it },
                            valueRange = 2f..8f,
                            steps = 6,
                            color = Color(0xFF9C27B0)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botones de acción
                    ActionButtons(
                        onDismiss = onDismiss,
                        onConfirm = {
                            onConfirm(focus.toInt(), short.toInt(), long.toInt(), interval.toInt())
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3355ff).copy(alpha = 0.2f),
                            Color(0xFF3355ff).copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = Color(0xFF3355ff),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Personaliza tu técnica de productividad",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingSection(
    icon: ImageVector,
    title: String,
    value: Int,
    unit: String,
    color: Color,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),

    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = color.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = color.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "$value $unit",
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = color
                    )
                }
            }
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            content()
        }
    }
}

@Composable
private fun PomodoroSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    color: Color
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        colors = SliderDefaults.colors(
            thumbColor = color,
            activeTrackColor = color,
            inactiveTrackColor = color.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    )
}

@Composable
private fun ActionButtons(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                2.dp, 
                Color(0xFF3355ff).copy(alpha = 0.3f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF3355ff)
            )
        ) {
            Text(
                text = "Cancelar",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        
        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3355ff),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "Guardar",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}



