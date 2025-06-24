package com.menfroyt.studyoso.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModel
import com.menfroyt.studyoso.ViewModel.usuario.UsuarioViewModelFactory
import com.menfroyt.studyoso.data.db.AppDatabase
import com.menfroyt.studyoso.data.entities.Usuario
import com.menfroyt.studyoso.data.repositories.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import com.menfroyt.studyoso.utils.hashPassword


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val usuarioRepository = remember { UsuarioRepository(db.UsuarioDao()) }
    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(usuarioRepository)
    )

    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var apellido by remember { mutableStateOf(TextFieldValue("")) }
    var correo by remember { mutableStateOf(TextFieldValue("")) }

    var contrasena by remember { mutableStateOf(TextFieldValue("")) }
    var confirmarContrasena by remember { mutableStateOf(TextFieldValue("")) }
    var passwordsMatch by remember { mutableStateOf(true) }
    var confirmarContrasenaError by remember { mutableStateOf(false) }

    var fechaNacimiento by remember { mutableStateOf(TextFieldValue("")) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    LaunchedEffect(showError) {
        if (showError) {
            snackbarHostState.showSnackbar(errorMessage)
            showError = false
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Éxito!") },
            text = { Text("Registro completado correctamente") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    // Iniciar el proceso de login
                    usuarioViewModel.login(
                        correo.text.trim(),
                        contrasena.text.trim(),
                        onSuccess = { usuarioAutenticado ->
                            navController.navigate("home/${usuarioAutenticado.idUsuario}") {
                                popUpTo("register") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            showError = true
                            errorMessage = error
                        }
                    )
                }) {
                    Text("Continuar")
                }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        fechaNacimiento = TextFieldValue(date.toString())
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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
                value = confirmarContrasena,
                onValueChange = {
                    confirmarContrasena = it
                    passwordsMatch = contrasena.text == it.text
                    confirmarContrasenaError = it.text.isNotEmpty() && !passwordsMatch
                },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = confirmarContrasenaError,
                supportingText = {
                    if (confirmarContrasenaError) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (confirmarContrasena.text.isNotEmpty() && passwordsMatch) {
                        Text(
                            text = "Las contraseñas coinciden",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (confirmarContrasena.text.isNotEmpty())
                        if (passwordsMatch) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = if (confirmarContrasena.text.isNotEmpty())
                        if (passwordsMatch) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.outline
                )
            )

            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it },
                label = { Text("Fecha de Nacimiento") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha"
                        )
                    }
                }
            )

            Button(
                onClick = {
                    if (nombre.text.isBlank() || apellido.text.isBlank() ||
                        correo.text.isBlank() || contrasena.text.isBlank() ||
                        fechaNacimiento.text.isBlank() || confirmarContrasena.text.isBlank()
                    ) {
                        errorMessage = "Complete todos los campos"
                        showError = true
                        return@Button
                    }

                    if (contrasena.text != confirmarContrasena.text) {
                        errorMessage = "Las contraseñas no coinciden"
                        showError = true
                        return@Button
                    }

                    isLoading = true
                    val hashedPassword = hashPassword(contrasena.text.trim())
                    val usuario = Usuario(
                        nombre = nombre.text.trim(),
                        apellido = apellido.text.trim(),
                        correo = correo.text.trim(),
                        contrasena = hashedPassword,
                        fechaNacimiento = fechaNacimiento.text.trim()
                    )

                    scope.launch {
                        try {
                            usuarioViewModel.agregarUsuario(usuario)
                            delay(1500) // Simular carga
                            isLoading = false
                            showSuccessDialog = true
                        } catch (e: Exception) {
                            isLoading = false
                            showError = true
                            errorMessage = "Error al registrar: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text("¿Tienes cuenta? Iniciar Sesión")
            }
        }
    }
}



