package cu.z17.views.imageEditor.sections.text

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TextTopBar(
    colors: List<Color>,
    currentColor: Int,
    onTextColorChange: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.Center
    ) {
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .clickable {
                        onTextColorChange(index)
                    }
                    .padding(horizontal = 10.dp)
                    .size(30.dp)
                    .background(color = color, shape = CircleShape)
                    .border(
                        1.dp,
                        color = if (currentColor == index) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
            )
        }
    }
}