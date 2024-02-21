package cu.z17.views.picture

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import cu.z17.views.utils.Z17BasePictureHeaders
import okhttp3.Headers

@Composable
fun Z17BasePicture(
    modifier: Modifier,
    source: Any?,
    placeholder: Any = Icons.Outlined.Image,
    colorFilter: ColorFilter? = null,
    contentScale: ContentScale = ContentScale.Fit,
    description: String = "",
    filterQuality: FilterQuality = FilterQuality.High,
    customHeaders: Map<String, String>? = null,
    blurHash: String = ""
) {
    Box(modifier) {
        when {
            source == null && blurHash.isNotEmpty() -> {
                PictureWithBlurHash(
                    modifier = modifier,
                    colorFilter = null,
                    contentScale = contentScale,
                    description = description,
                    filterQuality = filterQuality,
                    blurHash = blurHash
                )
            }

            source is String && source.isNotEmpty() -> {
                PictureFromUrl(
                    modifier = Modifier.fillMaxSize(),
                    url = source,
                    placeholder = if (placeholder is Int || placeholder is Drawable || placeholder is Color) placeholder else cu.z17.views.R.drawable.placeholder,
                    contentScale = contentScale,
                    description = description,
                    colorFilter = colorFilter,
                    filterQuality = filterQuality,
                    customHeaders = Z17BasePictureHeaders.fromMapToHeaders(customHeaders)
                )
            }

            source is Uri -> {
                PictureFromUri(
                    uri = source,
                    modifier = Modifier.fillMaxSize(),
                    description = description,
                    filterQuality = filterQuality,
                    contentScale = contentScale,
                )
            }

            source is Int -> {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = source,
                    contentDescription = description,
                    colorFilter = colorFilter,
                    contentScale = contentScale,
                    filterQuality = filterQuality,
                )
            }

            source is ImageVector -> {
                Image(
                    imageVector = source,
                    contentDescription = description,
                    colorFilter = colorFilter,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }

            source is Bitmap -> {
                val bitmap = remember {
                    source.prepareToDraw()
                    source
                }

                PictureFromBitmap(
                    bitmap = bitmap,
                    description = description,
                    colorFilter = colorFilter,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    filterQuality = filterQuality
                )
            }

            source is Drawable -> {
                val bitmap = remember {
                    val aux = source.toBitmap()
                    aux.prepareToDraw()
                    aux
                }

                PictureFromBitmap(
                    bitmap = bitmap,
                    description = description,
                    colorFilter = colorFilter,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    filterQuality = filterQuality
                )
            }

            source is Color -> {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawRect(
                        color = source,
                    )
                }
            }

            else -> {
                Z17BasePicture(
                    modifier = Modifier.fillMaxSize(),
                    source = placeholder,
                    colorFilter = colorFilter,
                    contentScale = contentScale,
                    description = description,
                    filterQuality = filterQuality
                )
            }
        }
    }
}