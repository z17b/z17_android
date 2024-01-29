package cu.z17.views.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.memory.MemoryCache
import coil.request.CachePolicy
import cu.z17.singledi.SingletonInitializer
import kotlinx.coroutines.Dispatchers

class Z17CoilDecoders(private val context: Context) {

    companion object : SingletonInitializer<Z17CoilDecoders>()

    val imageLoader = ImageLoader.Builder(context)
        .decoderDispatcher(Dispatchers.IO)
        .fetcherDispatcher(Dispatchers.IO)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
            add(VideoFrameDecoder.Factory())
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