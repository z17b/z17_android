package cu.z17.views.videoPlayer.trackSelector

import androidx.compose.runtime.Immutable

@Immutable
data class TrackElement(
    val trackType: Int,
    val index: Int = 0,
    val trackItem: Int = 0,
    val trackStringData: String = "",
    val trackFormatData: FormatData?,
) {
    override fun toString(): String {
        return "trackType: $trackType, index: $index, trackItem: $trackItem, trackStringData: $trackStringData, trackFormatData: $trackFormatData"
    }
}

open class FormatData

data class FormatVideoResolution(
    val width: Int,
    val height: Int,
) : FormatData()

data class FormatAudioPreferred(
    val audio: String,
) : FormatData()

data class FormatSubsPreferred(
    val language: String,
) : FormatData()
