package cu.z17.views.floatingMutilple

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cu.z17.views.picture.Z17BasePicture

@Composable
fun Z17MultipleFabMenuButton(
    modifier: Modifier = Modifier,
    item: Z17MultipleFabObj,
    onClick: (Z17MultipleFabObj) -> Unit,
    containerColor: Color?,
    iconColor: Color?
) {

    FloatingActionButton(
        modifier = modifier,
        onClick = {
            onClick(item)
        },
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
        shape = CircleShape,
        containerColor = containerColor ?: MaterialTheme.colorScheme.surface
    ) {
        Z17BasePicture(
            source = item.icon,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(
                color = iconColor ?: MaterialTheme.colorScheme.background
            )
        )
    }
}