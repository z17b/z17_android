package cu.z17.views.camera

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cu.z17.views.button.bounceClick
import cu.z17.views.picture.Z17BasePicture

// REQUIRE: Z17 STORAGE PERMISSIONS
@Composable
fun Z17Camera(
    modifier: Modifier = Modifier,
    imagePathToSave: String,
    videoPathToSave: String,
    isVideo: Boolean = false,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: Z17CameraViewModel = viewModel(
        key = imagePathToSave + videoPathToSave,
        factory = Z17CameraViewModelFactory(
            imagePathToSave,
            videoPathToSave
        )
    ),
    context: Context = LocalContext.current,
    onClose: () -> Unit,
    onResult: (String, ResultType) -> Unit,
    onError: () -> Unit
) {
    Box(modifier = modifier) {
        val previewView: PreviewView = remember { PreviewView(context) }

        var isFlashLightOn by remember {
            mutableStateOf(false)
        }

        var isFrontCamera by remember {
            mutableStateOf(false)
        }

        fun handleFlashLight() {
            isFlashLightOn = !isFlashLightOn
            viewModel.handleFlashMode(isFlashLightOn)
        }

        val captureResult by viewModel.captureResult.collectAsStateWithLifecycle()

        if (captureResult == CaptureState.RESULT) {
            onResult(imagePathToSave, ResultType.PHOTO)
            viewModel.setStateRead()
        }

        if (captureResult == CaptureState.ERROR) {
            onError()
            viewModel.setStateRead()
        }

        val recordState by viewModel.recordingResult.collectAsStateWithLifecycle()

        if (recordState == RecordingState.RESULT) {
            onResult(videoPathToSave, ResultType.VIDEO)
            viewModel.setStateRead()
        }

        LaunchedEffect(isVideo, isFrontCamera) {
            if (isVideo) {
                if (isFrontCamera) {
                    viewModel.showFrontVideoCamera(previewView, lifecycleOwner)
                } else {
                    viewModel.showBackVideoCamera(previewView, lifecycleOwner)
                }
            } else {
                if (isFrontCamera) {
                    viewModel.showFrontCamera(previewView, lifecycleOwner)
                } else {
                    viewModel.showBackCamera(previewView, lifecycleOwner)
                }
            }
        }

        AndroidView(
            factory = {
                previewView
            },
            modifier = Modifier
                .fillMaxSize()
        )

        IconButton(
            modifier = Modifier
                .align(alignment = Alignment.TopStart),
            onClick = {
                onClose()
            }
        ) {
            Z17BasePicture(
                modifier = Modifier.size(30.dp),
                source = Icons.Outlined.Clear,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        }

        Row(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .clickable {
                        handleFlashLight()
                    },
                contentAlignment = Alignment.Center
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = if (isFlashLightOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }

            AnimatedVisibility(visible = (recordState == RecordingState.RECORDING || recordState == RecordingState.PAUSE)) {
                IconButton(
                    modifier = Modifier
                        .size(40.dp)
                        .bounceClick(),
                    onClick = {
                        if (recordState == RecordingState.PAUSE) viewModel.resumeRecord() else viewModel.pauseRecord()
                    }
                ) {
                    Z17BasePicture(
                        modifier = Modifier.fillMaxSize(),
                        source = if (recordState == RecordingState.PAUSE) Icons.Filled.PlayCircleFilled else Icons.Filled.PauseCircleFilled,
                        colorFilter = ColorFilter.tint(color = Color(0xFFc62828))
                    )
                }
            }

            Box(
                modifier = Modifier
                    .border(
                        4.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .padding(if (isVideo) 10.dp else 5.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    modifier = Modifier.bounceClick(),
                    onClick = {
                        if (!isVideo)
                            viewModel.captureAndSaveImage(context)
                        else {
                            if (recordState == RecordingState.NOT_REQUESTED) {
                                viewModel.startVideoRecord(
                                    context
                                )
                            } else {
                                viewModel.stopRecord()
                            }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isVideo) 50.dp else 60.dp)
                            .padding(5.dp)
                            .background(
                                color = if (isVideo && recordState == RecordingState.RECORDING) MaterialTheme.colorScheme.primary else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .alpha(if (recordState == RecordingState.RECORDING || recordState == RecordingState.PAUSE) 0F else 1F)
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .clickable {
                        if (recordState != RecordingState.RECORDING && recordState != RecordingState.PAUSE) {
                            isFrontCamera = !isFrontCamera
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.Cameraswitch,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        }

        LaunchedEffect(Unit) {
            Z17CamaraModule.getInstance().imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
        }
    }
}

enum class CaptureState {
    NOT_REQUESTED,
    ERROR,
    RESULT
}

enum class RecordingState {
    NOT_REQUESTED,
    RECORDING,
    RESULT,
    PAUSE
}

enum class ResultType {
    VIDEO,
    PHOTO,
    DELETE
}