package cu.z17.views.videosAlbum.data

import android.graphics.Bitmap
import android.net.Uri

data class VideoAlbum(
    val uri: Uri,
    internal val dateTaken: Long?,
    val displayName: String?,
    internal val id: Long?,
    internal val folderName: String?,
    val duration: Long?,
    val thumbnail: Bitmap? = null,
)
