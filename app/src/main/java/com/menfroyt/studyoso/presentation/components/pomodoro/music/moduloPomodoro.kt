package esan.mendoza.teststudyoso.presentation.components.pomodoro.music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

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
if(show) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(380.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)

            ) {
                Text("Tiempo: ${focus.toInt()} min", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = focus,
                    onValueChange = { focus = it },
                    valueRange = 15f..60f,
                    steps = 45,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF3355ff),
                        activeTrackColor = Color(0xFF3355ff)
                    )
                )

                Text("Descanso: ${short.toInt()} min", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = short,
                    onValueChange = { short = it },
                    valueRange = 5f..30f,
                    steps = 25,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF3355ff),
                        activeTrackColor = Color(0xFF3355ff)
                    )
                )

                Text(
                    "Descanso largo: ${long.toInt()} min",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = long,
                    onValueChange = { long = it },
                    valueRange = 5f..60f,
                    steps = 55,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF3355ff),
                        activeTrackColor = Color(0xFF3355ff)
                    )
                )

                Text(
                    "Intervalo: ${interval.toInt()} intervalos",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = interval,
                    onValueChange = { interval = it },
                    valueRange = 1f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF3355ff),
                        activeTrackColor = Color(0xFF3355ff)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss,) {
                        Text(
                            "Cancel",
                            color = Color(0xFF3355ff)
                        )

                    }
                    TextButton(onClick = {
                        onConfirm(focus.toInt(), short.toInt(), long.toInt(), interval.toInt())
                    }) {
                        Text("OK",
                            color = Color(0xFF3355ff))
                    }
                }
            }
        }
    }
}
}



