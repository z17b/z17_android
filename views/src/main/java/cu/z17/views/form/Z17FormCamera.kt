package cu.z17.views.form

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cu.z17.views.camera.ResultType
import cu.z17.views.camera.Z17Camera
import cu.z17.views.picture.Z17BasePicture
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Z17FormCamera(
    modifier: Modifier = Modifier,
    type: Int = 1, // 1 -> Photo, // 2 -> Video
    rootPath: String,
    initialValue: String = "",
    onClose: () -> Unit,
    onRequestRealPath: (String) -> String
) {
    Box(modifier.safeContentPadding()) {
        var state by remember {
            mutableStateOf(Z17FormCameraState(value = initialValue, type = type))
        }

        val filePickerLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
                onResult = {
                    it?.path?.let { path ->
                        state = state.copy(
                            value = onRequestRealPath(path),
                            step = Z17FormCameraStep.PREVIEW
                        )
                    }
                }
            )

        fun newImageName(): String {
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
            return "${formatter.format(Date())}.jpeg"
        }

        fun newVideoName(): String {
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
            return "${formatter.format(Date())}.mp4"
        }

        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            // TOP
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        when (state.step) {
                            Z17FormCameraStep.SHOOTING -> onClose()

                            Z17FormCameraStep.PREVIEW -> {
                                state = state.copy(step = Z17FormCameraStep.SHOOTING)
                            }

                            Z17FormCameraStep.EDITING -> {
                                state = state.copy(step = Z17FormCameraStep.PREVIEW)
                            }
                        }
                    }
                ) {
                    Z17BasePicture(
                        modifier = Modifier.size(24.dp),
                        source = Icons.AutoMirrored.Filled.ArrowBackIos,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                    )
                }

                Spacer(Modifier.weight(1F))

                when (state.step) {
                    Z17FormCameraStep.SHOOTING -> {
                        IconButton(
                            onClick = {
                                filePickerLauncher.launch("image/*")
                            }
                        ) {
                            Z17BasePicture(
                                modifier = Modifier.size(24.dp),
                                source = Icons.Default.Folder,
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                            )
                        }
                    }

                    Z17FormCameraStep.PREVIEW -> {}

                    Z17FormCameraStep.EDITING -> {}
                }
            }

            // BODY
            when (state.step) {
                Z17FormCameraStep.SHOOTING -> {
                    Z17Camera(
                        modifier = Modifier
                            .weight(1F),
                        isVideo = state.type == 2,
                        imagePathToSave = rootPath + newImageName(),
                        videoPathToSave = rootPath + newVideoName(),
                        onClose = onClose,
                        onResult = { path, resultType, duration, rotation ->
                            when (resultType) {
                                ResultType.VIDEO -> {

                                }

                                ResultType.PHOTO -> {
                                    state = state.copy(
                                        value = path,
                                        step = Z17FormCameraStep.PREVIEW
                                    )
                                }

                                ResultType.DELETE -> {

                                }
                            }

                        },
                        onError = onClose,
                        showCloseBtn = false
                    )
                }

                Z17FormCameraStep.PREVIEW -> {
                    Z17BasePicture(modifier = Modifier.weight(1F), source = state.value)
                }

                Z17FormCameraStep.EDITING -> TODO()
            }

            // BOTTOM
        }
    }
}

@Immutable
data class Z17FormCameraState(
    val value: String,
    val type: Int, // 1 -> Photo, // 2 -> Video
    val step: Z17FormCameraStep = Z17FormCameraStep.SHOOTING
)

enum class Z17FormCameraStep {
    SHOOTING,
    PREVIEW,
    EDITING
}