package cu.z17.views.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.executeBlocking
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import cu.z17.singledi.SinglediException
import cu.z17.singledi.SingletonInitializer
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import java.io.File
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class Z17CoilDecoders(
    private val context: Context,
    private val trustFactor: Pair<SSLSocketFactory, X509TrustManager>? = null,
) {
    companion object : SingletonInitializer<Z17CoilDecoders>()

    val imageLoader = ImageLoader.Builder(context)
        .decoderDispatcher(Dispatchers.IO)
        .fetcherDispatcher(Dispatchers.IO)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(1.0)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(File(context.cacheDir.path, "cache"))
                .maxSizePercent(1.0)
                .build()
        }
        .crossfade(true)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
            add(VideoFrameDecoder.Factory())
        }
        .okHttpClient {
            OkHttpClient().newBuilder().apply {
                trustFactor?.let {
                    this.sslSocketFactory(
                        it.first,
                        it.second
                    )
                    this.hostnameVerifier { _, _ -> true }
                }
            }.build()
        }
        .build()

    fun clearCacheFor(url: String) {
        imageLoader.memoryCache?.remove(MemoryCache.Key(url))
        imageLoader.diskCache?.remove(url)

        Log.d("COIL_CACHE", "Cache clear for $url")
    }

    fun getInCache(
        url: String,
        customHeaders: Map<String, String>? = null,
    ): Bitmap? {
        val imageRequest = ImageRequest.Builder(context).apply {
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
            // Adding headers
            if (!url.contains("s3.todus.cu/official") && !url.contains("s3.todus.cu/catalog") && !url.startsWith(
                    "https://todus.cu"
                )
            )
                if (customHeaders != null)
                    this.headers(Z17BasePictureHeaders.fromMapToHeaders(customHeaders)!!)
                else try {
                    if (Z17BasePictureHeaders.getInstance().thereAreHeaders()) {
                        this.headers(Z17BasePictureHeaders.getInstance().getHeaders()!!)
                    }
                } catch (e: SinglediException) {
                    e.printStackTrace()
                }
        }.build()

        val loader = Z17CoilDecoders.getInstanceOrNull()?.imageLoader

        return if (loader != null) {
            try {
                val result = (loader.executeBlocking(imageRequest) as SuccessResult).drawable
                val bitmap = (result as BitmapDrawable).bitmap

                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else null
    }
}