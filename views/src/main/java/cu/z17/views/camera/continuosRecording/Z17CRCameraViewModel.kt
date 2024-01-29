package cu.z17.views.camera.continuosRecording

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import cu.z17.views.camera.RecordingState
import cu.z17.views.camera.Z17CameraModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class Z17CRCameraViewModel(private val videoPathToSave: String) :
    ViewModel() {

    private val z17CameraModule: Z17CameraModule = Z17CameraModule.getInstance()

    private val _recordingResult = MutableStateFlow(RecordingState.NOT_REQUESTED)
    val recordingResult = _recordingResult.asStateFlow()

    private var recording: Recording? = null

    private var videoCapture: VideoCapture<Recorder>? = null

    private var cut: Int = 0

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

    @OptIn(ExperimentalCamera2Interop::class)
    @SuppressLint("MissingPermission")
    fun startVideoRecord(context: Context) {
        _recordingResult.value = RecordingState.RECORDING

        cut++

        val extension = videoPathToSave.split(".").last()
        val path = videoPathToSave.replace(".${extension}", "") + "_${cut}_.$extension"

        val outputOptions = FileOutputOptions.Builder(File(path)).build()

        recording = videoCapture?.output!!.prepareRecording(context, outputOptions)
            .withAudioEnabled()
            .start(
                ContextCompat.getMainExecutor(context)
            ) { event ->
                if (event is VideoRecordEvent.Finalize && _recordingResult.value != RecordingState.CANCEL) {
                    val uri = event.outputResults.outputUri
                    if (uri != Uri.EMPTY) {
                        if (_recordingResult.value == RecordingState.FLIPPING)
                            _recordingResult.value = RecordingState.RESULT
                    }
                }
            }
    }

    fun stopRecord() {
        if (_recordingResult.value != RecordingState.FLIPPING)
            _recordingResult.value = RecordingState.NOT_REQUESTED
        recording?.stop()
        recording?.close()
    }

    fun flip() {
        _recordingResult.value = RecordingState.FLIPPING
    }

    fun cancelRecord() {
        _recordingResult.value = RecordingState.CANCEL
        recording?.stop()
        recording?.close()
    }

}