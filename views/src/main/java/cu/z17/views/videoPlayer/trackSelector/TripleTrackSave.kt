package cu.z17.views.videoPlayer.trackSelector

import androidx.compose.runtime.Immutable

@Immutable
data class TripleTrackSave(
    val videoTracks: List<TrackElement> = emptyList(),
    val audioTracks: List<TrackElement> = emptyList(),
    val subsTracks: List<TrackElement> = emptyList(),
) {
    fun addVideoTrack(trackElement: TrackElement): TripleTrackSave {
        return this.copy(
            videoTracks = ArrayList<TrackElement>().apply {
                this.addAll(videoTracks)
                this.add(trackElement)
            }
        )
    }

    fun addAudioTrack(trackElement: TrackElement): TripleTrackSave {
        return this.copy(
            audioTracks = ArrayList<TrackElement>().apply {
                this.addAll(audioTracks)
                this.add(trackElement)
            }
        )
    }

    fun addSubsTrack(trackElement: TrackElement): TripleTrackSave {
        return this.copy(
            subsTracks = ArrayList<TrackElement>().apply {
                this.addAll(subsTracks)
                this.add(trackElement)
            }
        )
    }
}
