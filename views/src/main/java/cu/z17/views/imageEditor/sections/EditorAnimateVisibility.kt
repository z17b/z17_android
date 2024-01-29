package cu.z17.views.imageEditor.sections

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable

@Composable
fun EditorAnimateVisibility(
    last: Bitmap?,
    content: @Composable (Bitmap) -> Unit,
    show: () -> Boolean,
) {
    AnimatedVisibility(
        visible = show(),
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(100))
    ) {
        last?.let {
            content(it)
        }
    }
}

@Composable
fun EditorAnimateVisibility(
    last: Uri?,
    content: @Composable (Uri) -> Unit,
    show: () -> Boolean,
) {
    AnimatedVisibility(
        visible = show(),
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(100))
    ) {
        last?.let {
            content(it)
        }
    }
}