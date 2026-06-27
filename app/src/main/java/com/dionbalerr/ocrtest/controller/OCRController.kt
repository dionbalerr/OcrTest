package com.dionbalerr.ocrtest.controller

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.dionbalerr.ocrtest.ScreenCaptureService

class OCRController(private val context: Context)
{
    fun startOCR()
    {
        Toast.makeText(context, "StartOcr() called", Toast.LENGTH_SHORT).show()

        val intent = Intent(context, ScreenCaptureService::class.java)
            .setAction(ScreenCaptureService.Companion.ACTION_RUN_OCR)

        context.startService(intent)
    }
}