package com.menfroyt.studyoso.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var apellido by remember { mutableStateOf(TextFieldValue("")) }
    var correo by remember { mutableStateOf(TextFieldValue("")) }
    var contrasena by remember { mutableStateOf(TextFieldValue("")) }
    var fechaNacimiento by remember { mutableStateOf(TextFieldValue("")) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it },
                label = { Text("fechaNacimiento(yyyy-mm-dd)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text("¿tienes cuenta? Iniciar Sesión")
            }

        }
    }
}

