package com.menfroyt.studyoso.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.menfroyt.studyoso.R
import com.menfroyt.studyoso.ViewModel.usuario.*
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.repositories.UsuarioRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    // Estados y dependencias
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val usuarioRepository = remember { UsuarioRepository(db.UsuarioDao()) }
    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(usuarioRepository)
    )
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // Estados de UI
    var isLoading by remember { mutableStateOf(false) }
    var correo by remember { mutableStateOf(TextFieldValue("")) }
    var contrasena by remember { mutableStateOf(TextFieldValue("")) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Efectos
    LaunchedEffect(Unit) {
        if (sessionManager.isLoggedIn()) {
            navController.navigate("home/${sessionManager.getUserId()}") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(showError) {
        if (showError) {
            snackbarHostState.showSnackbar(errorMessage)
            showError = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo
            BackgroundImage()

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoImage()
                LoginForm(
                    correo = correo,
                    contrasena = contrasena,
                    isLoading = isLoading,
                    onCorreoChange = { correo = it },
                    onContrasenaChange = { contrasena = it }
                )
                LoginButton(
                    isLoading = isLoading,
                    onLogin = {
                        handleLogin(
                            correo = correo.text,
                            contrasena = contrasena.text,
                            scope = scope,
                            usuarioViewModel = usuarioViewModel,
                            sessionManager = sessionManager,
                            navController = navController,
                            onError = { mensaje ->
                                errorMessage = mensaje
                                showError = true
                            },
                            setLoading = { isLoading = it }
                        )
                    }
                )
                RegisterButton(
                    isLoading = isLoading,
                    navController = navController
                )
            }

            // Indicador de carga
            if (isLoading) {
                LoadingIndicator()
            }
        }
    }
}

@Composable
private fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.backgroundlogin),
        contentDescription = "Background Image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun LogoImage() {
    Image(
        painter = painterResource(id = R.drawable.study),
        contentDescription = "Study OSO Logo",
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun LoginForm(
    correo: TextFieldValue,
    contrasena: TextFieldValue,
    isLoading: Boolean,
    onCorreoChange: (TextFieldValue) -> Unit,
    onContrasenaChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = correo,
        onValueChange = onCorreoChange,
        label = { Text("Correo Electrónico") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        enabled = !isLoading
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = contrasena,
        onValueChange = onContrasenaChange,
        label = { Text("Contraseña") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        enabled = !isLoading
    )
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onLogin: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = onLogin,
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(50.dp),
        enabled = !isLoading
    ) {
        Text("Iniciar Sesión")
    }
}

@Composable
private fun RegisterButton(
    isLoading: Boolean,
    navController: NavController
) {
    Spacer(modifier = Modifier.height(8.dp))
    TextButton(
        onClick = { navController.navigate("register") },
        enabled = !isLoading
    ) {
        Text("¿No tienes cuenta? Regístrate")
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun handleLogin(
    correo: String,
    contrasena: String,
    scope: kotlinx.coroutines.CoroutineScope,
    usuarioViewModel: UsuarioViewModel,
    sessionManager: SessionManager,
    navController: NavController,
    onError: (String) -> Unit,
    setLoading: (Boolean) -> Unit
) {
    if (correo.isBlank() || contrasena.isBlank()) {
        onError("Ingrese correo y contraseña")
        return
    }

    scope.launch {
        setLoading(true)
        usuarioViewModel.login(
            correo.trim(),
            contrasena.trim(),
            onSuccess = { usuario ->
                sessionManager.saveSession(usuario.idUsuario, correo.trim())
                setLoading(false)
                navController.navigate("home/${usuario.idUsuario}") {
                    popUpTo("login") { inclusive = true }
                }
            },
            onError = { mensaje ->
                setLoading(false)
                onError(mensaje)
            }
        )
    }
}