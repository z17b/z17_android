package cu.z17.views.videoPlayer


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MOVIE
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL
import androidx.media3.common.util.RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE
import androidx.media3.common.util.RepeatModeUtil.REPEAT_TOGGLE_MODE_ONE
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import cu.z17.views.utils.findActivity
import cu.z17.views.videoPlayer.controller.VideoPlayerControllerConfig
import cu.z17.views.videoPlayer.controller.applyToExoPlayerView
import cu.z17.views.videoPlayer.pip.enterPIPMode
import cu.z17.views.videoPlayer.trackSelector.FormatAudioPreferred
import cu.z17.views.videoPlayer.trackSelector.FormatSubsPreferred
import cu.z17.views.videoPlayer.trackSelector.FormatVideoResolution
import cu.z17.views.videoPlayer.trackSelector.TrackElement
import cu.z17.views.videoPlayer.trackSelector.TrackSelectorHelper
import cu.z17.views.videoPlayer.trackSelector.TripleTrackSave
import cu.z17.views.videoPlayer.uri.HLSMediaItem
import cu.z17.views.videoPlayer.util.setFullScreen
import kotlinx.coroutines.delay
import java.util.UUID


@SuppressLint("SourceLockedOrientationActivity", "UnsafeOptInUsageError")
@Composable
fun Z17HLSVideoPlayer(
    modifier: Modifier = Modifier,
    mediaItem: HLSMediaItem,
    handleLifecycle: Boolean = true,
    autoPlay: Boolean = true,
    usePlayerController: Boolean = true,
    controllerConfig: VideoPlayerControllerConfig = VideoPlayerControllerConfig.Default,
    seekBeforeMilliSeconds: Long = 10000L,
    seekAfterMilliSeconds: Long = 10000L,
    repeatMode: RepeatMode = RepeatMode.NONE,
    @FloatRange(from = 0.0, to = 1.0) volume: Float = 1f,
    onCurrentTimeChanged: (Long) -> Unit = {},
    enablePip: Boolean = true,
    enablePipWhenBackPressed: Boolean = true,
    handleAudioFocus: Boolean = true,
    playerInstance: ExoPlayer.() -> Unit = {},
    playerState: PlayerState,
    updatePlayerState: (PlayerState, Player) -> Unit = { _, _ -> },
    onRotate: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current

    val configuration = LocalConfiguration.current

    var currentTime by remember { mutableLongStateOf(0L) }

    var mediaSession = remember<MediaSession?> { null }

    val bandwidthMeter = remember {
        DefaultBandwidthMeter.getSingletonInstance(context)
    }

    val dataSourceFactory: DataSource.Factory = remember {
        DefaultDataSource.Factory(context)
    }

    val timer = remember {
        object : CountDownTimer(4000, 4000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                val currentActivity = context.findActivity()
                currentActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }
        }
    }

    /*val keyString2 = "  clearKeys: {" +
            "    'f3c5e0361e6654b28f8049c778b23946': 'a4631a153a443df9eed0593043db7519'," +
            "    'abba271e8bcf552bbd2e86a434a9a5d9': '69eaa802a6763af979e8d1940fb88392'," +
            "    '6d76f25cb17f5e16b8eaef6bbf582d8e': 'cb541084c99731aef4fff74500c12ead'" +
            "  }"
    val local =
        LocalMediaDrmCallback(keyString2.toByteArray())

    DefaultDrmSessionManager.Builder()
        .build(local)*/

    /*val keyString =
        "{\"keys\":[{\"kty\":\"oct\",\"k\":\"MGQ2NzEyYmYyYTg0ZWRjYzkzZDAwMWE5NjEzZjZmZWM\",\"kid\":\"Y2ZiNWUyYjczYmVmNGYzYzg3OGYyNWFiODZhNzQ1MWY\"}],'type':\"temporary\"}"

    val drmCallback: MediaDrmCallback = LocalMediaDrmCallback(keyString.toByteArray())
*/

    val videoSource: HlsMediaSource = remember {
        HlsMediaSource.Factory(dataSourceFactory)
            /*.setDrmSessionManagerProvider {
                DefaultDrmSessionManager.Builder()
                    .setUuidAndExoMediaDrmProvider(
                        C.WIDEVINE_UUID,
                        FrameworkMediaDrm.DEFAULT_PROVIDER
                    )
                    .build(drmCallback)
            }*/
            .createMediaSource(MediaItem.fromUri(Uri.parse(mediaItem.url)))
    }

    val videoTrackSelectionFactory = remember {
        AdaptiveTrackSelection.Factory()
    }

    val trackSelector = remember {
        DefaultTrackSelector(
            context,
            videoTrackSelectionFactory
        )
    }

    var trackSelectorHelper by remember {
        mutableStateOf<TrackSelectorHelper?>(null)
    }

    val player = remember {
        ExoPlayer.Builder(context)
            .setBandwidthMeter(bandwidthMeter)
            .setTrackSelector(trackSelector)
            .setSeekBackIncrementMs(seekBeforeMilliSeconds)
            .setSeekForwardIncrementMs(seekAfterMilliSeconds)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                handleAudioFocus,
            )
            .build()
            .also(playerInstance)
    }

    val defaultPlayerView = remember {
        PlayerView(context)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)

            if (currentTime != player.currentPosition) {
                onCurrentTimeChanged(currentTime)
            }

            currentTime = player.currentPosition
        }
    }

    LaunchedEffect(usePlayerController) {
        defaultPlayerView.useController = usePlayerController
    }

    LaunchedEffect(player, playerState) {
        defaultPlayerView.player = player

        player
            .addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)

                    if (playbackState == ExoPlayer.STATE_READY && trackSelectorHelper == null) {
                        trackSelectorHelper = TrackSelectorHelper(context, trackSelector)
                        trackSelectorHelper?.loadTrackData()
                    }
                }

                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)

                    val selectedVideoTrack =
                        playerState.selectedVideoTrack
                            ?: TrackElement(
                                trackType = C.TRACK_TYPE_VIDEO,
                                index = 0,
                                trackItem = 0,
                                trackStringData = "${trackSelector.parameters.maxVideoWidth} × ${trackSelector.parameters.maxVideoHeight} ${trackSelector.parameters.maxVideoBitrate} Mbps",
                                trackFormatData = FormatVideoResolution(
                                    width = trackSelector.parameters.maxVideoWidth,
                                    height = trackSelector.parameters.maxVideoHeight
                                )
                            )

                    val selectedAudioTrack =
                        if (playerState.selectedAudioTrack?.trackStringData == trackSelector.parameters.preferredAudioLanguages.firstOrNull()) playerState.selectedAudioTrack
                        else if (trackSelectorHelper?.tripleTrackSave?.audioTracks?.firstOrNull() != null)
                            TrackElement(
                                trackType = C.TRACK_TYPE_AUDIO,
                                index = 0,
                                trackItem = 0,
                                trackStringData = trackSelectorHelper!!.tripleTrackSave.audioTracks.first().trackStringData,
                                trackFormatData = FormatAudioPreferred(
                                    audio = trackSelectorHelper!!.tripleTrackSave.audioTracks.first().trackStringData
                                )
                            )
                        else null

                    val selectedSubsTrack =
                        if (playerState.selectedSubsTrack?.trackStringData == trackSelector.parameters.preferredTextLanguages.firstOrNull()) playerState.selectedSubsTrack
                        else if (trackSelectorHelper?.tripleTrackSave?.subsTracks?.firstOrNull() != null)
                            TrackElement(
                                trackType = C.TRACK_TYPE_TEXT,
                                index = 0,
                                trackItem = 0,
                                trackStringData = trackSelectorHelper!!.tripleTrackSave.subsTracks.first().trackStringData,
                                trackFormatData = FormatSubsPreferred(
                                    language = trackSelectorHelper!!.tripleTrackSave.subsTracks.first().trackStringData
                                )
                            )
                        else null

                    var newPlayerState = playerState.copy(
                        isPlaying = player.isPlaying,
                        isLoading = player.isLoading,
                        playbackState = player.playbackState,
                        bufferedPercentage = player.bufferedPercentage,
                        currentPosition = player.currentPosition.coerceAtLeast(0L),
                        totalDuration = player.duration.coerceAtLeast(0L),
                        tripleTrackSave = trackSelectorHelper?.loadTrackData()
                            ?: TripleTrackSave(),
                        selectedVideoTrack = selectedVideoTrack
                    )

                    if (selectedAudioTrack != null)
                        newPlayerState = newPlayerState.copy(
                            selectedAudioTrack = selectedAudioTrack
                        )

                    if (selectedSubsTrack != null)
                        newPlayerState = newPlayerState.copy(
                            selectedSubsTrack = selectedSubsTrack
                        )

                    updatePlayerState(
                        newPlayerState,
                        player
                    )
                }
            })
    }

    LaunchedEffect(mediaItem, player) {
        mediaSession?.release()
        mediaSession = MediaSession.Builder(context, ForwardingPlayer(player))
            .setId(
                "VideoPlayerMediaSession_${
                    UUID.randomUUID().toString().lowercase().split("-").first()
                }"
            )
            .build()

        player.setMediaSource(videoSource)

        player.prepare()

        player.playWhenReady = autoPlay
    }

    /*LaunchedEffect(controllerConfig) {
        controllerConfig.applyToExoPlayerView(defaultPlayerView) {
            updatePlayerState(
                playerState.copy(isFullscreen = it),
                player
            )

            if (it) {
                onFullScreenEnter()
            }
        }
    }*/

    LaunchedEffect(controllerConfig, repeatMode) {
        defaultPlayerView.setRepeatToggleModes(
            if (controllerConfig.showRepeatModeButton) {
                REPEAT_TOGGLE_MODE_ALL or REPEAT_TOGGLE_MODE_ONE
            } else {
                REPEAT_TOGGLE_MODE_NONE
            },
        )
        player.repeatMode = repeatMode.toExoPlayerRepeatMode()
    }

    LaunchedEffect(volume) {
        player.volume = volume
    }

    LaunchedEffect(
        playerState.selectedVideoTrack,
        playerState.selectedAudioTrack,
        playerState.selectedSubsTrack
    ) {
        playerState.selectedVideoTrack?.let {
            trackSelectorHelper?.selectTrack(it)
        }
        playerState.selectedAudioTrack?.let {
            trackSelectorHelper?.selectTrack(it)
        }
        playerState.selectedSubsTrack?.let {
            trackSelectorHelper?.selectTrack(it)
        }
    }

    LaunchedEffect(configuration.orientation) {
        onRotate(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    LaunchedEffect(playerState.isFullscreen) {
        val currentActivity = context.findActivity()
        currentActivity.setFullScreen(playerState.isFullscreen)
        currentActivity.requestedOrientation =
            if (playerState.isFullscreen) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR

        if (playerState.isFullscreen) timer.start()
    }

    VideoPlayerSurface(
        modifier = modifier.fillMaxWidth(),
        defaultPlayerView = defaultPlayerView,
        player = player,
        autoPlay = autoPlay,
        usePlayerController = usePlayerController,
        handleLifecycle = handleLifecycle,
        enablePip = enablePip,
    )

    BackHandler(enablePip && enablePipWhenBackPressed) {
        enterPIPMode(context, defaultPlayerView)
        player.play()
    }
}