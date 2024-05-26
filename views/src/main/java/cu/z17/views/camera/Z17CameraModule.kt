package cu.z17.views.camera

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.impl.ImageCaptureConfig
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import cu.z17.compress.compressFormat
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

    val backgroundExecutor = ContextCompat.getMainExecutor(context)

    var defaultFormat: Bitmap.CompressFormat? = null

    var defaultVideoBitrate: Int = 1_000_000

    fun provideVideoCapture(): VideoCapture<Recorder> {
        val recorder = Recorder.Builder()
            .setTargetVideoEncodingBitRate(defaultVideoBitrate) // Establece el bitrate a 2 Mbps
            .setExecutor(Executors.newSingleThreadExecutor())
            .setQualitySelector(QualitySelector.from(Quality.HD))
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
        val screenSize = Size(1080, 1920)
        val resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
            ResolutionStrategy(
                screenSize,
                ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER
            )
        ).build()
        return Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .build()
    }

    private fun provideImageCapture(): ImageCapture {val screenSize = Size(1080, 1920)
        val resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
            ResolutionStrategy(
                screenSize,
                ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER
            )
        ).build()
        return ImageCapture
            .Builder()
            .setResolutionSelector(resolutionSelector)
            .setJpegQuality(70)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(ImageCapture.FLASH_MODE_ON)
            .build()
    }

    private fun provideImageAnalysis(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
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