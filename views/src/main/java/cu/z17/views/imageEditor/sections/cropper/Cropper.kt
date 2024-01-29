package cu.z17.views.imageEditor.sections.cropper

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cu.z17.views.imageEditor.sections.cropper.components.ImageCropper
import cu.z17.views.imageEditor.sections.cropper.components.model.OutlineType
import cu.z17.views.imageEditor.sections.cropper.components.model.RectCropShape
import cu.z17.views.imageEditor.sections.cropper.components.settings.CropDefaults
import cu.z17.views.imageEditor.sections.cropper.components.settings.CropOutlineProperty


@Composable
fun Cropper(
    imageBitmap: ImageBitmap,
    onCropSuccess: (ImageBitmap) -> Unit,
    onCancel: () -> Unit,
) {
    Box {
        val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }

        var crop by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ImageCropper(
                modifier = Modifier.weight(1F),
                imageBitmap = imageBitmap,
                contentDescription = "Image Cropper",
                cropStyle = CropDefaults.style(),
                cropProperties = CropDefaults.properties(
                    cropOutlineProperty = CropOutlineProperty(
                        OutlineType.Rect,
                        RectCropShape(0, "Rect")
                    ),
                    handleSize = handleSize,
                    rotatable = false
                ),
                crop = crop,
                onCropStart = {},
                onCropSuccess = {
                    onCropSuccess(it)
                    crop = false
                })

            CropperBottomBar(
                onOk = {
                    crop = true
                },
                onCancel = onCancel,
            )

            BackHandler {
                onCancel()
            }
        }
    }
}