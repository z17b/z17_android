package cu.z17.views.loader

import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Z17SimpleCircleLoader(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    size: Dp = 40.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth
    )
}

@Composable
fun Z17CircleProgressLoader(
    modifier: Modifier = Modifier,
    progress: Float,
    strokeWidth: Dp = 5.dp,
    size: Dp = 30.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    CircularProgressIndicator(
        progress = progress,
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth
    )
}