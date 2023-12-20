package cu.z17.views.camera

import android.content.Context
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import cu.z17.singledi.SingletonInitializer
import java.util.concurrent.Executors

class Z17CamaraModule(val context: Context) {
    companion object : SingletonInitializer<Z17CamaraModule>()

    val cameraSelectorBack: CameraSelector
    val cameraSelectorFront: CameraSelector
    val processCameraProvider: ProcessCameraProvider
    val preview: Preview
    val imageCapture: ImageCapture
    val imageAnalysis: ImageAnalysis

    fun provideVideoCapture(): VideoCapture<Recorder> {
        val recorder = Recorder.Builder()
            .setExecutor(Executors.newSingleThreadExecutor())
            .build()

        return VideoCapture.withOutput(recorder)
    }

    private fun provideCameraSelectorBack(): CameraSelector {
        return CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    }

    private fun provideCameraSelectorFront(): CameraSelector {
        return CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
    }

    private fun provideCameraProvider(context: Context): ProcessCameraProvider {
        return ProcessCameraProvider.getInstance(context).get()
    }

    private fun provideCameraPreview(): Preview {
        return Preview.Builder().build()
    }

    private fun provideImageCapture(): ImageCapture {
        return ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_ON)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9).build()
    }

    private fun provideImageAnalysis(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
    }

    init {
        cameraSelectorBack = provideCameraSelectorBack()
        cameraSelectorFront = provideCameraSelectorFront()
        processCameraProvider = provideCameraProvider(context)
        preview = provideCameraPreview()
        imageCapture = provideImageCapture()
        imageAnalysis = provideImageAnalysis()
    }
}