package cu.z17.views.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Looper
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

class Z17CameraViewModel(private val imagePathToSave: String, private val videoPathToSave: String) :
    ViewModel() {

    private val z17CameraModule: Z17CameraModule = Z17CameraModule.getInstance()

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
        if (on) z17CameraModule.imageCapture.flashMode = FLASH_MODE_ON
        else z17CameraModule.imageCapture.flashMode = FLASH_MODE_OFF
    }

    fun showCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
    ) {
        z17CameraModule.preview.setSurfaceProvider(previewView.surfaceProvider)
        try {
            z17CameraModule.processCameraProvider.unbindAll()
            z17CameraModule.processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                z17CameraModule.cameraSelector,
                z17CameraModule.preview,
                z17CameraModule.imageAnalysis,
                z17CameraModule.imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showVideoCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
    ) {
        z17CameraModule.preview.setSurfaceProvider(previewView.surfaceProvider)
        videoCapture = z17CameraModule.provideVideoCapture()

        try {
            z17CameraModule.processCameraProvider.unbindAll()
            z17CameraModule.processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                z17CameraModule.cameraSelector,
                z17CameraModule.preview,
                videoCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun captureAndSaveImage(
        context: Context,
    )  {
        // for capture output
        _captureResult.value = CaptureState.SAVING
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val file = File(imagePathToSave)
                val fos = FileOutputStream(file)
                val outputOptions = ImageCapture.OutputFileOptions
                    .Builder(fos)
                    .build()

                z17CameraModule.imageCapture.takePicture(
                    outputOptions,
                    Z17CameraModule.getInstance().backgroundExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            // stop image
                            z17CameraModule.processCameraProvider.unbindAll()
                            _captureResult.value = CaptureState.RESULT
                        }

                        override fun onError(exception: ImageCaptureException) {
                            _captureResult.value = CaptureState.ERROR
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalCamera2Interop::class) @SuppressLint("MissingPermission")
    fun startVideoRecord(context: Context) {
        _recordingResult.value = RecordingState.RECORDING
        val outputOptions = FileOutputOptions.Builder(File(videoPathToSave)).build()

        recording = videoCapture?.output!!.prepareRecording(context, outputOptions)
            .withAudioEnabled()
            .start(
                Z17CameraModule.getInstance().backgroundExecutor
            ) { event ->
                if (event is VideoRecordEvent.Finalize && _recordingResult.value != RecordingState.CANCEL) {
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

    fun cancelRecord() {
        _recordingResult.value = RecordingState.CANCEL
        recording?.stop()
        recording?.close()
    }

}