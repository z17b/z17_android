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
import coil.size.Size
import cu.z17.singledi.SinglediException
import cu.z17.views.loader.Z17Shimmer
import cu.z17.views.utils.Z17BasePictureHeaders
import cu.z17.views.utils.Z17CoilDecoders
import cu.z17.views.utils.blurhash.BlurHashDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okhttp3.Headers

@Composable
fun PictureFromUri(
    modifier: Modifier,
    uri: Uri,
    contentScale: ContentScale,
    description: String,
    filterQuality: FilterQuality,
    context: Context = LocalContext.current
) {
    val source = remember {
        val imageRequest = ImageRequest.Builder(context)
            .data(uri)
            .memoryCacheKey(uri.path)
            .diskCacheKey(uri.path)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .interceptorDispatcher(Dispatchers.IO)
            .crossfade(true)

        imageRequest
            .dispatcher(Dispatchers.IO)
            .interceptorDispatcher(Dispatchers.IO)
            .build()
    }

    AsyncImage(
        modifier = modifier,
        model = source,
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
    customHeaders: Headers? = null
) {
    val context = LocalContext.current

    AsyncImage(
        modifier = modifier,
        model = remember {
            val imageRequest = ImageRequest.Builder(context)
                .data(url)
                .memoryCacheKey(url)
                .diskCacheKey(url)
                .crossfade(true)
                .size(Size.ORIGINAL)
                .listener(object : ImageRequest.Listener {
                    override fun onError(request: ImageRequest, result: ErrorResult) {
                        Log.d("COIL", "request: ${request.data}, result: ${result.throwable}")
                    }
                })

            // Setting placeholder
            if (placeholder is Drawable) {
                imageRequest
                    .placeholder(placeholder)
                    .error(placeholder)
            }

            if (placeholder is Int) {
                imageRequest
                    .placeholder(placeholder)
                    .error(placeholder)
            }

            if (placeholder is Color) {
                val shapeDrawable = ShapeDrawable(RectShape())
                shapeDrawable.intrinsicWidth = 200
                shapeDrawable.intrinsicHeight = 200
                shapeDrawable.paint.color = placeholder.hashCode()

                imageRequest
                    .placeholder(shapeDrawable)
                    .error(shapeDrawable)
            }

            // Adding headers
            if (!url.contains("s3.todus.cu/official") && !url.contains("s3.todus.cu/catalog") && !url.startsWith(
                    "https://todus.cu"
                )
            )
                if (customHeaders != null)
                    imageRequest.headers(customHeaders)
                else try {
                    if (Z17BasePictureHeaders.getInstance().thereAreHeaders())
                        imageRequest.headers(Z17BasePictureHeaders.getInstance().getHeaders()!!)
                } catch (e: SinglediException) {
                    e.printStackTrace()
                }

            imageRequest
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .dispatcher(Dispatchers.IO)
                .interceptorDispatcher(Dispatchers.IO)
                .build()
        },
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
    context: Context = LocalContext.current
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
                model = remember {
                    val imageRequest = ImageRequest.Builder(context)
                        .data(it)
                        .memoryCacheKey(blurHash)
                        .diskCacheKey(blurHash)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .dispatcher(Dispatchers.IO)
                        .interceptorDispatcher(Dispatchers.IO)
                        .crossfade(true)

                    imageRequest.dispatcher(Dispatchers.IO).interceptorDispatcher(Dispatchers.IO)
                        .build()
                },
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
    context: Context = LocalContext.current
) {
    AsyncImage(
        modifier = modifier,
        model = remember {
            val imageRequest = ImageRequest.Builder(context)
                .data(bitmap)
                .memoryCacheKey(bitmap.hashCode().toString())
                .diskCacheKey(bitmap.hashCode().toString())
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .dispatcher(Dispatchers.IO)
                .interceptorDispatcher(Dispatchers.IO)
                .crossfade(true)

            imageRequest
                .dispatcher(Dispatchers.IO)
                .interceptorDispatcher(Dispatchers.IO)
                .build()
        },
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
