package com.dionbalerr.ocrtest.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dionbalerr.ocrtest.controller.OCRController

@Composable
fun StartOCRScreen(navController: NavController)
{
    val context = LocalContext.current
    val ocrController = remember { OCRController(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Button(onClick =
            {
                Toast.makeText(context, "Start OCR button clicked!", Toast.LENGTH_SHORT).show()
                ocrController.startOCR()
            })
        {
            Text("Start OCR-ing")
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        Text("Test text, 彼はその討論で反対派に付いた。")
    }
}