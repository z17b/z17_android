package cu.z17.views.utils

import android.content.Context
import android.graphics.Bitmap
import cu.z17.compress.compressFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object ThumbnailGenerator {
    suspend fun generateVideoThumbnailInFile(videoPath: String, context: Context): File? {
        val dir = File(File(context.filesDir, "z17"), ".thumbnail")
        if (!dir.exists()) dir.mkdirs()

        val id = try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(videoPath.toByteArray())
            val number = BigInteger(1, messageDigest)
            val md5 = StringBuilder(number.toString(16))
            while (md5.length < 32) {
                md5.insert(0, "0")
            }
            md5.toString()
        } catch (e: NoSuchAlgorithmException) {
            videoPath
        }

        var possibleThumbnail: File? = File(dir, "thumbnail_$id.jpeg")

        if (possibleThumbnail?.exists() == true) return possibleThumbnail

        VideoUtils.getThumbnailOnTime(videoPath, 0L, 512F)?.let {
            saveBitmap(
                bitmap = it,
                destination = possibleThumbnail!!,
                onComplete = {},
                onError = {
                    possibleThumbnail = null
                }
            )
        }

        return possibleThumbnail
    }

    private suspend fun saveBitmap(
        bitmap: Bitmap,
        destination: File,
        format: Bitmap.CompressFormat = destination.compressFormat(),
        quality: Int = 100,
        onComplete: () -> Unit,
        onError: () -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            destination.parentFile?.mkdirs()
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(destination.absolutePath)
                bitmap.compress(format, quality, fileOutputStream)
            } catch (e: Exception) {
                onError()
            } finally {
                fileOutputStream?.run {
                    flush()
                    close()
                }

                onComplete()
            }
        }
    }

}