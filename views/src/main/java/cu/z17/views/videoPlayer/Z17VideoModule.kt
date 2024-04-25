package cu.z17.views.videoPlayer

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.exoplayer.upstream.experimental.ExperimentalBandwidthMeter
import cu.z17.singledi.SingletonInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class Z17VideoModule(private val context: Context) {
    companion object : SingletonInitializer<Z17VideoModule>() {
        private const val DOWNLOAD_CONTENT_DIRECTORY = "stream_videos_cache"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @UnstableApi
    val bandwidthMeter = ExperimentalBandwidthMeter.Builder(context)
        .setResetOnNetworkTypeChange(true)
        .build()

    private val downloadContentDirectory =
        File(context.cacheDir, DOWNLOAD_CONTENT_DIRECTORY)

    @UnstableApi
    private val standaloneDatabaseProvider = StandaloneDatabaseProvider(context)

    @UnstableApi
    private val noOpCacheEvictor = NoOpCacheEvictor()

    private val cacheDB = hashMapOf<String, SimpleCache>()

    @UnstableApi
    fun getCacheDirectory(identifier: String): SimpleCache {
        val folderId = getFolderId(identifier)
        var cacheInDb = cacheDB[folderId]
        if (cacheInDb == null) {
            cacheDB[folderId] = SimpleCache(
                File(downloadContentDirectory, folderId),
                noOpCacheEvictor,
                standaloneDatabaseProvider
            )
            cacheInDb = cacheDB[folderId]
        }

        return cacheInDb!!
    }

    @OptIn(UnstableApi::class)
    fun clearFromRAMCache(identifier: String) {
        val folderId = getFolderId(identifier)
        val simpleCache = cacheDB[folderId]
        simpleCache?.release()
        cacheDB.remove(folderId)
    }

    fun deleteDISKCache(identifier: String) {
        val folderId = getFolderId(identifier)
        val videoFolder = File(downloadContentDirectory, folderId)
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                videoFolder.deleteRecursively()
                videoFolder.delete()
            }
        }
    }

    private fun getFolderId(identifier: String) = identifier.hashCode().toString().replace("-", "M")
}