package com.menfroyt.studyoso.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.menfroyt.studyoso.presentation.Home

@Composable
fun NavegacionApp(

) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            Home( )
        }

    }
}



