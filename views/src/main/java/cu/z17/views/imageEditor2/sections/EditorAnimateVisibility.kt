package cu.z17.views.imageEditor2.sections

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable

@Composable
fun EditorAnimateVisibility(
    lastImage: Bitmap?,
    content: @Composable (Bitmap) -> Unit,
    show: () -> Boolean,
) {
    AnimatedVisibility(
        visible = show(),
        enter = fadeIn(),
        exit = fadeOut(animationSpec = tween(100))
    ) {
        lastImage?.let {
            content(it)
        }
    }
}