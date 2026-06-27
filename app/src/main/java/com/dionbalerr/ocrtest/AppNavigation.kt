package com.dionbalerr.ocrtest

import android.R.attr.name
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    LaunchedEffect(navController) {
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
            StartOCR(navController)
        }
    }
}