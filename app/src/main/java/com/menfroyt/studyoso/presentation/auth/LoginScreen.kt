package com.menfroyt.studyoso.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModel
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.repositories.UsuarioRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val usuarioRepository = remember { UsuarioRepository(db.UsuarioDao()) }
    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(usuarioRepository)
    )
    val sessionManager = remember { SessionManager(context) }

    // Verificar si ya existe una sesión activa
    LaunchedEffect(Unit) {
        if (sessionManager.isLoggedIn()) {
            navController.navigate("home/${sessionManager.getUserId()}") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    var correo by remember { mutableStateOf(TextFieldValue("")) }
    var contrasena by remember { mutableStateOf(TextFieldValue("")) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(showError) {
        if (showError) {
            snackbarHostState.showSnackbar(errorMessage)
            showError = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Button(
                onClick = {
                    if (correo.text.isBlank() || contrasena.text.isBlank()) {
                        errorMessage = "Ingrese correo y contraseña"
                        showError = true
                        return@Button
                    }

                    usuarioViewModel.login(
                        correo.text.trim(),
                        contrasena.text.trim(),
                        onSuccess = { usuario ->
                            // Guardar la sesión
                            sessionManager.saveSession(usuario.idUsuario, correo.text.trim())
                            navController.navigate("home/${usuario.idUsuario}") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = { mensaje ->
                            errorMessage = mensaje
                            showError = true
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Iniciar Sesión")
            }

            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}