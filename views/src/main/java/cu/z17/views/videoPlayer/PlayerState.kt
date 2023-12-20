package cu.z17.views.videoPlayer

import androidx.media3.common.Player.STATE_IDLE
import cu.z17.views.videoPlayer.trackSelector.TrackElement
import cu.z17.views.videoPlayer.trackSelector.TripleTrackSave
import androidx.compose.runtime.Immutable

@Immutable
data class PlayerState(
    val isFullscreen: Boolean = false,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val isPIP: Boolean = false,
    val playbackState: Int = STATE_IDLE,
    val bufferedPercentage: Int = 0,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val tripleTrackSave: TripleTrackSave = TripleTrackSave(),
    val selectedVideoTrack: TrackElement? = null,
    val selectedAudioTrack: TrackElement? = null,
    val selectedSubsTrack: TrackElement? = null,
) {
    override fun toString(): String {
        return "val isFullscreen: Boolean = $isFullscreen,\n" +
                "    val isPlaying: Boolean = $isPlaying,\n" +
                "    val isLoading: Boolean = $isLoading,\n" +
                "    val playbackState: Int = $playbackState,\n" +
                "    val bufferedPercentage: Int = $bufferedPercentage,\n" +
                "    val currentPosition: Long = $currentPosition,\n" +
                "    val totalDuration: Long = $totalDuration,\n" /*+
                "    val tripleTrackSave: TripleTrackSave = $tripleTrackSave,\n" +
                "    val selectedVideoTrack: TrackElement? = $selectedVideoTrack,\n" +
                "    val selectedAudioTrack: TrackElement? = $selectedAudioTrack,\n" +
                "    val selectedSubsTrack: TrackElement? = $selectedSubsTrack"*/
    }
}