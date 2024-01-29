package cu.z17.views.videoPlayer.controller

import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import cu.z17.views.videoPlayer.PlayerState

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    playerState: PlayerState,
    onPauseToggle: () -> Unit,
) {
    var isVisible by remember {
        mutableStateOf(true)
    }

    val timer = remember {
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                isVisible = false
            }
        }
    }

    Box(
        modifier = modifier.clickable {
            timer.cancel()
            isVisible = !isVisible
        }
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                CenterControls(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    isPlaying = playerState.isPlaying,
                    onPauseToggle = onPauseToggle,
                    playbackState = playerState.playbackState
                )
            }
        }

        LaunchedEffect(isVisible) {
            if (isVisible) {
                timer.start()
            }
        }
    }

}

@Composable
fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    playbackState: Int,
    onPauseToggle: () -> Unit,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(
            modifier = Modifier.size(60.dp), onClick = onPauseToggle
        ) {
            Icon(
                when {
                    isPlaying -> {
                        Icons.Outlined.Pause
                    }

                    playbackState == Player.STATE_ENDED -> {
                        Icons.Outlined.PlayArrow
                    }

                    else -> {
                        Icons.Outlined.PlayArrow
                    }
                },
                tint = Color.White,
                contentDescription = "Play/Pause",
            )
        }
    }
}