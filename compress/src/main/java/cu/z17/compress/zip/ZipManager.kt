package cu.z17.compress.zip

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class ZipManager {
    suspend fun decompressZipFile(zipFile: File, destinationPath: String) = try {
        withContext(Dispatchers.IO) {
            val bufferSize = 4096
            val buffer = ByteArray(bufferSize)

            FileInputStream(zipFile).use { fis ->
                ZipInputStream(fis).use { zis ->
                    var zipEntry: ZipEntry? = zis.nextEntry

                    while (zipEntry != null) {
                        val fileName = zipEntry.name
                        val file = File(destinationPath, fileName)

                        if (fileName.startsWith(".") || fileName.startsWith("__")) continue

                        if (zipEntry.isDirectory) {
                            file.mkdirs()
                        } else {
                            val parentFile = file.parentFile
                            parentFile?.mkdirs()

                            FileOutputStream(file).use { fos ->
                                while (true) {
                                    val read = zis.read(buffer)
                                    if (read == -1) break
                                    fos.write(buffer, 0, read)
                                }
                            }
                        }

                        zipEntry = zis.nextEntry
                    }
                }
            }
            true // Return true if the decompression was successful
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false // Return false if an exception occurred
    }
}