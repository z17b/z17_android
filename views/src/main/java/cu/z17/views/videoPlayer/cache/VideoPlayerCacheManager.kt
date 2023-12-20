/*
 * Copyright 2023 Dora Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cu.z17.views.videoPlayer.cache

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import cu.z17.singledi.SingletonInitializer
import java.io.File

/**
 * Manage video player cache.
 */
@OptIn(UnstableApi::class)
class VideoPlayerCacheManager(context: Context) {
    companion object : SingletonInitializer<VideoPlayerCacheManager>() {
        private const val CACHE_SIZE = 50 * 1024 * 1024L
    }

    var cacheInstance: Cache?

    init {
        cacheInstance = try {
            SimpleCache(
                File(context.cacheDir, "video"),
                LeastRecentlyUsedCacheEvictor(CACHE_SIZE),
                StandaloneDatabaseProvider(context),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
