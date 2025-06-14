package esan.mendoza.teststudyoso.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import esan.mendoza.teststudyoso.R

@Composable
fun MatrizEisenhowerScreen(modifier: Modifier = Modifier, onScreenSelected: (String) -> Unit) {


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Matriz de Eisenhower",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                onScreenSelected(
                    "AddTaskScreen"
                )
            },
            modifier = Modifier
                .fillMaxWidth() // Para que el botón ocupe todo el ancho
                .padding(vertical = 8.dp) // Añadir un poco de espacio vertical
        ) {
            Text("Agregar tarea")
        }



        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically // Centra la imagen y el cuadrante verticalmente
        ) {
            Column(
                modifier = Modifier.weight(1f), // La columna ocupa la mitad del espacio de la Row
                horizontalAlignment = Alignment.CenterHorizontally // <--- AÑADE ESTO
            ) {
                // Cuadrante 1: Urgente e Importante
                CuadranteMatriz(
                    titulo = "Tienes que hacerlo",
                    descripcion = "Urgente e Importante",
                    color = Color(0xFFFF6B6B),
                    //icon = Icons.Filled.Warning,
                    modifier = Modifier
                        .weight(1f) // Para que este cuadrante tome espacio vertical dentro de la Column
                        .fillMaxWidth() // El cuadrante se expandirá al ancho de la Column
                        .padding(2.dp),
                    //icon = Icons.Filled.Warning,
                    imageVector = painterResource(id = R.drawable.c1)
                )
            }

            // Cuadrante 2: No Urgente e Importante
            CuadranteMatriz(
                titulo = "Puedes decidir",
                descripcion = "No Urgente e Importante",
                color = Color(0xFF4ECDC4),
                modifier = Modifier
                    .weight(1f) // Cada cuadrante ocupa la mitad de la columna
                    .fillMaxWidth()
                    .padding(2.dp),
                //icon = Icons.Filled.Dataset,
                imageVector =painterResource(id = R.drawable.c2)
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Cuadrante 3: Urgente y No Importante
            CuadranteMatriz(
                titulo = "Puedes delegar",
                descripcion = "Urgente y No Importante",
                color = Color(0xFFFFBE0B),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                //icon = Icons.Filled.Warning,
                imageVector = painterResource(id = R.drawable.c3)
            )

            // Cuadrante 4: No Urgente y No Importante
            CuadranteMatriz(
                titulo = "Puedes postergarlo o eliminarlo",
                descripcion = "No Urgente y No Importante",
                color = Color(0xFF95A5A6),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                //icon = Icons.Filled.Warning,
                imageVector = painterResource(id = R.drawable.c4)
            )
        }
    }
}

@Composable
private fun CuadranteMatriz(
    titulo: String,
    descripcion: String,
    color: Color,
    //icon: ImageVector? = null, // Parámetro opcional para el icono
    modifier: Modifier = Modifier,
    imageVector: Painter
) {
    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp)) // Aumentar el espacio

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp)) // Aumentar el espacio
            Image(
                painter = imageVector,
                contentDescription = "Logo Study Oso",
                modifier = Modifier
                    .size(130.dp)
            )
        }
    }
}