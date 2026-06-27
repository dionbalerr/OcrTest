package com.dionbalerr.ocrtest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dionbalerr.ocrtest.ui.screen.PermissionScreen
import com.dionbalerr.ocrtest.ui.screen.StartOCRScreen

@Composable
fun AppNavigation()
{
    val navController = rememberNavController()

    LaunchedEffect(navController)
    {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            println("Current route: ${backStackEntry.destination.route}")
        }
    }

    NavHost(
        navController = navController,
        startDestination = "greeting"
    )
    {
        composable("greeting")
        {
            Greeting("Android", navController)
        }

        composable("permission")
        {
            PermissionScreen(navController)
        }

        composable("start_ocr")
        {
            StartOCRScreen(navController)
        }
    }
}