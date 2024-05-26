package cu.z17.android.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.*
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.TimeUnit


object MediaUtil {
    const val IMAGE_PNG = "image/png"
    const val IMAGE_JPEG = "image/jpeg"
    const val IMAGE_GIF = "image/gif"
    const val AUDIO_AAC = "audio/aac"
    const val AUDIO_UNSPECIFIED = "audio/*"
    const val VIDEO_UNSPECIFIED = "cu/todus/android/adapter/message/delegate/video/*"

    const val ALL_IMAGE = "image/"
    const val ALL_AUDIO = "audio/"
    const val ALL_VIDEO = "video/"
    const val ALL_FILES = "application/"
    const val TEXT = "text/plain"

    private const val AUTHORITY = "com.ianhanniballake.localstorage.documents"
    private const val DEBUG = false

    fun generateImageThumbnail(bitmap: Bitmap, file: File, size: Float = 15F): Bitmap {

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
                size//cacontext.getResources().getDimensionPixelSize(R.dimen.media_bubble_height).toFloat()
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

    fun getImageWidthHeight(imageFile: String): Pair<Long, Long> {
        val rotate = getCameraPhotoOrientation(imageFile)
        val matrix = Matrix()
        matrix.postRotate(rotate.toFloat())

        val mOriginal = BitmapFactory.decodeFile(imageFile)

        val createBitmap =
            Bitmap.createBitmap(mOriginal, 0, 0, mOriginal.width, mOriginal.height, matrix, true)
        return createBitmap.width.toLong() to createBitmap.height.toLong()
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
            //Timber.e(e)
        }

        return rotate
    }

    fun getHumanDuration(duration: Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.SECONDS.toMinutes(duration),
            TimeUnit.SECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.SECONDS.toMinutes(
                    duration
                )
            )
        )
    }

    fun getMimeType(context: Context, uri: Uri): String {
        var type = context.contentResolver.getType(uri)
        if (type == null) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            type = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension.lowercase(Locale.ROOT))
        }
        return getCorrectedMimeType(type)
    }

    fun getMimeType(extension: String): String {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase(Locale.ROOT))
            ?: ""
    }

    private fun getCorrectedMimeType(mimeType: String?): String {
        return when (mimeType) {
            "image/jpg" -> if (MimeTypeMap.getSingleton().hasMimeType(IMAGE_JPEG)) IMAGE_JPEG
            else mimeType

            else -> mimeType ?: ""
        }
    }

    fun isImageType(contentType: String): Boolean {
        return contentType.startsWith("image/")
    }

    fun isVideoType(contentType: String): Boolean {
        return contentType.startsWith("video/")
    }

    fun isFileType(contentType: String): Boolean {
        return contentType.startsWith("*/")
    }

    fun isAudioType(contentType: String): Boolean {
        return contentType.startsWith("audio/")
    }

    fun isGifType(contentType: String): Boolean {
        return contentType.contains("image/gif")
    }

    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            when {
                DocumentsContract.isDocumentUri(context, uri) -> // LocalStorageProvider
                    when {
                        isLocalStorageDocument(uri) -> // The path is the id
                            return DocumentsContract.getDocumentId(uri)

                        isExternalStorageDocument(uri) || isDownloadsDocument(uri) || isMediaDocument(
                            uri
                        ) -> {
                            return getPathFromContent(context, uri)
                        }
                    }

                "content".equals(uri.scheme!!, ignoreCase = true) -> // Return the remote address
                    return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                        context, uri, null, null
                    )

                "file".equals(uri.scheme!!, ignoreCase = true) -> return uri.path
            }
            return getPathFromContent(context, uri)

        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            return getPathFromContent(context, uri)

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return uri.toString()
    }

    fun getPathFromContent(context: Context, uri: Uri): String? {

        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val mimeType = MimeUtils.guessMimeTypeFromUri(context, uri)
        val file = File(
            context.externalCacheDir,
            getFileNameByUri(context, uri)
                ?: "asdasd.txt"
        )
        return if (IO.save(inputStream, file.outputStream())) {
            file.path
        } else {
            null
        }
    }

    private fun getFileNameByUri(context: Context, uri: Uri): String? {
        var result: String? = null
        try {
            if (uri.scheme.equals("content")) {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor.use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        result = cursor.getString(columnIndex)
                    }
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result?.lastIndexOf('/')
                if (cut != null && cut != -1) {
                    result = result?.substring(cut.plus(1))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun isLocal(url: String?): Boolean {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://")
    }

    private fun isLocalStorageDocument(uri: Uri): Boolean {
        return AUTHORITY == uri.authority
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    @SuppressLint("Recycle")
    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?,
    ): String? {
        val column = "_data"
        val projection = arrayOf(column)

        context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)!!
            .use { cursor ->
                if (cursor.moveToFirst()) {
                    if (DEBUG) DatabaseUtils.dumpCursor(cursor)

                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            }
        return null
    }

    fun close(stream: Closeable?) {
        if (stream != null) {
            try {
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun getDurationFromFile(context: Context, path: File): Long {
        val uri = Uri.fromFile(path)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val millSecond = Integer.parseInt(durationStr ?: "0")
        return TimeUnit.MILLISECONDS.toSeconds(millSecond.toLong())
    }

    @Throws(Exception::class)
    private fun createChecksum(filename: String): String {
        val fis = FileInputStream(filename)

        val mGraphicBuffer = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        while (true) {
            val readNum = fis.read(buf)
            if (readNum == -1) break
            mGraphicBuffer.write(buf, 0, readNum)
        }
        fis.close()

        // Generate the checksum
        return generateChecksum(mGraphicBuffer)

    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    @Throws(Exception::class)
    fun getChecksum(filename: String): String {
        val b = createChecksum(filename)
        return b
    }

    private fun printableHexString(data: ByteArray): String {
        // Create Hex String
        val hexString: StringBuilder = StringBuilder()
        for (aMessageDigest: Byte in data) {
            var h: String = Integer.toHexString(0xFF and aMessageDigest.toInt())
            while (h.length < 2) h = "0$h"
            hexString.append(h)
        }
        return hexString.toString()
    }

    private fun generateChecksum(data: ByteArrayOutputStream): String {
        try {
            val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
            val hash: ByteArray = digest.digest(data.toByteArray())
            return printableHexString(hash)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun aspectRatio(oldWidth: Long, oldHeight: Long, newWidth: Long, newHeight: Long): Int {
        val viewWidthToBitmapWidthRatio = (newWidth.toDouble() / oldWidth.toDouble())
        return (oldHeight * viewWidthToBitmapWidthRatio).toInt()
    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }


}