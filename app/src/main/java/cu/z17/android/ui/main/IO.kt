package cu.z17.android.ui.main

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Scanner

object IO {
    /**
     * Save input stream to local private file.
     *
     * @param context
     * @param name
     * @param stream  - the input stream
     * @return True if success, false otherwise.
     */
    fun save(context: Context, name: String?, stream: InputStream?): Boolean {
        var file: FileOutputStream? = null
        if (stream == null) return false
        file = try {
            context.openFileOutput(name, Context.MODE_PRIVATE)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        }
        return save(stream, file)
    }

    /**
     * Save input stream to a file on external storage like sd card on device
     * memory. By external we mean storage which is not private to the app.
     *
     * @param context
     * @param name
     * @param stream  - the input stream
     * @return True if success, false otherwise.
     */
    fun saveExternal(context: Context, name: String, stream: InputStream?): Boolean {
        var file: FileOutputStream? = null
        if (stream == null) return false
        file = try {
            val downloads = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            FileOutputStream(File(downloads, name))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        }
        return save(stream, file)
    }

    /**
     * Save input stream to file.
     *
     * @param stream  - the input stream
     * @param file    - the output file stream
     * @return True if success, false otherwise.
     */
    fun save(stream: InputStream?, file: FileOutputStream?): Boolean {
        try {
            if (stream == null) throw Exception()
            var l: Int
            val buffer = ByteArray(1024 * 32)
            while (stream.read(buffer).also { l = it } != -1) file!!.write(buffer, 0, l)
            file!!.flush()
            stream.close()
            file.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /**
     * Get the full path in form of a URI of the internal app storage.
     * Useful for intents.
     *
     * @param context
     * @param name
     * @return
     */
    fun getPath(context: Context, name: String?): Uri {
        return Uri.fromFile(context.getFileStreamPath(name))
    }

    /**
     * Get the full path in form of a URI of the external storage like sd card
     * or device memory. By external we mean storage which is not private to the
     * app. Useful for intents.
     *
     * @param context
     * @param name
     * @return
     */
    fun getExternalPath(context: Context, name: String?): Uri {
        val downloads = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        return Uri.fromFile(File(downloads, name))
    }
    /**
     * Read a stream and return a string.
     *
     * @param is
     * @param charsetName
     * @return
     */
    /**
     * Read a stream and return a string. Uses UTF-8 encoding.
     *
     * @param is
     * @return
     */
    @JvmOverloads
    fun readStream(`is`: InputStream?, charsetName: String? = "UTF-8"): String {
        return if (`is` == null) "" else try {
            val scanner = Scanner(`is`, charsetName)
            scanner.useDelimiter("\\A").next().trim { it <= ' ' }
        } catch (e: NoSuchElementException) {
            ""
        }
    }

    /**
     * @param sourceLocation
     * @param targetLocation
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Throws(FileNotFoundException::class, IOException::class)
    fun copyFile(sourceLocation: File?, targetLocation: File) {
        if (!targetLocation.exists()) targetLocation.createNewFile()
        val `in`: InputStream = FileInputStream(sourceLocation)
        val out: OutputStream = FileOutputStream(targetLocation)

        // Copy the bits from instream to outstream
        val buf = ByteArray(1024)
        var len: Int
        while (`in`.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        `in`.close()
        out.close()
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun copyFile(sourceLocation: File?, out: OutputStream) {
        val `in`: InputStream = FileInputStream(sourceLocation)

        // Copy the bits from instream to outstream
        val buf = ByteArray(1024)
        var len: Int
        while (`in`.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        `in`.close()
        out.close()
    }
}