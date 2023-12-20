package cu.z17.views.floatingMutilple

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Z17MultipleFabMenuItem(
    modifier: Modifier = Modifier,
    menuItem: Z17MultipleFabObj,
    onMenuItemClick: (Z17MultipleFabObj) -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        //label
        Z17MultipleFabMenuLabel(label = menuItem.label)

        //fab
        Z17MultipleFabMenuButton(
            modifier = Modifier.size(50.dp),
            item = menuItem,
            onClick = onMenuItemClick,
            containerColor = menuItem.buttonColor,
            iconColor = menuItem.iconColor
        )

    }

}