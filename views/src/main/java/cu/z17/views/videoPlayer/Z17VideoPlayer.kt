package cu.z17.views.videoPlayer


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.media3.ui.PlayerView
import cu.z17.views.utils.findActivity
import cu.z17.views.utils.observeAsState
import cu.z17.views.videoPlayer.cache.VideoPlayerCacheManager
import cu.z17.views.videoPlayer.controller.VideoPlayerControllerConfig
import cu.z17.views.videoPlayer.controller.applyToExoPlayerView
import cu.z17.views.videoPlayer.pip.enterPIPMode
import cu.z17.views.videoPlayer.pip.isActivityStatePipMode
import cu.z17.views.videoPlayer.trackSelector.FormatAudioPreferred
import cu.z17.views.videoPlayer.trackSelector.FormatSubsPreferred
import cu.z17.views.videoPlayer.trackSelector.FormatVideoResolution
import cu.z17.views.videoPlayer.trackSelector.TrackElement
import cu.z17.views.videoPlayer.trackSelector.TrackSelectorHelper
import cu.z17.views.videoPlayer.trackSelector.TripleTrackSave
import cu.z17.views.videoPlayer.uri.VideoPlayerMediaItem
import cu.z17.views.videoPlayer.uri.toUri
import cu.z17.views.videoPlayer.util.setFullScreen
import kotlinx.coroutines.delay
import java.util.*

/**
 * [VideoPlayer] is UI component that can play video in Jetpack Compose. It works based on ExoPlayer.
 * You can play local (e.g. asset files, resource files) files and all video files in the network environment.
 * For all video formats supported by the [VideoPlayer] component, see the ExoPlayer link below.
 *
 * If you rotate the screen, the default action is to reset the player state.
 * To prevent this happening, put the following options in the `android:configChanges` option of your app's AndroidManifest.xml to keep the settings.
 * ```
 * keyboard|keyboardHidden|orientation|screenSize|screenLayout|smallestScreenSize|uiMode
 * ```
 *
 * This component is linked with Compose [androidx.compose.runtime.DisposableEffect].
 * This means that it move out of the Composable Scope, the ExoPlayer instance will automatically be destroyed as well.
 *
 * @see <a href="https://exoplayer.dev/supported-formats.html">Exoplayer Support Formats</a>
 *
 * @param modifier Modifier to apply to this layout node.
 * @param mediaItems [VideoPlayerMediaItem] to be played by the video player. The reason for receiving media items as an array is to configure multi-track. If it's a single track, provide a single list (e.g. listOf(mediaItem)).
 * @param handleLifecycle Sets whether to automatically play/stop the player according to the activity lifecycle. Default is true.
 * @param autoPlay Autoplay when media item prepared. Default is true.
 * @param usePlayerController Using player controller. Default is true.
 * @param controllerConfig Player controller config. You can customize the Video Player Controller UI.
 * @param seekBeforeMilliSeconds The seek back increment, in milliseconds. Default is 10sec (10000ms). Read-only props (Changes in values do not take effect.)
 * @param seekAfterMilliSeconds The seek forward increment, in milliseconds. Default is 10sec (10000ms). Read-only props (Changes in values do not take effect.)
 * @param repeatMode Sets the content repeat mode.
 * @param volume Sets thie player volume. It's possible from 0.0 to 1.0.
 * @param onCurrentTimeChanged A callback that returned once every second for player current time when the player is playing.
 * @param fullScreenSecurePolicy Windows security settings to apply when full screen. Default is off. (For example, avoid screenshots that are not DRM-applied.)
 * @param onFullScreenEnter A callback that occurs when the player is full screen. (The [VideoPlayerControllerConfig.showFullScreenButton] must be true to trigger a callback.)
 * @param onFullScreenExit A callback that occurs when the full screen is turned off. (The [VideoPlayerControllerConfig.showFullScreenButton] must be true to trigger a callback.)
 * @param enablePip Enable PIP (Picture-in-Picture).
 * @param enablePipWhenBackPressed With [enablePip] is `true`, set whether to enable PIP mode even when you press Back. Default is false.
 * @param handleAudioFocus Set whether to handle the video playback control automatically when it is playing in PIP mode and media is played in another app. Default is true.
 * @param playerInstance Return exoplayer instance. This instance allows you to add [com.google.android.exoplayer2.analytics.AnalyticsListener] to receive various events from the player.
 */
