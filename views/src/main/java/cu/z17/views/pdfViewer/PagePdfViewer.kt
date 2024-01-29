package cu.z17.views.pdfViewer

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import cu.z17.views.loader.Z17Shimmer
import cu.z17.views.zoomables.rememberZoomState
import cu.z17.views.zoomables.zoom
import kotlin.math.sqrt

@Composable
internal fun PagePdfViewer(
    modifier: Modifier = Modifier,
    actualPage: Pair<Int, Bitmap?>,
    cacheKey: String,
    width: Int,
    height: Int,
    context: Context,
    zoomable: Boolean,
) {
    if (actualPage.second != null) {
        val request = ImageRequest.Builder(context)
            .size(width, height)
            .memoryCacheKey(cacheKey)
            .data(actualPage.second)
            .build()

        Image(
            modifier = modifier
                .background(Color.White)
                .aspectRatio(1f / sqrt(2f))
                .fillMaxWidth()
                .then(
                    if (zoomable) Modifier.zoom(
                        clip = true,
                        zoomState = rememberZoomState(
                            limitPan = true,
                            rotatable = false
                        ),
                    ) else Modifier
                ),
            contentScale = ContentScale.Fit,
            painter = rememberAsyncImagePainter(request),
            contentDescription = "Page ${actualPage.first + 1}"
        )
    } else {
        Z17Shimmer(modifier = Modifier.size(width.dp, height.dp))
    }
}