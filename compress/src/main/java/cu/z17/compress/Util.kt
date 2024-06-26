package cu.z17.compress

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream


private val separator = File.separator

private fun cachePath(context: Context) = "${context.cacheDir.path}${separator}compressor$separator"

fun String.compressFormat() = when (this) {
    "png" -> Bitmap.CompressFormat.PNG
    "webp" -> if(android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP
    else -> Bitmap.CompressFormat.JPEG
}

fun File.compressFormat() = when (extension.lowercase()) {
    "png" -> Bitmap.CompressFormat.PNG
    "webp" -> if(android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP
    else -> Bitmap.CompressFormat.JPEG
}

fun Bitmap.CompressFormat.extension() = when (this) {
    Bitmap.CompressFormat.PNG -> "png"
    if(android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP -> "webp"
    Bitmap.CompressFormat.WEBP -> "webp"
    else -> "jpeg"
}

fun loadBitmap(imageFile: File) = BitmapFactory.decodeFile(imageFile.absolutePath).run {
    determineImageRotation(imageFile, this)
}

fun decodeSampledBitmapFromFile(imageFile: File, reqWidth: Int, reqHeight: Int): Bitmap {
    return BitmapFactory.Options().run {
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, this)

        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

        inJustDecodeBounds = false
        BitmapFactory.decodeFile(imageFile.absolutePath, this)
    }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

fun determineImageRotation(imageFile: File, bitmap: Bitmap, maxHeight: Int = 3000, maxWidth: Int = 3000): Bitmap {
    val exif = ExifInterface(imageFile.absolutePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
    val matrix = Matrix()
    when (orientation) {
        6 -> matrix.postRotate(90f)
        3 -> matrix.postRotate(180f)
        8 -> matrix.postRotate(270f)
    }

    var needCompression = false
    val finalHeight = if (maxHeight > bitmap.height) bitmap.height else {
        needCompression = true
        1920
    }
    val finalWidth = if (maxWidth > bitmap.width) bitmap.width else {
        needCompression = true
        1080
    }

    if (needCompression) {
        val finalBitmap = decodeSampledBitmapFromFile(
            imageFile = imageFile,
            reqWidth = finalWidth,
            reqHeight = finalHeight
        )
        return finalBitmap
    }
    else return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

internal fun copyToCache(context: Context, imageFile: File): File {
    return imageFile.copyTo(File("${cachePath(context)}${imageFile.name}"), true)
}

fun overWrite(
    imageFile: File,
    bitmap: Bitmap,
    format: Bitmap.CompressFormat = imageFile.compressFormat(),
    quality: Int = 100,
): File {
    val result = if (format == imageFile.compressFormat()) {
        imageFile
    } else {
        File("${imageFile.absolutePath.substringBeforeLast(".")}.${format.extension()}")
    }
    imageFile.delete()
    saveBitmap(bitmap, result, format, quality)

    return result
}

fun saveBitmap(
    bitmap: Bitmap,
    destination: File,
    format: Bitmap.CompressFormat = destination.compressFormat(),
    quality: Int = 100,
) {
    destination.parentFile?.mkdirs()
    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(destination.absolutePath)
        bitmap.compress(format, quality, fileOutputStream)
    } finally {
        fileOutputStream?.run {
            flush()
            close()
        }
    }
}