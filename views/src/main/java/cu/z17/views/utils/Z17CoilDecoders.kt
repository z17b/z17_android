package cu.z17.views.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
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

    fun getInCache(key: String) = imageLoader.memoryCache?.get(MemoryCache.Key(key))?.bitmap
    fun setInCache(key: String, bitmap: Bitmap) =
        imageLoader.memoryCache?.set(MemoryCache.Key(key), MemoryCache.Value(bitmap))
}