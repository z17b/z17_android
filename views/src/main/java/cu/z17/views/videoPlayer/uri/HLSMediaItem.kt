package cu.z17.views.videoPlayer.uri

import androidx.media3.common.MediaItem.DrmConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes

data class HLSMediaItem(
    val url: String,
    val mediaMetadata: MediaMetadata = MediaMetadata.EMPTY,
    val mimeType: String = MimeTypes.APPLICATION_M3U8,
    val drmConfiguration: DrmConfiguration? = null,
    val itemUniqueId: String = ""
)
