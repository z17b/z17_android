package cu.z17.views.camera

import android.content.Context
import android.view.OrientationEventListener
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cu.z17.views.button.bounceClick
import cu.z17.views.imageEditor.sections.rotater.ROTATION_0
import cu.z17.views.imageEditor.sections.rotater.ROTATION_180
import cu.z17.views.imageEditor.sections.rotater.ROTATION_270
import cu.z17.views.imageEditor.sections.rotater.ROTATION_90
import cu.z17.views.label.Z17Label
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.utils.convertToMS
import cu.z17.views.utils.findActivity
import kotlinx.coroutines.delay

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
    onResult: (String, ResultType, Long, Float) -> Unit,
    onError: () -> Unit,
) {
    if (Z17CameraModule.getInstance().canUseCamera)
        Box(modifier = modifier) {
            val configuration = LocalConfiguration.current

            val previewView: PreviewView = remember { PreviewView(context) }

            var isFlashLightOn by remember {
                mutableStateOf(false)
            }

            var currentRecordTime by remember {
                mutableLongStateOf(0L)
            }

            var isFrontCamera by remember {
                mutableStateOf(false)
            }

            var actualRotation by remember {
                mutableFloatStateOf(ROTATION_0)
            }
            val animateRotation by animateFloatAsState(
                actualRotation,
                label = "ar"
            )

            fun handleFlashLight() {
                isFlashLightOn = !isFlashLightOn
                viewModel.handleFlashMode(isFlashLightOn)
            }

            val captureResult by viewModel.captureResult.collectAsStateWithLifecycle()

            val recordState by viewModel.recordingResult.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                val orientationEventListener = object : OrientationEventListener(context) {
                    override fun onOrientationChanged(orientation: Int) {
                        // Monitors orientation values to determine the target rotation value
                        actualRotation = when (orientation) {
                            in 45..134 -> ROTATION_270
                            in 135..224 -> ROTATION_180
                            in 225..314 -> ROTATION_90
                            else -> ROTATION_0
                        }
                    }
                }
                orientationEventListener.enable()

                while (true) {
                    delay(1000)

                    if (recordState == RecordingState.RECORDING) {
                        currentRecordTime += 1
                    }
                }
            }

            LaunchedEffect(captureResult) {
                if (captureResult == CaptureState.RESULT) {
                    onResult(imagePathToSave, ResultType.PHOTO, 0L, actualRotation)
                    viewModel.setStateRead()
                }

                if (captureResult == CaptureState.ERROR) {
                    onError()
                    viewModel.setStateRead()
                }
            }

            LaunchedEffect(recordState) {
                if (recordState == RecordingState.RESULT) {
                    onResult(videoPathToSave, ResultType.VIDEO, currentRecordTime * 1000, actualRotation)
                    viewModel.setStateRead()
                }
            }

            // CAMERA VIEW
            AndroidView(
                factory = {
                    previewView
                },
                modifier = Modifier
                    .fillMaxSize()
            )

            // CLOSE BTN
            IconButton(
                modifier = Modifier
                    .align(alignment = Alignment.TopStart),
                onClick = {
                    viewModel.cancelRecord()
                    onClose()
                }
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(30.dp),
                    source = Icons.Outlined.Clear,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }

            if (isVideo)
                Z17Label(
                    text = currentRecordTime.convertToMS(),
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(20.dp)
                        .background(color = Color.Black, shape = RoundedCornerShape(5.dp))
                        .padding(horizontal = 5.dp, vertical = 3.dp),
                    color = Color.White
                )

            Row(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CHANGE TORCH MODE
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                            shape = CircleShape
                        )
                        .padding(10.dp)
                        .clip(CircleShape)
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

                // VIDEO PAUSE-RESUME BTN
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

                // SHOT BTN
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
                        if (isVideo) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(5.dp)
                                    .background(
                                        color = if (recordState == RecordingState.RECORDING) MaterialTheme.colorScheme.primary else Color.White,
                                        shape = CircleShape
                                    )
                            )
                        } else
                            Z17BasePicture(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .padding(10.dp)
                                    .rotate(animateRotation),
                                source = Icons.Outlined.CameraAlt,
                                colorFilter = ColorFilter.tint(color = Color.Black)
                            )

                    }
                }

                // SWITCH CAMERA BTN
                AnimatedVisibility(visible = !isVideo || (recordState == RecordingState.NOT_REQUESTED)) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                                shape = CircleShape
                            )
                            .padding(10.dp)
                            .clickable {
                                if (isFrontCamera) {
                                    Z17CameraModule
                                        .getInstance()
                                        .changeToBackCamera()
                                } else {
                                    Z17CameraModule
                                        .getInstance()
                                        .changeToFrontCamera()
                                }
                                isFrontCamera = !isFrontCamera

                                if (isVideo) {
                                    viewModel.showVideoCamera(previewView, lifecycleOwner)
                                } else {
                                    viewModel.showCamera(previewView, lifecycleOwner)
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

            }

            LaunchedEffect(isVideo) {
                Z17CameraModule.getInstance().imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
                if (isVideo) {
                    viewModel.showVideoCamera(previewView, lifecycleOwner)
                } else {
                    viewModel.showCamera(previewView, lifecycleOwner)
                }
            }

            DisposableEffect(Unit) {
                val previewsConfiguration = configuration.orientation
                onDispose {
                    Z17CameraModule.getInstance().processCameraProvider.unbindAll()

                    try {
                        val currentActivity = context.findActivity()
                        currentActivity.requestedOrientation = previewsConfiguration
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
}

enum class CaptureState {
    NOT_REQUESTED,
    ERROR,
    RESULT
}

enum class RecordingState {
    CANCEL,
    FLIPPING,
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