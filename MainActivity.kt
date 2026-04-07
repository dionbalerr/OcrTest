package com.dionbalerr.ocrtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.dionbalerr.ocrtest.ui.theme.OCRTestTheme

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent()
        {
            OCRTestTheme()
            {
                Scaffold(modifier = Modifier.fillMaxSize())
                { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding))
                    {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, navController: NavController)
{
    Text(
        text = "Hello $name!"
    )
    navController.navigate("permission")
    { popUpTo("greeting") {inclusive=true} }
}