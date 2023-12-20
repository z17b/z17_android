package cu.z17.views.floatingMutilple

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Z17MultipleFabMenu(
    modifier: Modifier = Modifier,
    visible: Boolean,
    items: List<Z17MultipleFabObj>,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
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
        Column(
            modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items.forEach { menuItem ->
                Z17MultipleFabMenuItem(
                    menuItem = menuItem,
                    onMenuItemClick = {
                        onClick()
                        menuItem.action()
                    }
                )
            }
        }
    }
}
