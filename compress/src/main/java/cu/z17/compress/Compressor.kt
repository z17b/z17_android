package cu.z17.compress

import android.content.Context
import cu.z17.compress.constraint.Compression
import cu.z17.compress.constraint.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext


object Compressor {
    suspend fun compress(
        context: Context,
        imageFile: File,
        returnToDefault: Boolean = false,
        coroutineContext: CoroutineContext = Dispatchers.IO,
        compressionPatch: Compression.() -> Unit = { default() }
    ) = withContext(coroutineContext + Job()) {
        val compression = Compression().apply(compressionPatch)
        var imageInCache = copyToCache(context, imageFile)
        compression.constraints.forEach { constraint ->
            while (constraint.isSatisfied(imageInCache).not()) {
                imageInCache = constraint.satisfy(imageInCache)
            }
        }
        return@withContext if (!returnToDefault) imageInCache else {
            imageFile.delete()

            imageInCache.copyFileUsingStream(imageFile)

            imageInCache.delete()

            File(imageFile.path)
        }
    }

    suspend fun compressAndGetBitmap(
        context: Context,
        imageFile: File,
        coroutineContext: CoroutineContext = Dispatchers.IO,
        compressionPatch: Compression.() -> Unit = { default() }
    ) = withContext(coroutineContext + Job()) {
        val compression = Compression().apply(compressionPatch)
        var result = copyToCache(context, imageFile)
        compression.constraints.forEach { constraint ->
            while (constraint.isSatisfied(result).not()) {
                result = constraint.satisfy(result)
            }
        }

        val bitmap = loadBitmap(result)

        return@withContext bitmap
    }
}

@Throws(IOException::class)
fun File.copyFileUsingStream(dest: File): File {
    var `is`: InputStream? = null
    var os: OutputStream? = null
    try {
        `is` = FileInputStream(this)
        os = FileOutputStream(dest)
        val buffer = ByteArray(1024)
        var length: Int
        while ((`is`.read(buffer).also { length = it }) > 0) {
            os.write(buffer, 0, length)
        }
    } finally {
        `is`!!.close()
        os!!.close()
    }

    return dest
}