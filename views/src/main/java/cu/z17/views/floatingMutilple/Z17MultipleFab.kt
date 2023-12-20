package cu.z17.views.floatingMutilple

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cu.z17.views.picture.Z17BasePicture

@Composable
fun Z17MultipleFab(
    modifier: Modifier = Modifier,
    state: Z17MultipleFabState,
    rotation: Float,
    onClick: (Z17MultipleFabState) -> Unit,
    iconColor: Color,
    buttonColor: Color,
    baseIcon: Any
) {
    FloatingActionButton(
        modifier = modifier
            .rotate(rotation),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
        onClick = {
            onClick(
                if (state == Z17MultipleFabState.EXPANDED) {
                    Z17MultipleFabState.COLLAPSED
                } else {
                    Z17MultipleFabState.EXPANDED
                }
            )
        },
        shape = CircleShape,
        containerColor = buttonColor
    ) {
        Z17BasePicture(
            source = baseIcon,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(
                color = iconColor
            )
        )
    }

}