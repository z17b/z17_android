package cu.z17.views.textToggle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label

@Composable
fun Z17TextToggle(
    modifier: Modifier = Modifier,
    state: Boolean,
    onChange: (Boolean) -> Unit,
    leading: @Composable (() -> Unit)? = null,
    label: String = "",
    labelStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    switchColors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.onBackground,
        checkedTrackColor = MaterialTheme.colorScheme.primary,
        uncheckedBorderColor = MaterialTheme.colorScheme.onBackground,
        uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
        uncheckedTrackColor = Color.Transparent
    )
) {
    Row(
        modifier = modifier.clickable {
            onChange(!state)
        },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading?.let {
            leading()
        }

        Z17Label(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 10.dp),
            text = label,
            style = labelStyle
        )

        Switch(
            checked = state,
            onCheckedChange = onChange,
            colors = switchColors
        )
    }
}