@SuppressLint("SourceLockedOrientationActivity", "UnsafeOptInUsageError")
@Composable
fun Z17VideoPlayer(
    modifier: Modifier = Modifier,
    mediaItems: List<VideoPlayerMediaItem>,
    handleLifecycle: Boolean = true,
    autoPlay: Boolean = true,
    usePlayerController: Boolean = true,
    controllerConfig: VideoPlayerControllerConfig = VideoPlayerControllerConfig.Default,
    seekBeforeMilliSeconds: Long = 10000L,
    seekAfterMilliSeconds: Long = 10000L,
    repeatMode: RepeatMode = RepeatMode.NONE,
    @FloatRange(from = 0.0, to = 1.0) volume: Float = 1f,
    onCurrentTimeChanged: (Long) -> Unit = {},
    fullScreenSecurePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    onFullScreenEnter: () -> Unit = {},
    onFullScreenExit: () -> Unit = {},
    enablePip: Boolean = false,
    enablePipWhenBackPressed: Boolean = false,
    handleAudioFocus: Boolean = true,
    playerInstance: ExoPlayer.() -> Unit = {},
) {
    val context = LocalContext.current

    var currentTime by remember { mutableLongStateOf(0L) }

    val dataSourceFactory = remember {
        /*DefaultHttpDataSource.Factory().apply {
            setUserAgent(
                Util.getUserAgent(
                    context,
                    "Picta"
                )
            )
            val bandwidthMeter = DefaultBandwidthMeter.Builder(context)
                .setResetOnNetworkTypeChange(false)
                .build()
            setTransferListener(bandwidthMeter)
        }*/
        DefaultHttpDataSource.Factory()
    }

    var mediaSession = remember<MediaSession?> { null }

    val player = remember {
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(seekBeforeMilliSeconds)
            .setSeekForwardIncrementMs(seekAfterMilliSeconds)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                handleAudioFocus,
            )
            .apply {
                val cache =
                    VideoPlayerCacheManager.createInstance { VideoPlayerCacheManager(context) }.cacheInstance

                if (cache != null) {
                    val cacheDataSourceFactory = CacheDataSource.Factory()
                        .setCache(cache)
                        .setUpstreamDataSourceFactory(
                            DefaultDataSource.Factory(
                                context,
                                dataSourceFactory
                            )
                        )
                    setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
                }
            }
            .build()
            .also(playerInstance)
    }

    val defaultPlayerView = remember {
        PlayerView(context)
    }

    BackHandler(enablePip && enablePipWhenBackPressed) {
        enterPIPMode(context, defaultPlayerView)
        player.play()
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

    LaunchedEffect(player) {
        defaultPlayerView.player = player
    }

    LaunchedEffect(mediaItems, player) {
        mediaSession?.release()
        mediaSession = MediaSession.Builder(context, ForwardingPlayer(player))
            .setId(
                "VideoPlayerMediaSession_${
                    UUID.randomUUID().toString().lowercase().split("-").first()
                }"
            )
            .build()
        val exoPlayerMediaItems = mediaItems.map {
            val uri = it.toUri(context)

            MediaItem.Builder().apply {
                setUri(uri)
                setMediaMetadata(it.mediaMetadata)
                setMimeType(it.mimeType)
                setDrmConfiguration(
                    if (it is VideoPlayerMediaItem.NetworkMediaItem) {
                        it.drmConfiguration
                    } else {
                        null
                    },
                )
            }.build()
        }

        player.setMediaItems(exoPlayerMediaItems)
        player.prepare()

        if (autoPlay) {
            player.play()
        }
    }

    var isFullScreenModeEntered by remember { mutableStateOf(false) }

    LaunchedEffect(controllerConfig) {
        controllerConfig.applyToExoPlayerView(defaultPlayerView) {
            isFullScreenModeEntered = it

            if (it) {
                onFullScreenEnter()
            }
        }
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

    VideoPlayerSurface(
        defaultPlayerView = defaultPlayerView,
        player = player,
        autoPlay = autoPlay,
        usePlayerController = usePlayerController,
        handleLifecycle = handleLifecycle,
        enablePip = enablePip,
    )

    if (isFullScreenModeEntered) {
        var fullScreenPlayerView by remember { mutableStateOf<PlayerView?>(null) }

        VideoPlayerFullScreenDialog(
            player = player,
            autoPlay = autoPlay,
            currentPlayerView = defaultPlayerView,
            controllerConfig = controllerConfig,
            repeatMode = repeatMode,
            onDismissRequest = {
                fullScreenPlayerView?.let {
                    PlayerView.switchTargetView(player, it, defaultPlayerView)
                    defaultPlayerView.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_fullscreen)
                        .performClick()
                    val currentActivity = context.findActivity()
                    currentActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    currentActivity.setFullScreen(false)
                    onFullScreenExit()
                }

                isFullScreenModeEntered = false
            },
            securePolicy = fullScreenSecurePolicy,
            enablePip = enablePip,
            fullScreenPlayerView = {
                fullScreenPlayerView = this
            },
        )
    }
}

