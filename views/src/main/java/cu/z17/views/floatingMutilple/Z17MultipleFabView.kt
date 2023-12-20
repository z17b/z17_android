package cu.z17.views.floatingMutilple

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Z17MultipleView(
    items: List<Z17MultipleFabObj>,
    modifier: Modifier = Modifier,
    baseIcon: Any = Icons.Outlined.Add,
    buttonColor: Color = MaterialTheme.colorScheme.surface,
    iconColor: Color = MaterialTheme.colorScheme.background
) {

    var filterFabState by remember {
        mutableStateOf(Z17MultipleFabState.COLLAPSED)
    }

    val transitionState = remember {
        MutableTransitionState(filterFabState).apply {
            targetState = Z17MultipleFabState.COLLAPSED
        }
    }

    val transition = updateTransition(targetState = transitionState, label = "transition")

    val iconRotationDegree by transition.animateFloat({
        tween(durationMillis = 150, easing = FastOutSlowInEasing)
    }, label = "rotation") {
        if (it.currentState == Z17MultipleFabState.EXPANDED) 230f else 0f
    }

    Column(
        modifier = modifier
            .fillMaxSize(), horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
    ) {
        Z17MultipleFabMenu(
            items = items,
            visible = filterFabState == Z17MultipleFabState.EXPANDED,
            onClick = {
                filterFabState = Z17MultipleFabState.COLLAPSED
            })

        Z17MultipleFab(
            state = filterFabState,
            rotation = iconRotationDegree,
            onClick = { state ->
                filterFabState = state
            },
            iconColor = iconColor,
            buttonColor = buttonColor,
            baseIcon = if (filterFabState == Z17MultipleFabState.COLLAPSED) baseIcon else Icons.Outlined.Close
        )
    }
}