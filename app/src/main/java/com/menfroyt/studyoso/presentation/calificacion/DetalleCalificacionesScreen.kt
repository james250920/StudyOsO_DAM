package com.menfroyt.studyoso.presentation.calificacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModel
import com.menfroyt.studyoso.ViewModel.calificacion.CalificacionViewModelFactory
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModel
import com.menfroyt.studyoso.ViewModel.curso.CursoViewModelFactory
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModel
import com.menfroyt.studyoso.ViewModel.tipoPrueba.TipoPruebaViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.repositories.CalificacionRepository
import com.menfroyt.studyoso.data.repositories.CursoRepository
import com.menfroyt.studyoso.data.repositories.TipoPruebaRepository
import kotlin.text.get

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetalleCalificacionesScreen(
    modifier: Modifier = Modifier,
    cursoId: Int,
    onScreenSelected: (String) -> Unit
) {
    // Inicialización de dependencias
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    // ViewModels y estados
    val tipoPruebaRepo = remember { TipoPruebaRepository(db.TipoPruebaDao()) }
    val tipoPruebaViewModel: TipoPruebaViewModel = viewModel(factory = TipoPruebaViewModelFactory(tipoPruebaRepo))
    val tiposPrueba by tipoPruebaViewModel.tiposPrueba.collectAsState()

    val calificacionRepo = remember { CalificacionRepository(db.CalificacionDao()) }
    val calificacionViewModel: CalificacionViewModel = viewModel(factory = CalificacionViewModelFactory(calificacionRepo))
    val calificaciones by calificacionViewModel.calificaciones.collectAsState()

    val cursoRepository = remember { CursoRepository(db.CursoDao()) }
    val cursoViewModel: CursoViewModel = viewModel(factory = CursoViewModelFactory(cursoRepository))
    var nombreCurso by remember { mutableStateOf<String>("") }

    // Efectos
    LaunchedEffect(cursoId) {
        tipoPruebaViewModel.cargarTiposPorCurso(cursoId)
        calificacionViewModel.cargarCalificacionesPorCurso(cursoId)
        val curso = cursoViewModel.getCursoById(cursoId)
        nombreCurso = curso?.nombreCurso ?: ""
    }

    // Cálculos
    val calPorTipo = tiposPrueba.associateWith { tipo ->
        calificaciones.filter { it.idTipoPrueba == tipo.idTipoPrueba }
    }

    val promediosPorTipo = tiposPrueba.associate { tipo ->
        tipo.nombreTipo to (calPorTipo[tipo]?.mapNotNull { it.calificacionObtenida }?.average()?.takeIf { !it.isNaN() } ?: 0.0)
    }

    val promedioFinal = tiposPrueba.sumOf { tipo ->
        val promedioTipo = promediosPorTipo[tipo.nombreTipo] ?: 0.0
        promedioTipo * (tipo.pesoTotal / 100.0)
    }

    // UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Barra superior
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onScreenSelected("ListCalificaciones") },
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Regresar a lista",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = nombreCurso,
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(
                    onClick = { onScreenSelected("AgregarCalificacion") },
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "añadir",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Contenido con padding
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de distribución de notas
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Distribución de Notas", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        tiposPrueba.forEach { tipo ->
                            PesoItem(tipo.nombreTipo, "${tipo.pesoTotal.toInt()}%")
                        }
                    }
                }
            }

            // Card de calificaciones actuales
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Calificaciones Actuales", style = MaterialTheme.typography.titleMedium)
                    tiposPrueba.forEach { tipo ->
                        NotaItem(
                            tipo.nombreTipo,
                            *(calPorTipo[tipo]?.map { (it.calificacionObtenida?.toString() ?: "-") }?.toTypedArray() ?: arrayOf()),
                            emoji = "⭐"
                        )
                    }
                    Button(
                        onClick = { onScreenSelected("SimuladoCalificacion/$cursoId") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Simulador de notas")
                    }
                }
            }

            // Card de promedios por tipo
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Promedios por Tipo", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        promediosPorTipo.forEach { (tipo, promedio) ->
                            PromedioItem(tipo, String.format("%.2f", promedio))
                        }
                    }
                }
            }

            // Card de promedio final
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Promedio Final",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        String.format("%.2f", promedioFinal),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}


@Composable
private fun PesoItem(titulo: String, peso: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.bodyMedium
        )
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                peso,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun NotaItem(titulo: String, vararg notas: String, emoji: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                emoji,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            notas.forEachIndexed { index, nota ->
                ElevatedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Nota ${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            nota,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromedioItem(titulo: String, promedio: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                promedio,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}