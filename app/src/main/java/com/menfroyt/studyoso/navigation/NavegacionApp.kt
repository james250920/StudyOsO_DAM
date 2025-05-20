package com.menfroyt.studyoso.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.menfroyt.studyoso.presentation.ConfiguracionScreen
import com.menfroyt.studyoso.presentation.Home
import com.menfroyt.studyoso.presentation.LoginScreen
import com.menfroyt.studyoso.presentation.PerfilScreen
import com.menfroyt.studyoso.presentation.PomodoroScreen
import com.menfroyt.studyoso.presentation.RegisterScreen
import com.menfroyt.studyoso.presentation.StudyOsoLandingScreen

@Composable
fun NavegacionApp(

) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") { StudyOsoLandingScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { Home(navController) }
        composable("login") { LoginScreen(navController) }
        composable("perfil") { PerfilScreen() }
        composable("configuracion") { ConfiguracionScreen() }
        composable("pomodoro") { PomodoroScreen() }
        composable("AgregarCursos"){}
        composable("Calificaciones") {}
        composable("ListaTareas") {}
        composable("MatrizEisenhower"){}
        composable("Calendario") {}
    }
}



