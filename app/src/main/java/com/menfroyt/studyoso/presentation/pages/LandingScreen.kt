package com.menfroyt.studyoso.presentation.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import esan.mendoza.teststudyoso.R
@Composable
fun StudyOsoLandingScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffffff)) // Azul claro
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Transforma tu Vida Académica",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )

            Image(
                painter = painterResource(id = R.drawable.study),
                contentDescription = "Logo StudyOso",
                modifier = Modifier
                    .height(180.dp)
                    .padding(vertical = 8.dp)
            )

            Text(
                text = "StudyOso aplicación que revolucionará tu organización " +
                        "planificando las horas, asegurando que no te pierdas ninguna tarea " +
                        "y ayudando a gestionar tu vida académica " +
                        "de forma eficiente.",
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Button(
                    onClick = { navController.navigate("register") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Registrarse", fontWeight = FontWeight.Bold,color = Color.White)
                }

                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ingresar", fontWeight = FontWeight.Bold,color = Color.White)
                }
            }
        }
    }
}


