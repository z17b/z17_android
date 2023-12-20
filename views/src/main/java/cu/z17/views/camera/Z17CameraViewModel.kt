package cu.z17.views.camera

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream

class Z17CameraViewModel(private val imagePathToSave: String, private val videoPathToSave: String) :
    ViewModel() {

    private val z17CamaraModule: Z17CamaraModule = Z17CamaraModule.getInstance()

    private val _captureResult = MutableStateFlow(CaptureState.NOT_REQUESTED)
    val captureResult = _captureResult.asStateFlow()

    private val _recordingResult = MutableStateFlow(RecordingState.NOT_REQUESTED)
    val recordingResult = _recordingResult.asStateFlow()

    var recording: Recording? = null

    private var videoCapture: VideoCapture<Recorder>? = null

    fun setStateRead() {
        _captureResult.value = CaptureState.NOT_REQUESTED
        _recordingResult.value = RecordingState.NOT_REQUESTED
    }

    fun handleFlashMode(on: Boolean) {
        if (on) z17CamaraModule.imageCapture.flashMode = FLASH_MODE_ON
        else z17CamaraModule.imageCapture.flashMode = FLASH_MODE_OFF
    }

    fun showBackCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        z17CamaraModule.preview.setSurfaceProvider(previewView.surfaceProvider)
        try {
            z17CamaraModule.processCameraProvider.unbindAll()
            z17CamaraModule.processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                z17CamaraModule.cameraSelectorBack,
                z17CamaraModule.preview,
                z17CamaraModule.imageAnalysis,
                z17CamaraModule.imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showFrontCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        z17CamaraModule.preview.setSurfaceProvider(previewView.surfaceProvider)
        try {
            z17CamaraModule.processCameraProvider.unbindAll()
            z17CamaraModule.processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                z17CamaraModule.cameraSelectorFront,
                z17CamaraModule.preview,
                z17CamaraModule.imageAnalysis,
                z17CamaraModule.imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showBackVideoCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        z17CamaraModule.preview.setSurfaceProvider(previewView.surfaceProvider)
        videoCapture = z17CamaraModule.provideVideoCapture()

        try {
            z17CamaraModule.processCameraProvider.unbindAll()
            z17CamaraModule.processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                z17CamaraModule.cameraSelectorBack,
                z17CamaraModule.preview,
                videoCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showFrontVideoCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        z17CamaraModule.preview.setSurfaceProvider(previewView.surfaceProvider)
        videoCapture = z17CamaraModule.provideVideoCapture()

        try {
            z17CamaraModule.processCameraProvider.unbindAll()
            z17CamaraModule.processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                z17CamaraModule.cameraSelectorFront,
                z17CamaraModule.preview,
                videoCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun captureAndSaveImage(
        context: Context
    ) {
        // for capture output
        val file = File(imagePathToSave)
        val fos = FileOutputStream(file)
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(fos)
            .build()

        z17CamaraModule.imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // stop image
                    z17CamaraModule.processCameraProvider.unbindAll()

                    _captureResult.value = CaptureState.RESULT
                }

                override fun onError(exception: ImageCaptureException) {
                    _captureResult.value = CaptureState.ERROR
                }
            }
        )
    }

    @SuppressLint("MissingPermission")
    fun startVideoRecord(context: Context) {
        _recordingResult.value = RecordingState.RECORDING
        val outputOptions = FileOutputOptions.Builder(File(videoPathToSave)).build()

        recording = videoCapture?.output!!.prepareRecording(context, outputOptions)
            .withAudioEnabled()
            .start(
                ContextCompat.getMainExecutor(context)
            ) { event ->
                if (event is VideoRecordEvent.Finalize) {
                    val uri = event.outputResults.outputUri
                    if (uri != Uri.EMPTY) {
                        _recordingResult.value = RecordingState.RESULT
                    }
                }
            }
    }

    fun resumeRecord() {
        _recordingResult.value = RecordingState.RECORDING
        recording?.resume()
    }

    fun pauseRecord() {
        _recordingResult.value = RecordingState.PAUSE
        recording?.pause()
    }

    fun stopRecord() {
        _recordingResult.value = RecordingState.NOT_REQUESTED
        recording?.stop()
        recording?.close()
    }
}