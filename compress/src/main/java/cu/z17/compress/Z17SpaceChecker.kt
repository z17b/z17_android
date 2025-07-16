package cu.z17.compress

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getExternalFilesDirs
import java.util.Locale
import kotlin.math.roundToLong

@RequiresApi(Build.VERSION_CODES.O)
class Z17SpaceChecker(private val context: Context) {
    private lateinit var mStorageManager: StorageManager
    private val mStorageVolumesByExtDir = mutableListOf<VolumeStats>()
    private var mKbToggleValue = true
    private var kbToUse = KB
    private var mbToUse = MB
    private var gbToUse = GB

    private fun getVolumeStats() {
        // We will get our volumes from the external files directory list. There will be one
        // entry per external volume.
        val extDirs = getExternalFilesDirs(context, null)

        mStorageVolumesByExtDir.clear()
        extDirs.forEach { file ->
            val storageVolume: StorageVolume? = mStorageManager.getStorageVolume(file)
            if (storageVolume == null) {
                Log.d(TAG, "Could not determinate StorageVolume for ${file.path}")
            } else {
                val totalSpace: Long
                val usedSpace: Long
                if (storageVolume.isPrimary) {
                    // Special processing for primary volume. "Total" should equal size advertised
                    // on retail packaging and we get that from StorageStatsManager. Total space
                    // from File will be lower than we want to show.
                    val uuid = StorageManager.UUID_DEFAULT
                    val storageStatsManager =
                        context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                    // Total space is reported in round numbers. For example, storage on a
                    // SamSung Galaxy S7 with 32GB is reported here as 32_000_000_000. If
                    // true GB is needed, then this number needs to be adjusted. The constant
                    // "KB" also need to be changed to reflect KiB (1024).
//                    totalSpace = storageStatsManager.getTotalBytes(uuid)
                    totalSpace = (storageStatsManager.getTotalBytes(uuid) / 1_000_000_000) * gbToUse
                    usedSpace = totalSpace - storageStatsManager.getFreeBytes(uuid)
                } else {
                    // StorageStatsManager doesn't work for volumes other than the primary volume
                    // since the "UUID" available for non-primary volumes is not acceptable to
                    // StorageStatsManager. We must revert to File for non-primary volumes. These
                    // figures are the same as returned by statvfs().
                    totalSpace = file.totalSpace
                    usedSpace = totalSpace - file.freeSpace
                }
                mStorageVolumesByExtDir.add(
                    VolumeStats(storageVolume, totalSpace, usedSpace)
                )
            }
        }
    }

    private fun showVolumeStats() {
        val sb = StringBuilder()
        mStorageVolumesByExtDir.forEach { volumeStats ->
            val (usedToShift, usedSizeUnits) = getShiftUnits(volumeStats.mUsedSpace)
            val usedSpace = (100f * volumeStats.mUsedSpace / usedToShift).roundToLong() / 100f
            val (totalToShift, totalSizeUnits) = getShiftUnits(volumeStats.mTotalSpace)
            val totalSpace = (100f * volumeStats.mTotalSpace / totalToShift).roundToLong() / 100f
            val uuidToDisplay: String?
            val volumeDescription =
                if (volumeStats.mStorageVolume.isPrimary) {
                    uuidToDisplay = ""
                    PRIMARY_STORAGE_LABEL
                } else {
                    uuidToDisplay = " (${volumeStats.mStorageVolume.uuid})"
                    volumeStats.mStorageVolume.getDescription(context)
                }
            sb
        }
    }

    private fun getShiftUnits(x: Long): Pair<Long, String> {
        val usedSpaceUnits: String
        val shift =
            when {
                x < kbToUse -> {
                    usedSpaceUnits = "Bytes"; 1L
                }

                x < mbToUse -> {
                    usedSpaceUnits = "KB"; kbToUse
                }

                x < gbToUse -> {
                    usedSpaceUnits = "MB"; mbToUse
                }

                else -> {
                    usedSpaceUnits = "GB"; gbToUse
                }
            }
        return Pair(shift, usedSpaceUnits)
    }

    private fun selectKbValue() {
        if (mKbToggleValue) {
            kbToUse = KB
            mbToUse = MB
            gbToUse = GB
        } else {
            kbToUse = KiB
            mbToUse = MiB
            gbToUse = GiB
        }
    }

    companion object {
        fun Float.nice(fieldLength: Int = 6): String =
            String.format(Locale.US, "%$fieldLength.2f", this)

        // StorageVolume should have an accessible "getPath()" method that will do
        // the following so we don't have to resort to reflection.
        @Suppress("unused")
        fun StorageVolume.getStorageVolumePath(): String {
            return try {
                javaClass
                    .getMethod("getPath")
                    .invoke(this) as String
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        // See https://en.wikipedia.org/wiki/Kibibyte for description
        // of these units.

        // These values seems to work for "Files by Google"...
        const val KB = 1_000L
        const val MB = KB * KB
        const val GB = KB * KB * KB

        // ... and these values seems to work for other file manager apps.
        const val KiB = 1_024L
        const val MiB = KiB * KiB
        const val GiB = KiB * KiB * KiB

        const val PRIMARY_STORAGE_LABEL = "Internal Storage"

        const val TAG = "MainActivity"
    }

    data class VolumeStats(
        val mStorageVolume: StorageVolume,
        var mTotalSpace: Long = 0,
        var mUsedSpace: Long = 0,
    )
}

class Z17SpaceCheckerOld(private val context: Context) {
}


