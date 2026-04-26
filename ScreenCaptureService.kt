package com.dionbalerr.ocrtest

import android.media.ImageReader
import android.media.projection.MediaProjection
import android.hardware.display.VirtualDisplay
import android.hardware.display.DisplayManager
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import androidx.core.graphics.createBitmap

class ScreenCaptureService : Service()
{
    companion object
    {
        const val ACTION_START = "start_service"
        const val ACTION_RUN_OCR = "run_ocr"
    }

    private lateinit var imageReader: ImageReader
    private lateinit var mediaProjection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay
    @Volatile private var hasCapturedFrame = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        Log.i("DEBUGGING TOOL", "ScreenCaptureService onStartCommand")
        val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED) ?: 0
        val data = intent?.getParcelableExtra<Intent>("data")

        when(intent?.action)
        {
            ACTION_START ->
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    val notification = createNotification()
                    startForeground(1, notification)

                    val projectionManager =
                        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

                    mediaProjection = projectionManager.getMediaProjection(resultCode, data)

                    mediaProjection.registerCallback(object : MediaProjection.Callback() {
                        override fun onStop() {
                            super.onStop()
                            Log.d("ScreenCaptureService", "MediaProjection stopped")
                            virtualDisplay.release()
                            imageReader.close()
                        }
                    }, Handler(Looper.getMainLooper()))

                    createImageReader()
                    captureFrame()

                    return START_NOT_STICKY
                }
            }
            ACTION_RUN_OCR ->
            {
                if (!isCaptureSessionReady()) {
                    Log.w("ScreenCaptureService", "ACTION_RUN_OCR ignored: capture session is not ready")
                    Toast.makeText(
                        this,
                        "Screen capture is not ready. Grant permission again first.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return START_NOT_STICKY
                }

                captureFrame()

                return START_NOT_STICKY
            }

            else -> return START_NOT_STICKY
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification
    {
        val channelId = "screen_capture_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val chan = NotificationChannel(
                channelId,
                "Screen Capture",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(chan)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Screen Capture Running")
            .setContentText("Your screen is being captured")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    private fun createImageReader()
    {
        val metrics = Resources.getSystem().displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(
            width,
            height,
            PixelFormat.RGBA_8888,
            2
        )

        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            null
        )
    }

    private fun captureFrame()
    {
        hasCapturedFrame = false
        imageReader.setOnImageAvailableListener({ reader ->
            if (hasCapturedFrame) return@setOnImageAvailableListener

            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            hasCapturedFrame = true
            // Stop receiving more frames; this service is one-shot.
            imageReader.setOnImageAvailableListener(null, null)

            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val width = image.width
            val height = image.height

            val rowPadding = rowStride - pixelStride * width

            val bitmap = createBitmap(width + rowPadding / pixelStride, height)

            bitmap.copyPixelsFromBuffer(buffer)

            image.close()

            runOCR(bitmap)
        }, Handler(Looper.getMainLooper()))
    }

    private fun runOCR(bitmap: Bitmap)
    {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        val recognizer = TextRecognition.getClient(
            JapaneseTextRecognizerOptions.Builder().build()
        )

        recognizer.process(inputImage)
            .addOnSuccessListener {
                Log.d("OCR captured", it.text)
                Toast.makeText(this, it.text, Toast.LENGTH_SHORT).show()
//                shutdownService()
            }
            .addOnFailureListener {
                Log.e("OCR captured", "Failed", it)
//                shutdownService()
            }
    }

    private fun shutdownService()
    {
        runCatching {
            if (::virtualDisplay.isInitialized) virtualDisplay.release()
        }
        runCatching {
            if (::imageReader.isInitialized) imageReader.close()
        }
        runCatching {
            if (::mediaProjection.isInitialized) mediaProjection.stop()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun isCaptureSessionReady(): Boolean
    {
        return ::mediaProjection.isInitialized &&
                ::virtualDisplay.isInitialized &&
                ::imageReader.isInitialized
    }
    override fun onDestroy()
    {
        super.onDestroy()
        runCatching {
            if (::virtualDisplay.isInitialized) virtualDisplay.release()
        }
        runCatching {
            if (::imageReader.isInitialized) imageReader.close()
        }
    }
}