package cu.z17.views.picture

import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun Z17BlurImage(
    modifier: Modifier,
    source: Bitmap,
    colorFilter: ColorFilter? = null,
    contentScale: ContentScale = ContentScale.Fit,
    description: String = "",
    filterQuality: FilterQuality = FilterQuality.High,
    size: Int? = null,
    blurRadio: Float
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        LegacyBlurImage(
            modifier = modifier,
            source = source,
            colorFilter = colorFilter,
            contentScale = contentScale,
            description = description,
            filterQuality = filterQuality,
            size = size,
            blurRadio = blurRadio
        )
    } else {
        Z17BasePicture(
            modifier = modifier.blur(blurRadio.dp),
            source = source,
            colorFilter = colorFilter,
            contentScale = contentScale,
            description = description,
            filterQuality = filterQuality,
            size = size
        )
    }
}

@Composable
private fun LegacyBlurImage(
    modifier: Modifier,
    source: Bitmap,
    colorFilter: ColorFilter? = null,
    contentScale: ContentScale = ContentScale.Fit,
    description: String = "",
    filterQuality: FilterQuality = FilterQuality.High,
    size: Int? = null,
    blurRadio: Float
) {
    val context = LocalContext.current

    remember {
        try {
            val renderScript = RenderScript.create(context)
            val bitmapAlloc = Allocation.createFromBitmap(renderScript, source)
            ScriptIntrinsicBlur.create(renderScript, bitmapAlloc.element).apply {
                setRadius(blurRadio)
                setInput(bitmapAlloc)
                forEach(bitmapAlloc)
            }
            bitmapAlloc.copyTo(source)
            renderScript.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }



    Z17BasePicture(
        modifier = modifier,
        source = source,
        colorFilter = colorFilter,
        contentScale = contentScale,
        description = description,
        filterQuality = filterQuality,
        size = size
    )
}