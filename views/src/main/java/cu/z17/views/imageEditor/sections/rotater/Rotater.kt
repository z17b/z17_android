package cu.z17.views.imageEditor.sections.rotater

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cu.z17.views.picture.Z17BasePicture
import kotlinx.coroutines.delay

@Composable
fun Rotate(
    imageBitmap: Bitmap,
    degrees: Float,
    onImageRotated: (Bitmap) -> Unit,
    onCancel: () -> Unit,
) {
    var currentImage by remember {
        mutableStateOf<Bitmap?>(null)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Z17BasePicture(
            modifier = Modifier
                .weight(1F),
            source = currentImage,
            placeholder = MaterialTheme.colorScheme.background
        )

        RotaterBottomBar(
            onOk = {
                onImageRotated(currentImage ?: imageBitmap)
            },
            onCancel = {
                onCancel()
            }
        )

        LaunchedEffect(degrees) {
            currentImage = null
            delay(100)
            currentImage = imageBitmap.rotate(degrees)
        }
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

const val ROTATION_0 = 0F
const val ROTATION_90 = 90F
const val ROTATION_180 = 180F
const val ROTATION_270 = 270F
