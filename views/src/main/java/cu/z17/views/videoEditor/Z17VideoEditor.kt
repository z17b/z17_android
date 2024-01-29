package cu.z17.views.videoEditor

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import cu.z17.views.imageEditor.Loading
import cu.z17.views.imageEditor.Z17EditorState
import cu.z17.views.imageEditor.sections.EditorAnimateVisibility
import cu.z17.views.videoEditor.sections.cutter.CutterBottomBar
import cu.z17.views.videoEditor.sections.cutter.CutterTopBar
import cu.z17.views.videoEditor.sections.viewer.Viewer
import cu.z17.views.videoEditor.sections.viewer.ViewerTopBar
import cu.z17.views.videoPlayer.PlayerState
import cu.z17.views.videoPlayer.controller.PlayerControls
import java.io.File


@OptIn(UnstableApi::class)
@Composable
fun Z17VideoEditor(
    modifier: Modifier = Modifier,
    source: String,
    duration: Long,
    videoPathToSave: String,
    onViewState: (Boolean) -> Unit,
    onError: () -> Unit,
    onEdited: (Boolean) -> Unit,
    viewModel: VideoEditorViewModel = viewModel(
        key = source
    ),
    configs: VideoEditorConfigurations = VideoEditorConfigurations(),
) {
    Box(modifier = modifier) {
        val context = LocalContext.current

        var currentSource by remember {
            mutableStateOf(source)
        }

        var currentDuration by remember {
            mutableLongStateOf(duration)
        }

        val currentState by viewModel.currentState.collectAsStateWithLifecycle()

        val thumbnails by viewModel.thumbnails.value.collectAsStateWithLifecycle(initialValue = emptyList())

        val isOnView by remember {
            derivedStateOf {
                currentState == Z17EditorState.VIEW
            }
        }

        var playerState by remember {
            mutableStateOf(PlayerState())
        }

        var player by remember {
            mutableStateOf<Player?>(null)
        }

        var cutPoints by remember {
            mutableStateOf(0F..currentDuration.toFloat())
        }

        fun updatePlayerState(pS: PlayerState, p: Player) {
            playerState = pS
            player = p
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // region TOP_BAR
            EditorAnimateVisibility(
                last = Uri.fromFile(File(currentSource)),
                content = {
                    ViewerTopBar(
                        canCancel = currentSource != source,
                        requestState = {
                            viewModel.requestState(it)
                            player?.pause()
                        },
                        requestCancel = {
                            cutPoints = 0F..duration.toFloat()

                            viewModel.cancelChanges(currentSource) {
                                currentSource = source
                                onEdited(false)
                                viewModel.requestState(Z17EditorState.VIEW)
                            }
                        },
                        configs = configs
                    )
                },
                show = { currentState == Z17EditorState.VIEW }
            )

            EditorAnimateVisibility(
                last = Uri.fromFile(File(source)),
                content = {
                    CutterTopBar(
                        cutPoints = cutPoints,
                        maxEnd = currentDuration.toFloat(),
                        thumbnails = thumbnails,
                        onCutPointsChange = {
                            cutPoints = it
                            player?.pause()
                        }
                    )
                },
                show = { currentState == Z17EditorState.CROP }
            )

            // endregion TOP_BAR

            // region CONTENT
            Box(
                modifier = Modifier
                    .weight(1F)
            ) {
                EditorAnimateVisibility(
                    last = Uri.fromFile(File(source)),
                    content = {
                        Viewer(
                            modifier = Modifier.fillMaxSize(),
                            source = it,
                            cutPoints = cutPoints,
                            playerState = playerState,
                            updatePlayerState = ::updatePlayerState
                        )

                        PlayerControls(
                            modifier = Modifier
                                .fillMaxSize(),
                            playerState = playerState,
                            onPauseToggle = {
                                when {
                                    playerState.isPlaying -> {
                                        player?.pause()
                                    }

                                    !playerState.isPlaying &&
                                            playerState.playbackState == Player.STATE_ENDED -> {
                                        player?.seekTo(0)

                                        player?.playWhenReady = true
                                    }

                                    else -> {
                                        player?.play()
                                    }
                                }
                            },
                        )
                    },
                    show = { true }
                )

                if (currentState == Z17EditorState.LOADING) {
                    Loading()
                }
            }
            // endregion CONTENT

            // region BOTTOM_BAR
            EditorAnimateVisibility(
                last = Uri.fromFile(File(source)),
                content = {
                    CutterBottomBar(
                        onOk = {
                            viewModel.saveVideo(
                                source = it,
                                context = context,
                                videoPathToSave = videoPathToSave,
                                startMilliseconds = cutPoints.start.toLong(),
                                endMilliseconds = cutPoints.endInclusive.toLong(),
                                onComplete = {
                                    currentDuration = it
                                    currentSource = videoPathToSave
                                    viewModel.requestState(Z17EditorState.VIEW)
                                    onEdited(true)
                                },
                                onError = {
                                    viewModel.requestState(Z17EditorState.ERROR)
                                }
                            )
                        },
                        onCancel = {
                            viewModel.requestState(Z17EditorState.VIEW)
                        }
                    )
                },
                show = { currentState == Z17EditorState.CROP }
            )
            // endregion BOTTOM_BAR
        }

        if (!isOnView)
            BackHandler {
                viewModel.requestState(Z17EditorState.VIEW)
            }

        LaunchedEffect(currentState) {
            onViewState(currentState == Z17EditorState.VIEW)

            if (currentState == Z17EditorState.ERROR) onError()
        }

        LaunchedEffect(Unit) {
            viewModel.generateThumbnailList(source, currentDuration) {
                viewModel.requestState(Z17EditorState.VIEW)
            }
        }
    }
}

@Immutable
data class VideoEditorConfigurations(
    val allowCrop: Boolean = true,
    val allowFilters: Boolean = true,
)