@SuppressLint("SourceLockedOrientationActivity", "UnsafeOptInUsageError")
@Composable
fun Z17VideoPlayer2(
    modifier: Modifier = Modifier,
    mediaItems: List<VideoPlayerMediaItem>,
    handleLifecycle: Boolean = true,
    autoPlay: Boolean = true,
    usePlayerController: Boolean = true,
    controllerConfig: VideoPlayerControllerConfig = VideoPlayerControllerConfig.Default,
    seekBeforeMilliSeconds: Long = 10000L,
    seekAfterMilliSeconds: Long = 10000L,
    repeatMode: RepeatMode = RepeatMode.NONE,
    @FloatRange(from = 0.0, to = 1.0) volume: Float = 1f,
    onCurrentTimeChanged: (Long) -> Unit = {},
    fullScreenSecurePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    onFullScreenEnter: () -> Unit = {},
    onFullScreenExit: () -> Unit = {},
    enablePip: Boolean = false,
    enablePipWhenBackPressed: Boolean = false,
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
            .apply {
                val cache =
                    VideoPlayerCacheManager.createInstance { VideoPlayerCacheManager(context) }.cacheInstance

                if (cache != null) {
                    val cacheDataSourceFactory = CacheDataSource.Factory()
                        .setCache(cache)
                        .setUpstreamDataSourceFactory(
                            DefaultDataSource.Factory(
                                context,
                                dataSourceFactory
                            )
                        )
                    setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
                }
            }
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

    LaunchedEffect(mediaItems, player) {
        mediaSession?.release()
        mediaSession = MediaSession.Builder(context, ForwardingPlayer(player))
            .setId(
                "VideoPlayerMediaSession_${
                    UUID.randomUUID().toString().lowercase().split("-").first()
                }"
            )
            .build()

        val exoPlayerMediaItems = mediaItems.map {
            val uri = it.toUri(context)

            MediaItem.Builder().apply {
                setUri(uri)
                setMediaMetadata(it.mediaMetadata)
                setMimeType(it.mimeType)
                setDrmConfiguration(
                    if (it is VideoPlayerMediaItem.NetworkMediaItem) {
                        it.drmConfiguration
                    } else {
                        null
                    },
                )
            }.build()
        }

        player.setMediaItems(exoPlayerMediaItems)
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
        if (playerState.isPlaying) {
            enterPIPMode(context, defaultPlayerView) {
                updatePlayerState(playerState.copy(isPIP = true), player)
            }
            player.play()
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
internal fun VideoPlayerSurface(
    modifier: Modifier = Modifier,
    defaultPlayerView: PlayerView,
    player: ExoPlayer,
    autoPlay: Boolean,
    usePlayerController: Boolean,
    handleLifecycle: Boolean,
    enablePip: Boolean,
    onPipEntered: () -> Unit = {}
) {
    val context = LocalContext.current

    var isPendingPipMode by remember { mutableStateOf(false) }

    val currentEvent = remember {
        mutableStateOf<Lifecycle.Event?>(null)
    }

    val events by LocalLifecycleOwner.current.lifecycle.observeAsState()

    currentEvent.value = events

    LaunchedEffect(currentEvent.value) {
        when (currentEvent.value) {
            Lifecycle.Event.ON_PAUSE -> {
                player.pause()

                if (enablePip && player.playWhenReady) {
                    isPendingPipMode = true

                    Handler(Looper.getMainLooper()).post {
                        enterPIPMode(context, defaultPlayerView)
                        onPipEntered()

                        Handler(Looper.getMainLooper()).postDelayed({
                            isPendingPipMode = false
                        }, 500)
                    }
                }
            }

            Lifecycle.Event.ON_RESUME -> {
                if (autoPlay) player.play()

                if (enablePip && player.playWhenReady) {
                    defaultPlayerView.useController = usePlayerController
                }
            }

            Lifecycle.Event.ON_STOP -> {
                val isPipMode = context.isActivityStatePipMode()

                if (handleLifecycle || (enablePip && isPipMode && !isPendingPipMode)) {
                    player.stop()
                }
            }

            Lifecycle.Event.ON_DESTROY -> {
                player.stop()
                player.release()
            }

            else -> {}
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            defaultPlayerView.apply {
                useController = usePlayerController
                resizeMode = RESIZE_MODE_FIT
                setBackgroundColor(Color.BLACK)
            }
        }
    )


}