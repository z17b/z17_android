package cu.z17.views.pdfViewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label
import cu.z17.views.picture.Z17BasePicture

@Composable
internal fun PdfControls(
    actualPageIndex: Int,
    pageCount: Int,
    onPageChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.padding(20.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (actualPageIndex != 0)
            IconButton(modifier = Modifier.background(
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5F
                ),
                shape = CircleShape
            ),
                onClick = { onPageChange(actualPageIndex - 1) }) {
                Z17BasePicture(
                    modifier = Modifier.size(24.dp),
                    source = Icons.Default.SkipPrevious,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
                )
            }

        Spacer(modifier = Modifier.width(10.dp))

        Z17Label(
            text = "P: ${actualPageIndex + 1} / $pageCount",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.width(10.dp))

        if (actualPageIndex != (pageCount - 1))
            IconButton(modifier = Modifier.background(
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5F
                ),
                shape = CircleShape
            ),
                onClick = { onPageChange(actualPageIndex + 1) }) {
                Z17BasePicture(
                    modifier = Modifier.size(24.dp),
                    source = Icons.Default.SkipNext,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
                )
            }
    }
}