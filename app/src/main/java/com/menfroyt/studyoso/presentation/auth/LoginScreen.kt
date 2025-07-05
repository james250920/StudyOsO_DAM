package com.menfroyt.studyoso.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.menfroyt.studyoso.R
import com.menfroyt.studyoso.ViewModel.usuario.*
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.repositories.UsuarioRepository
import com.menfroyt.studyoso.presentation.auth.SessionManager
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
    var passwordVisible by remember { mutableStateOf(false) }
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

            // Contenido principal con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card principal de login
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Header con logo y título
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            LogoImage()
                            
                            Text(
                                text = "¡Bienvenido de nuevo!",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = "Inicia sesión para continuar con tus estudios",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Formulario de login
                        LoginForm(
                            correo = correo,
                            contrasena = contrasena,
                            passwordVisible = passwordVisible,
                            isLoading = isLoading,
                            onCorreoChange = { correo = it },
                            onContrasenaChange = { contrasena = it },
                            onPasswordVisibilityChange = { passwordVisible = it }
                        )

                        // Botón de login
                        LoginButton(
                            isLoading = isLoading,
                            enabled = correo.text.isNotBlank() && contrasena.text.isNotBlank(),
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

                        // Divider con texto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f))
                            Text(
                                text = "  o  ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f))
                        }

                        // Botón de registro
                        RegisterButton(
                            isLoading = isLoading,
                            navController = navController
                        )
                    }
                }
            }

            // Overlay de carga mejorado
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
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.study),
            contentDescription = "Study OSO Logo",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun LoginForm(
    correo: TextFieldValue,
    contrasena: TextFieldValue,
    passwordVisible: Boolean,
    isLoading: Boolean,
    onCorreoChange: (TextFieldValue) -> Unit,
    onContrasenaChange: (TextFieldValue) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo de correo mejorado
        OutlinedTextField(
            value = correo,
            onValueChange = onCorreoChange,
            label = { Text("Correo Electrónico") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Campo de contraseña mejorado
        OutlinedTextField(
            value = contrasena,
            onValueChange = onContrasenaChange,
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )


    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    enabled: Boolean,
    onLogin: () -> Unit
) {
    Button(
        onClick = onLogin,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Iniciando sesión...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun RegisterButton(
    isLoading: Boolean,
    navController: NavController
) {
    OutlinedButton(
        onClick = { navController.navigate("register") },
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.5.dp, 
            MaterialTheme.colorScheme.primary
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "¿No tienes cuenta? Crear una nueva",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Iniciando sesión...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Por favor espera",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
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
    // Validaciones mejoradas
    when {
        correo.isBlank() -> {
            onError("Por favor ingresa tu correo electrónico")
            return
        }
        contrasena.isBlank() -> {
            onError("Por favor ingresa tu contraseña")
            return
        }
        !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
            onError("Por favor ingresa un correo electrónico válido")
            return
        }
    }

    scope.launch {
        setLoading(true)
        try {
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
        } catch (e: Exception) {
            setLoading(false)
            onError("Error inesperado: ${e.message}")
        }
    }
}