package cu.z17.views.picturesAlbum.data

import android.net.Uri

data class AlbumImage(
    val uri: Uri,
    internal val dateTaken: Long?,
    val displayName: String?,
    internal val id: Long?,
    internal val folderName: String?,
)
