package cu.z17.views.picture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.size.Scale
import cu.z17.singledi.SinglediException
import cu.z17.views.loader.Z17Shimmer
import cu.z17.views.utils.Z17BasePictureHeaders
import cu.z17.views.utils.Z17CoilDecoders
import cu.z17.views.utils.blurhash.BlurHashDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

@Composable
fun PictureFromUri(
    modifier: Modifier,
    uri: Uri,
    contentScale: ContentScale,
    description: String,
    filterQuality: FilterQuality,
    context: Context = LocalContext.current,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(context).apply {
            this.data(uri)
            this.memoryCacheKey(uri.path)
            this.diskCacheKey(uri.path)
            this.memoryCachePolicy(CachePolicy.DISABLED)
            this.diskCachePolicy(CachePolicy.ENABLED)
            this.dispatcher(Dispatchers.IO)
            this.interceptorDispatcher(Dispatchers.IO)
            this.crossfade(true)
            this.size(1920, 1080)
            this.scale(scale = Scale.FILL)

            this.dispatcher(Dispatchers.IO)
            this.interceptorDispatcher(Dispatchers.IO)
        }.build(),
        filterQuality = filterQuality,
        contentDescription = description,
        contentScale = contentScale,
        imageLoader = Z17CoilDecoders.getInstance().imageLoader
    )
}

@Composable
fun PictureFromUrl(
    modifier: Modifier,
    url: String,
    placeholder: Any,
    colorFilter: ColorFilter? = null,
    contentScale: ContentScale,
    description: String,
    filterQuality: FilterQuality,
    customHeaders: Map<String, String>? = null,
) {
    val context = LocalContext.current

    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(context).apply {
            this.data(url)
            this.memoryCacheKey(url)
            this.diskCacheKey(url)
            this.memoryCachePolicy(CachePolicy.DISABLED)
            this.diskCachePolicy(CachePolicy.ENABLED)
            this.dispatcher(Dispatchers.IO)
            this.interceptorDispatcher(Dispatchers.IO)
            this.crossfade(true)
            this.size(1920, 1080)
            this.scale(scale = Scale.FILL)
            this.listener(object : ImageRequest.Listener {
                override fun onError(request: ImageRequest, result: ErrorResult) {
                    Log.d("COIL", "request: ${request.data}, result: ${result.throwable}")
                }
            })

            // Setting placeholder
            if (placeholder is Drawable) {
                this.placeholder(placeholder)
                this.error(placeholder)
            }

            if (placeholder is Int) {
                this.placeholder(placeholder)
                this.error(placeholder)
            }

            if (placeholder is Color) {
                val shapeDrawable = ShapeDrawable(RectShape())
                shapeDrawable.intrinsicWidth = 200
                shapeDrawable.intrinsicHeight = 200
                shapeDrawable.paint.color = placeholder.hashCode()

                this.placeholder(shapeDrawable)
                this.error(shapeDrawable)
            }

            // Adding headers
            if (Z17CoilDecoders.getInstanceOrNull()?.needsHeader?.invoke(url) == true)
                if (customHeaders != null)
                    this.headers(Z17BasePictureHeaders.fromMapToHeaders(customHeaders)!!)
                else try {
                    if (Z17BasePictureHeaders.getInstance().thereAreHeaders()) {
                        this.headers(Z17BasePictureHeaders.getInstance().getHeaders()!!)
                    }
                } catch (e: SinglediException) {
                    e.printStackTrace()
                }
        }.build(),
        filterQuality = filterQuality,
        contentDescription = description,
        contentScale = contentScale,
        colorFilter = colorFilter,
        imageLoader = Z17CoilDecoders.getInstance().imageLoader
    )
}

@Composable
fun PictureWithBlurHash(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    contentScale: ContentScale,
    description: String = "",
    filterQuality: FilterQuality,
    blurHash: String,
    context: Context = LocalContext.current,
) {
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    if (bitmap.value == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            Z17Shimmer(modifier = Modifier.fillMaxSize())
        }
    } else {
        bitmap.value?.let {
            AsyncImage(
                modifier = modifier,
                model = ImageRequest.Builder(context).apply {
                    this.data(it)
                    this.memoryCacheKey(blurHash)
                    this.diskCacheKey(blurHash)
                    this.memoryCachePolicy(CachePolicy.DISABLED)
                    this.diskCachePolicy(CachePolicy.ENABLED)
                    this.dispatcher(Dispatchers.IO)
                    this.interceptorDispatcher(Dispatchers.IO)
                    this.crossfade(true)
                    this.size(1920, 1080)
                    this.scale(scale = Scale.FILL)

                    this.dispatcher(Dispatchers.IO).interceptorDispatcher(Dispatchers.IO)
                }.build(),
                filterQuality = filterQuality,
                contentDescription = description,
                contentScale = contentScale,
                colorFilter = colorFilter,
                imageLoader = Z17CoilDecoders.getInstance().imageLoader
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(500)
        bitmap.value = BlurHashDecoder.decode(blurHash, 140, 140, 1f, false)
    }
}

@Composable
fun PictureFromBitmap(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    contentScale: ContentScale,
    description: String,
    filterQuality: FilterQuality,
    bitmap: Bitmap,
    context: Context = LocalContext.current,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(context).apply {
            this.data(bitmap)
            this.memoryCacheKey(bitmap.hashCode().toString())
            this.diskCacheKey(bitmap.hashCode().toString())
            this.memoryCachePolicy(CachePolicy.DISABLED)
            this.diskCachePolicy(CachePolicy.ENABLED)
            this.dispatcher(Dispatchers.IO)
            this.interceptorDispatcher(Dispatchers.IO)
            this.crossfade(true)

            this.dispatcher(Dispatchers.IO)
            this.interceptorDispatcher(Dispatchers.IO)
        }.build(),
        filterQuality = filterQuality,
        contentDescription = description,
        contentScale = contentScale,
        colorFilter = colorFilter,
        imageLoader = Z17CoilDecoders.getInstance().imageLoader
    )
}

val ShimmerColorShades = listOf(

    Color.LightGray.copy(0.9f),

    Color.LightGray.copy(0.2f),

    Color.LightGray.copy(0.9f)
)
