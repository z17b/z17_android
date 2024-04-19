package cu.z17.views.videoPlayer


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import cu.z17.views.utils.findActivity
import cu.z17.views.videoPlayer.controller.VideoPlayerControllerConfig
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
import java.io.File
import java.util.UUID


@SuppressLint("SourceLockedOrientationActivity", "UnsafeOptInUsageError")
@Composable
fun Z17HLSVideoPlayer(
    modifier: Modifier = Modifier,
    mediaItem: HLSMediaItem,
    handleLifecycle: Boolean = true,
    autoPlay: Boolean = true,
    autoRotation: Boolean = false,
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
    pipScale: Pair<Int, Int> = 16 to 9,
) {
    val context = LocalContext.current

    val configuration = LocalConfiguration.current

    var currentTime by remember { mutableLongStateOf(0L) }

    var mediaSession = remember<MediaSession?> { null }

    val DOWNLOAD_CONTENT_DIRECTORY = "stream_videos_cache"
    val downloadContentDirectory = remember {
        File(context.getExternalFilesDir(null), DOWNLOAD_CONTENT_DIRECTORY)
    }
    val downloadCache = remember {
        SimpleCache(downloadContentDirectory, NoOpCacheEvictor(), StandaloneDatabaseProvider(context))
    }

    val cacheSink = remember{
        CacheDataSink.Factory()
            .setCache(downloadCache)
    }

    val bandwidthMeter = remember {
        DefaultBandwidthMeter.getSingletonInstance(context)
    }

    val dataSourceFactory: DataSource.Factory = remember {
        DefaultDataSource.Factory(context)
    }

    val videoTrackSelectionFactory = remember {
        AdaptiveTrackSelection.Factory()
    }

    val downStreamFactory = FileDataSource.Factory()

    val cacheDataSourceFactory  =
        CacheDataSource.Factory()
            .setCache(downloadCache)
            .setCacheWriteDataSinkFactory(cacheSink)
            .setCacheReadDataSourceFactory(downStreamFactory)
            .setUpstreamDataSourceFactory(dataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    val videoSource: HlsMediaSource = remember {
        HlsMediaSource.Factory(cacheDataSourceFactory)
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
                onCurrentTimeChanged(currentTime + 1)
            }

            currentTime = player.currentPosition
        }
    }

    LaunchedEffect(usePlayerController) {
        defaultPlayerView.useController = usePlayerController
    }

    LaunchedEffect(player) {
        defaultPlayerView.player = player
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
                        else {
                            val track = trackSelectorHelper?.tripleTrackSave?.audioTracks?.firstOrNull()

                            if (track != null)
                                TrackElement(
                                    trackType = C.TRACK_TYPE_AUDIO,
                                    index = 0,
                                    trackItem = 0,
                                    trackStringData = track.trackStringData,
                                    trackFormatData = FormatAudioPreferred(
                                        audio = track.trackStringData
                                    )
                                )
                            else null
                        }

                    val selectedSubsTrack =
                        if (playerState.selectedSubsTrack?.trackStringData == trackSelector.parameters.preferredTextLanguages.firstOrNull()) playerState.selectedSubsTrack
                        else {
                            val track = trackSelectorHelper?.tripleTrackSave?.subsTracks?.firstOrNull()
                            if (track != null)
                                TrackElement(
                                    trackType = C.TRACK_TYPE_TEXT,
                                    index = 0,
                                    trackItem = 0,
                                    trackStringData = track.trackStringData,
                                    trackFormatData = FormatSubsPreferred(
                                        language = track.trackStringData
                                    )
                                )
                            else null
                        }

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

    if (autoRotation)
        LaunchedEffect(playerState.isFullscreen) {
            val currentActivity = context.findActivity()
            currentActivity.setFullScreen(playerState.isFullscreen)
            currentActivity.requestedOrientation =
                if (playerState.isFullscreen) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR

            if (playerState.isFullscreen) timer.start()
        }

    DisposableEffect(Unit) {
        val previewsConfiguration = configuration.orientation
        onDispose {
            try {
                val currentActivity = context.findActivity()
                currentActivity.requestedOrientation = previewsConfiguration
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    VideoPlayerSurface(
        modifier = modifier.fillMaxWidth(),
        defaultPlayerView = defaultPlayerView,
        player = player,
        autoPlay = autoPlay,
        usePlayerController = usePlayerController,
        handleLifecycle = handleLifecycle,
        enablePip = enablePip,
        pipScale = pipScale
    )

    BackHandler(enablePip && enablePipWhenBackPressed) {
        if (playerState.isPlaying) {
            enterPIPMode(context, defaultPlayerView, pipScale) {
                updatePlayerState(playerState.copy(isPIP = true), player)
            }
            player.play()
        }
    }
}