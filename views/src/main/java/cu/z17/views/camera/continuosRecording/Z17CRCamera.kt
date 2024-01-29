package cu.z17.views.camera.continuosRecording


import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import cu.z17.views.camera.RecordingState
import cu.z17.views.camera.ResultType
import cu.z17.views.camera.Z17CameraModule
import cu.z17.views.label.Z17Label
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.utils.convertToMS
import cu.z17.views.utils.findActivity
import kotlinx.coroutines.delay

// REQUIRE: Z17 STORAGE PERMISSIONS
@Composable
fun Z17CRCamera(
    modifier: Modifier = Modifier,
    videoPathToSave: String,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: Z17CRCameraViewModel = viewModel(
        key = videoPathToSave,
        factory = Z17CRCameraViewModelFactory(
            videoPathToSave
        )
    ),
    context: Context = LocalContext.current,
    onClose: () -> Unit,
    onStart: (String, ResultType) -> Unit,
    onCameraFlip: () -> Unit,
) {
    if (Z17CameraModule.getInstance().canUseCamera)
        Box(modifier = modifier) {
            val configuration = LocalConfiguration.current

            val previewView: PreviewView = remember { PreviewView(context) }

            var isFrontCamera by remember {
                mutableStateOf(false)
            }

            var currentRecordTime by remember {
                mutableLongStateOf(0L)
            }

            val recordState by viewModel.recordingResult.collectAsStateWithLifecycle()

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

            Row(
                modifier = Modifier
                    .align(
                        alignment =
                        if (recordState == RecordingState.RECORDING) Alignment.TopEnd else
                            Alignment.BottomCenter
                    )
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = if (recordState == RecordingState.RECORDING) Arrangement.End else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .background(color = Color.Black, shape = RoundedCornerShape(5.dp))
                        .padding(horizontal = 5.dp, vertical = 3.dp),
                ) {
                    var isMid by remember {
                        mutableStateOf(true)
                    }

                    val animatedColor by animateColorAsState(
                        targetValue = if (!isMid) Color.Red else Color.Red.copy(
                            alpha = 0.5F
                        ),
                        label = "aci",
                        finishedListener = {
                            isMid = !isMid
                        },
                        animationSpec = tween(1000)
                    )

                    if (recordState == RecordingState.RECORDING)
                        Box(
                            modifier = Modifier
                                .size(width = 10.dp, height = 10.dp)
                                .clip(CircleShape)
                                .background(animatedColor)
                        ) {
                            LaunchedEffect(Unit) {
                                isMid = !isMid
                            }
                        }

                    Z17Label(
                        text = currentRecordTime.convertToMS(),
                        color = Color.White,
                    )
                }

                // SHOT BTN
                IconButton(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(35.dp)
                        .bounceClick(),
                    onClick = {
                        when (recordState) {
                            RecordingState.NOT_REQUESTED -> viewModel.startVideoRecord(context)
                            else -> viewModel.stopRecord()
                        }
                    }
                ) {
                    Z17BasePicture(
                        modifier = Modifier.fillMaxSize(),
                        source = if (recordState == RecordingState.RECORDING) Icons.Filled.StopCircle else Icons.Filled.PlayCircleFilled,
                        colorFilter = ColorFilter.tint(color = Color(0xFFc62828))
                    )
                }

                // SWITCH CAMERA BTN
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                            shape = CircleShape
                        )
                        .padding(3.dp)
                        .clickable {
                            if (recordState == RecordingState.RECORDING || recordState == RecordingState.PAUSE) {
                                viewModel.flip()

                                viewModel.stopRecord()

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

                                viewModel.showVideoCamera(previewView, lifecycleOwner)

                                onCameraFlip()

                                viewModel.startVideoRecord(context)
                            } else {
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

                                viewModel.showVideoCamera(previewView, lifecycleOwner)
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
                Z17CameraModule.getInstance().imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
                viewModel.showVideoCamera(previewView, lifecycleOwner)

                while (true) {
                    delay(1000)

                    if (recordState == RecordingState.RECORDING) {
                        currentRecordTime += 1
                    }
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