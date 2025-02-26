package cu.z17.views.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.annotation.OptIn
import androidx.exifinterface.media.ExifInterface
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(UnstableApi::class)
object VideoUtils {
    suspend fun getThumbnailOnTime(path: String, time: Long, size: Float = 256F): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(path)
                val myBitmap = mmr.getFrameAtTime(
                    time,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )

                val result = myBitmap ?: return@withContext null

                mmr.release()
                return@withContext generateImageThumbnail(result, File(path), size)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                return@withContext null
            }
        }
    }

    private fun generateImageThumbnail(bitmap: Bitmap, file: File, size: Float): Bitmap {
        val bitmapCompressed: Bitmap by lazy {

            var mOriginal = bitmap

            val rotate = getCameraPhotoOrientation(file.absolutePath)
            val matrix = Matrix()
            matrix.postRotate(rotate.toFloat())
            // Here you will get the image bitmap which has changed orientation
            mOriginal = Bitmap.createBitmap(
                mOriginal, 0, 0, mOriginal.width, mOriginal.height, matrix, true
            )

            var width = mOriginal.width.toFloat()
            var height = mOriginal.height.toFloat()
            var invert = false
            if (height > width) {
                val tmp = width
                width = height
                height = tmp
                invert = true
            }

            var newWidth =
                size
            var newHeight = height / width * newWidth

            if (newHeight < newWidth && invert) {
                val tmp = newWidth
                newWidth = newHeight
                newHeight = tmp
            }

            ThumbnailUtils.extractThumbnail(mOriginal, newWidth.toInt(), newHeight.toInt())
        }

        return bitmapCompressed

    }

    private fun getCameraPhotoOrientation(imageFile: String): Int {
        var rotate = 0
        try {
            val exif = ExifInterface(
                imageFile
            )
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rotate
    }

    @kotlin.OptIn(DelicateCoroutinesApi::class)
    fun saveVideo(
        source: Uri,
        context: Context,
        videoPathToSave: String,
        startMilliseconds: Long,
        endMilliseconds: Long,
    ) = callbackFlow {
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        fun close() {
            channel.close()
        }

        val transformer = Transformer.Builder(context)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    coroutineScope.launch {
                        try {
                            if (!channel.isClosedForSend) send(true)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        close()
                    }
                }

                override fun onError(
                    composition: Composition,
                    exportResult: ExportResult,
                    exportException: ExportException,
                ) {
                    coroutineScope.launch {
                        try {
                            if (!channel.isClosedForSend) send(false)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        close()
                    }
                }
            })
            .build()

        val inputMediaItem = MediaItem.Builder()
            .setUri(source)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(startMilliseconds)
                    .setEndPositionMs(endMilliseconds)
                    .build()
            )
            .build()

        /**
         * set effects
         */
        /**
         * set effects
         */
        val editedMediaItem = EditedMediaItem.Builder(inputMediaItem)
            .setFrameRate(30)
            .setEffects(
                Effects(
                    emptyList(),
                    listOf(
                        /*RgbAdjustment.Builder()
                            .build()*/
                    )
                )
            )
            .build()

        val composition =
            Composition.Builder(listOf(EditedMediaItemSequence(listOf(editedMediaItem))))
                .build()

        transformer.start(composition, videoPathToSave)

        awaitClose {
        }
    }
}