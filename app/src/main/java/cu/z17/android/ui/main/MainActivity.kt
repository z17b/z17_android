package cu.z17.android.ui.main

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.Player
import cu.z17.android.ui.theme.AppTheme
import cu.z17.compress.compressFormat
import cu.z17.views.camera.Z17CameraModule
import cu.z17.views.permission.PermissionNeedIt
import cu.z17.views.permission.Z17PermissionCheckerAndRequester
import cu.z17.views.utils.Z17BasePictureHeaders
import cu.z17.views.utils.Z17CoilDecoders
import cu.z17.views.videoPlayer.PlayerState
import cu.z17.views.videoPlayer.RESIZE_MODE_FILL
import cu.z17.views.videoPlayer.RepeatMode
import cu.z17.views.videoPlayer.Z17HLSVideoPlayer
import cu.z17.views.videoPlayer.Z17VideoModule
import cu.z17.views.videoPlayer.uri.HLSMediaItem

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        Z17CoilDecoders.createInstance { Z17CoilDecoders(applicationContext) }
        Z17BasePictureHeaders.createInstance {
            Z17BasePictureHeaders(
                headers = mapOf(
                    "Authorization" to "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDg4MjQwODksInRvRHVzSWQiOiJFZGR5IiwidXNlcm5hbWUiOiI1MzUyMTg5Mzc2IiwidmVyc2lvbiI6IjMwMDI0In0.YkpHKwJ6leAqmJc9Tkqfr5BDjciIKwNaJwXKPV26uGQ",
                    "User-Agent" to "ToDus 2.0.24 Pictures",
                    "Content-Type" to "application/json"
                )
            )
        }

        Z17CameraModule.createInstance { Z17CameraModule(applicationContext) }.apply {
            this.defaultFormat = "png".compressFormat()
        }
        Z17VideoModule.createInstance { Z17VideoModule(applicationContext) }

        setContent {
            AppTheme {

                var permissionsAccepted by remember {
                    mutableStateOf(false)
                }

                val context = LocalContext.current

                if (permissionsAccepted) {
                    var playerState by remember {
                        mutableStateOf(PlayerState())
                    }

                    var player by remember {
                        mutableStateOf<Player?>(null)
                    }

                    fun updatePlayerState(pS: PlayerState, p: Player) {
                        playerState = pS
                        player = p
                    }

                    fun onCurrentTimeChanged(t: Long) {
                        playerState = playerState.copy(currentPosition = t)
                    }
                    Z17HLSVideoPlayer(
                        modifier = Modifier.fillMaxSize(),
                        mediaItem = HLSMediaItem(
                            url = "https://stream.todus.cu/vs/68b13067c5"
                        ),
                        handleLifecycle = true,
                        autoPlay = false,
                        usePlayerController = false,
                        enablePip = false,
                        enablePipWhenBackPressed = true,
                        handleAudioFocus = true,
                        volume = 1F,
                        repeatMode = RepeatMode.ONE,
                        playerState = playerState,
                        updatePlayerState = ::updatePlayerState,
                        onCurrentTimeChanged = ::onCurrentTimeChanged,
                        contentScale = RESIZE_MODE_FILL
                    )
                }

                Z17PermissionCheckerAndRequester(
                    initialPermissions = listOf(
                        PermissionNeedIt.CAMERA,
                        PermissionNeedIt.RECORD_AUDIO,
                        PermissionNeedIt.STORAGE
                    ),
                    onGranted = {
                        permissionsAccepted = true
                    },
                    packageName = context.packageName,
                    stringContent = null
                )
            }
        }
    }
}


@Composable
fun <T> CrossFade(
    targetState: T,
    content: @Composable (T) -> Unit,
) {
    Crossfade(
        targetState = targetState,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ), label = "crossfade"
    ) {
        content(it)
    }
}