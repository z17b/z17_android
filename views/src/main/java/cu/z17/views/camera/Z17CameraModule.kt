package cu.z17.views.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import cu.z17.compress.compressFormat
import cu.z17.compress.constraint.Compression
import cu.z17.compress.constraint.Constraint
import cu.z17.compress.constraint.FormatConstraint
import cu.z17.compress.constraint.format
import cu.z17.singledi.SingletonInitializer
import java.util.concurrent.Executors

class Z17CameraModule(val context: Context, val initialDefaultFormat: String = "webp") {
    companion object : SingletonInitializer<Z17CameraModule>()

    private var lensFacing = CameraSelector.LENS_FACING_FRONT

    private var hasFrontCamera = true
    private var hasBackCamera = true

    private var canChange = true

    var canUseCamera = true

    var cameraSelector: CameraSelector =
        CameraSelector.Builder().requireLensFacing(lensFacing).build()

    val processCameraProvider: ProcessCameraProvider
    val preview: Preview
    val imageCapture: ImageCapture
    val imageAnalysis: ImageAnalysis

    var defaultFormat: Bitmap.CompressFormat? = null

    fun provideVideoCapture(): VideoCapture<Recorder> {
        val recorder = Recorder.Builder()
            .setExecutor(Executors.newSingleThreadExecutor())
            .build()

        return VideoCapture.withOutput(recorder)
    }

    fun changeToFrontCamera() {
        if (canChange)
            lensFacing = CameraSelector.LENS_FACING_FRONT
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    }

    fun changeToBackCamera() {
        if (canChange)
            lensFacing = CameraSelector.LENS_FACING_BACK
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    }

    private fun provideCameraProvider(context: Context): ProcessCameraProvider {
        return ProcessCameraProvider.getInstance(context).get()
    }

    private fun provideCameraPreview(): Preview {
        return Preview.Builder().build()
    }

    private fun provideImageCapture(): ImageCapture {
        return ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_ON).build()
    }

    private fun provideImageAnalysis(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
    }



    init {
        processCameraProvider = provideCameraProvider(context)

        if (!processCameraProvider.hasCamera(cameraSelector)) {

            canChange = false
            hasFrontCamera = false
        }

        changeToBackCamera()

        if (!processCameraProvider.hasCamera(cameraSelector)) {
            canChange = false
            hasBackCamera = false

            if (hasFrontCamera) changeToFrontCamera()
        }

        if (!hasFrontCamera && !hasBackCamera) canUseCamera = false

        preview = provideCameraPreview()
        imageCapture = provideImageCapture()
        imageAnalysis = provideImageAnalysis()

        defaultFormat = initialDefaultFormat.compressFormat()
    }
}