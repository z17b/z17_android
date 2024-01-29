package cu.z17.views.videoEditor.sections.viewer

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import cu.z17.views.videoPlayer.PlayerState
import cu.z17.views.videoPlayer.VideoPlayerSurface
import cu.z17.views.videoPlayer.cache.VideoPlayerCacheManager
import cu.z17.views.videoPlayer.uri.VideoPlayerMediaItem
import cu.z17.views.videoPlayer.uri.toUri
import java.util.UUID

@OptIn(UnstableApi::class)
@Composable
fun Viewer(
    modifier: Modifier = Modifier,
    source: Uri,
    cutPoints: ClosedFloatingPointRange<Float>,
    updatePlayerState: (PlayerState, Player) -> Unit = { _, _ -> },
    playerInstance: ExoPlayer.() -> Unit = {},
    playerState: PlayerState,
) {
    val context = LocalContext.current

    var mediaSession = remember<MediaSession?> { null }

    val dataSourceFactory: DataSource.Factory = remember {
        DefaultDataSource.Factory(context)
    }

    val player = remember {
        ExoPlayer.Builder(context)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                false,
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

    LaunchedEffect(player) {
        defaultPlayerView.player = player
    }

    LaunchedEffect(player, playerState) {
        defaultPlayerView.player = player

        player
            .addListener(object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)

                    val newPlayerState = playerState.copy(
                        isPlaying = player.isPlaying,
                        isLoading = player.isLoading,
                        playbackState = player.playbackState,
                        bufferedPercentage = player.bufferedPercentage,
                        currentPosition = player.currentPosition.coerceAtLeast(0L),
                        totalDuration = player.duration.coerceAtLeast(0L)
                    )

                    updatePlayerState(
                        newPlayerState,
                        player
                    )
                }
            })
    }

    LaunchedEffect(source, player, cutPoints) {
        mediaSession?.release()
        mediaSession = MediaSession.Builder(context, ForwardingPlayer(player))
            .setId(
                "VideoPlayerMediaSession_${
                    UUID.randomUUID().toString().lowercase().split("-").first()
                }"
            )
            .build()

        val exoPlayerMediaItem = listOf(
            VideoPlayerMediaItem.StorageMediaItem(
                storageUri = source
            ).let {
                val uri = it.toUri(context)

                var clippingConfiguration: MediaItem.ClippingConfiguration? = null

                if ((cutPoints.endInclusive - cutPoints.start) >= 3)
                    clippingConfiguration = MediaItem.ClippingConfiguration.Builder()
                        .setStartPositionMs(cutPoints.start.toLong())
                        .setEndPositionMs(cutPoints.endInclusive.toLong())
                        .build()

                MediaItem.Builder().apply {
                    setUri(uri)
                    setMediaMetadata(it.mediaMetadata)
                    setMimeType(it.mimeType)
                    if (clippingConfiguration != null) {
                        setClippingConfiguration(clippingConfiguration)
                    }
                }.build()
            }
        )

        player.setMediaItems(exoPlayerMediaItem)
        player.prepare()

        player.playWhenReady = false
    }

    VideoPlayerSurface(
        modifier = modifier.fillMaxWidth(),
        defaultPlayerView = defaultPlayerView,
        player = player,
        autoPlay = false,
        usePlayerController = false,
        handleLifecycle = true,
        enablePip = false,
        pipScale = 16 to 9
    )
}