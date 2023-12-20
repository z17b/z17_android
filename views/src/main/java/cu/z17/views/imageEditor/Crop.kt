package cu.z17.views.imageEditor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cu.z17.views.imageEditor.cropper.ImageCropper
import cu.z17.views.imageEditor.cropper.model.OutlineType
import cu.z17.views.imageEditor.cropper.model.RectCropShape
import cu.z17.views.imageEditor.cropper.settings.CropDefaults
import cu.z17.views.imageEditor.cropper.settings.CropOutlineProperty
import cu.z17.views.picture.Z17BasePicture


@Composable
fun Crop(
    imageBitmap: ImageBitmap,
    onCropSuccess: (ImageBitmap) -> Unit,
    onCancel: () -> Unit
) {
    Box {
        val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }

        var crop by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ImageCropper(modifier = Modifier.weight(1F),
                imageBitmap = imageBitmap,
                contentDescription = "Image Cropper",
                cropStyle = CropDefaults.style(),
                cropProperties = CropDefaults.properties(
                    cropOutlineProperty = CropOutlineProperty(
                        OutlineType.Rect, RectCropShape(0, "Rect")
                    ), handleSize = handleSize
                ),
                crop = crop,
                onCropStart = {},
                onCropSuccess = {
                    onCropSuccess(it)
                    crop = false
                })

            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                        .padding(10.dp)
                        .clickable {
                            onCancel()
                        }, contentAlignment = Alignment.Center
                ) {
                    Z17BasePicture(
                        modifier = Modifier.size(25.dp),
                        source = Icons.Outlined.Clear,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                        .padding(10.dp)
                        .clickable {
                            crop = true
                        }, contentAlignment = Alignment.Center
                ) {
                    Z17BasePicture(
                        modifier = Modifier.size(25.dp),
                        source = Icons.Outlined.Crop,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                    )
                }
            }

            BackHandler {
                onCancel()
            }
        }
    }
}
