package cu.z17.compress

import android.content.Context
import cu.z17.compress.constraint.Compression
import cu.z17.compress.constraint.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext

object Compressor {
    suspend fun compress(
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
        return@withContext result
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