package cu.z17.views.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cu.z17.views.camera.ResultType
import cu.z17.views.camera.Z17Camera
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Z17FormCamera(
    modifier: Modifier = Modifier,
    type: Int = 1, // 1 -> Photo, // 2 -> Video
    rootPath: String,
    initialValue: String = "",
    onClose: () -> Unit
) {
    Box(modifier.safeContentPadding()) {
        var state by remember {
            mutableStateOf(Z17FormCameraState(value = initialValue, type = type))
        }

        fun newImageName(): String {
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
            return "${formatter.format(Date())}.jpeg"
        }

        fun newVideoName(): String {
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
            return "${formatter.format(Date())}.mp4"
        }

        Column {
            // TOP

            // BODY
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

                        }

                        ResultType.DELETE -> {

                        }
                    }

                },
                onError = onClose
            )

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