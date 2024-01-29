package cu.z17.views.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label

@Composable
fun Z17Tab(
    onClick: () -> Unit, selected: Boolean, title: String
) {
    val selectedColor =
        if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground

    val selectedBg =
        if (selected) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05F) else MaterialTheme.colorScheme.onBackground.copy(
            alpha = 0.05F
        )

    Tab(
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .clip(
                RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
            )
            .background(selectedBg)
    ) {
        Z17Label(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 5.dp)
                .height(30.dp)
                .fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            color = selectedColor
        )
    }
}