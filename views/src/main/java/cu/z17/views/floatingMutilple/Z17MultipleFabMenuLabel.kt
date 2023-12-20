package cu.z17.views.floatingMutilple

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label

@Composable
fun Z17MultipleFabMenuLabel(
    modifier: Modifier = Modifier,
    label: String
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = Color.Black.copy(alpha = 0.4f)
    ) {
        Z17Label(
            text = label,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
            maxLines = 1
        )
    }
}