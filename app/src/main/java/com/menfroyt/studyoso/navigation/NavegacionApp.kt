package com.menfroyt.studyoso.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.menfroyt.studyoso.presentation.config.ConfiguracionScreen
import com.menfroyt.studyoso.presentation.home.Home
import com.menfroyt.studyoso.presentation.auth.LoginScreen
import com.menfroyt.studyoso.presentation.usuario.PerfilScreen
import com.menfroyt.studyoso.presentation.components.PomodoroScreen
import com.menfroyt.studyoso.presentation.auth.RegisterScreen
import com.menfroyt.studyoso.presentation.pages.StudyOsoLandingScreen
import com.menfroyt.studyoso.presentation.tarea.AddTaskScreen

@Composable
fun NavegacionApp(
    isDarkTheme: Boolean = false
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") { StudyOsoLandingScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { Home(navController) }
        composable("login") { LoginScreen(navController) }
    }
}



