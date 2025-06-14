package com.menfroyt.studyoso.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.menfroyt.studyoso.presentation.auth.LoginScreen
import com.menfroyt.studyoso.presentation.auth.RegisterScreen
import com.menfroyt.studyoso.presentation.home.Home
import com.menfroyt.studyoso.presentation.pages.StudyOsoLandingScreen


@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") { StudyOsoLandingScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable(
            route = "home/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
            Home(
                navController = navController,
                usuarioId = usuarioId
            )
        }
        composable("login") { LoginScreen(navController) }

    }
}



