package cu.z17.views.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Z17SnackBar(
    visible: MutableState<Boolean>,
    action: () -> Unit = {},
    onClose: () -> Unit = {},
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible.value,
            enter = expandVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            ),
            exit = shrinkVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )
        ) {
            Snackbar(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background,
                action = { action() },
                modifier = Modifier.padding(8.dp)
            ) {
                Z17Label(
                    text = message,
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
    }

    rememberCoroutineScope().launch {
        if (visible.value) {
            delay(3000)
            // pregunta de nuevo pq la recomposicion continua y el delay se interceptan aun cuando la lista esta llena
            visible.value = false
            onClose()
        }
    }
}