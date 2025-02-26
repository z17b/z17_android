package cu.z17.views.videosAlbum.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import cu.z17.views.videosAlbum.data.VideoAlbum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private val videoProjection = arrayOf(
    MediaStore.Video.Media._ID,
    MediaStore.Video.Media.DISPLAY_NAME,
    MediaStore.Video.Media.DATE_TAKEN,
    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
    MediaStore.Video.Media.DURATION
)

internal suspend fun Context.createVideoCursor(limit: Int, offset: Int): Cursor? {
    return withContext(Dispatchers.IO) {
        try {
            return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val bundle = bundleOf(
                    ContentResolver.QUERY_ARG_OFFSET to offset,
                    ContentResolver.QUERY_ARG_LIMIT to limit,
                    ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Video.Media.DATE_ADDED),
                    ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )
                contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoProjection,
                    bundle,
                    null
                )
            } else {
                contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoProjection,
                    null,
                    null,
                    "${MediaStore.Video.Media.DATE_ADDED} DESC LIMIT $limit OFFSET $offset",
                    null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}

internal suspend fun Context.fetchPageVideo(limit: Int, offset: Int): List<VideoAlbum> {
    try {
        val pictures = ArrayList<VideoAlbum>()
        val cursor = createVideoCursor(limit, offset)
        try {
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(videoProjection[0])
                val displayNameColumn = it.getColumnIndexOrThrow(videoProjection[1])
                val dateTakenColumn = it.getColumnIndexOrThrow(videoProjection[2])
                val bucketDisplayName = it.getColumnIndexOrThrow(videoProjection[3])
                val bucketDuration = it.getColumnIndexOrThrow(videoProjection[4])

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val dateTaken = it.getLong(dateTakenColumn)
                    val displayName = it.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val folderName = it.getString(bucketDisplayName)
                    var duration = it.getLong(bucketDuration)

                    if (duration == 0L) {
                        try {
                            val mmr = MediaMetadataRetriever()
                            mmr.setDataSource(this, contentUri)
                            val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            val millSecond = Integer.parseInt(durationStr)
                            duration = millSecond.toLong()
                            mmr.release()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    pictures.add(
                        VideoAlbum(
                            uri = contentUri,
                            dateTaken = dateTaken,
                            displayName = displayName,
                            id = id,
                            folderName = folderName.toString(),
                            duration = duration
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cursor?.close()
        return pictures
    } catch (e: Exception) {
        e.printStackTrace()
        return ArrayList()
    }
}