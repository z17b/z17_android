package cu.z17.views.videoPlayer.trackSelector

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.MappingTrackSelector.MappedTrackInfo
import androidx.media3.ui.DefaultTrackNameProvider


@OptIn(UnstableApi::class)
class TrackSelectorHelper(
    private val context: Context,
    private val trackSelector: DefaultTrackSelector,
) {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    private var _tripleTrackSave = TripleTrackSave()
    val tripleTrackSave = _tripleTrackSave

    fun loadTrackData(): TripleTrackSave {

        _tripleTrackSave = TripleTrackSave()

        val mappedTrackInfo: MappedTrackInfo =
            Assertions.checkNotNull(trackSelector.currentMappedTrackInfo)

        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            val trackType = mappedTrackInfo.getRendererType(rendererIndex)
            val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)

            Log.d(
                TAG,
                "------------------------------------------------------Track item $rendererIndex------------------------------------------------------"
            )
            Log.d(TAG, "track type: " + trackTypeToName(trackType))
            Log.d(TAG, "track group array: $trackGroupArray")

            for (groupIndex in 0 until trackGroupArray.length) {
                for (trackIndex in 0 until trackGroupArray[groupIndex].length) {
                    val trackStringData = DefaultTrackNameProvider(context.resources).getTrackName(
                        trackGroupArray[groupIndex].getFormat(trackIndex)
                    )

                    Log.d(
                        TAG,
                        "track item $groupIndex: trackName: $trackStringData"
                    )

                    when (trackType) {
                        C.TRACK_TYPE_VIDEO -> {
                            val formatVideoResolution = try {
                                FormatVideoResolution(
                                    width = trackStringData.split(",")[0].split(" × ")[0].toIntOrNull()
                                        ?: 100,
                                    height = trackStringData.split(",")[0].split(" × ")[1].toIntOrNull()
                                        ?: 100
                                )
                            } catch (e: Exception) {
                                null
                            }

                            _tripleTrackSave = _tripleTrackSave.addVideoTrack(
                                TrackElement(
                                    trackType = C.TRACK_TYPE_VIDEO,
                                    index = trackIndex,
                                    trackItem = groupIndex,
                                    trackStringData = trackStringData,
                                    trackFormatData = formatVideoResolution
                                )
                            )
                        }

                        C.TRACK_TYPE_AUDIO -> _tripleTrackSave = _tripleTrackSave.addAudioTrack(
                            TrackElement(
                                trackType = C.TRACK_TYPE_AUDIO,
                                index = trackIndex,
                                trackItem = groupIndex,
                                trackStringData = trackStringData,
                                trackFormatData = FormatAudioPreferred(audio = trackStringData)
                            )
                        )

                        C.TRACK_TYPE_TEXT -> _tripleTrackSave = _tripleTrackSave.addSubsTrack(
                            TrackElement(
                                trackType = C.TRACK_TYPE_TEXT,
                                index = trackIndex,
                                trackItem = groupIndex,
                                trackStringData = trackStringData,
                                trackFormatData = FormatSubsPreferred(language = trackStringData)
                            )
                        )
                    }
                }
            }
        }

        return this._tripleTrackSave
    }

    private fun trackTypeToName(trackType: Int): String {
        return when (trackType) {
            C.TRACK_TYPE_VIDEO -> "TRACK_TYPE_VIDEO"
            C.TRACK_TYPE_AUDIO -> "TRACK_TYPE_AUDIO"
            C.TRACK_TYPE_TEXT -> "TRACK_TYPE_TEXT"
            else -> "Invalid track type"
        }
    }

    fun selectTrack(trackElement: TrackElement) {
        val parameters = when (trackElement.trackType) {
            C.TRACK_TYPE_VIDEO -> trackSelector.buildUponParameters().setMaxVideoSize(
                (trackElement.trackFormatData as FormatVideoResolution).width,
                (trackElement.trackFormatData).height
            )

            C.TRACK_TYPE_AUDIO -> {
                trackSelector.buildUponParameters().setPreferredAudioLanguage(
                    (trackElement.trackFormatData as FormatAudioPreferred).audio
                )
            }

            C.TRACK_TYPE_TEXT -> trackSelector.buildUponParameters().setPreferredTextLanguage(
                (trackElement.trackFormatData as FormatSubsPreferred).language
            )

            else -> trackSelector.buildUponParameters()
        }

        trackSelector.setParameters(parameters)
    }
}