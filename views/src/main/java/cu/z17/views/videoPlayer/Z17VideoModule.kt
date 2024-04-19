package cu.z17.views.videoPlayer

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import cu.z17.singledi.SingletonInitializer
import java.io.File

class Z17VideoModule(private val context: Context) {
    companion object : SingletonInitializer<Z17VideoModule>() {
        private const val DOWNLOAD_CONTENT_DIRECTORY = "stream_videos_cache"
    }

    private val downloadContentDirectory =
        File(context.getExternalFilesDir(null), DOWNLOAD_CONTENT_DIRECTORY)

    @UnstableApi
    val downloadCache = SimpleCache(
        downloadContentDirectory,
        NoOpCacheEvictor(),
        StandaloneDatabaseProvider(context)
    )
}