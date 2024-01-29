package cu.z17.views.imageEditor.sections.viewer

import android.graphics.Bitmap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.zoomables.rememberZoomState
import cu.z17.views.zoomables.zoom


@Composable
fun Viewer(
    source: Bitmap,
) {
    Z17BasePicture(
        modifier = Modifier.fillMaxSize().zoom(
            clip = true,
            zoomState = rememberZoomState(limitPan = true, rotatable = false)
        ),
        source = source
    )
}