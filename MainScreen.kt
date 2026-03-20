package com.dionbalerr.ocrtest

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun MainScreen()
{
    val context = LocalContext.current
    val projectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

    var shouldRequest by remember { mutableStateOf(true) }

    val screenCaptureServiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            result ->
            if (result.resultCode == Activity.RESULT_OK)
            {
                shouldRequest = false
                val data = result.data

                val intent = Intent(context, ScreenCaptureService::class.java).apply {
                    putExtra("resultCode", result.resultCode)
                    putExtra("data", result.data)
                }
                Toast.makeText(context, "OCR is starting", Toast.LENGTH_SHORT).show()
                ContextCompat.startForegroundService(context, intent)
            }
            else
            {
                shouldRequest = false
                Toast.makeText(context, "Screen Capture permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    val permissionActivityLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted ->
        if (granted)
            screenCaptureServiceLauncher.launch(projectionManager.createScreenCaptureIntent())
        else
        {
            Toast.makeText(context, "Enable notifications pls", Toast.LENGTH_SHORT).show()
            shouldRequest = false
        }
    }

    LaunchedEffect(shouldRequest)
    {
        if (shouldRequest)
        {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            { Toast.makeText(context, "For OCR to work everywhere, please choose 'Entire Screen'.", Toast.LENGTH_SHORT).show() }
            permissionActivityLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (!shouldRequest)
    {
//        Toast.makeText(context, "Please enable permission 🥺", Toast.LENGTH_SHORT).show()
        Button(onClick = { shouldRequest = true })
        { Text("Gib permissionnnns \uD83E\uDD7A") }
    }

}
