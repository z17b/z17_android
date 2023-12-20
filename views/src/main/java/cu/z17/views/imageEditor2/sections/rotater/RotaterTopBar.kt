package cu.z17.views.imageEditor2.sections.rotater

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label

@Composable
fun RotaterTopBar(
    modifier: Modifier = Modifier,
    degreesSelected: Float,
    onDegreeSelected: (Float) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clickable {
                    onDegreeSelected(ROTATION_0)
                }
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = if (degreesSelected == ROTATION_0) 0.6F else 0.3F),
                    shape = CircleShape
                )
                .padding(10.dp), contentAlignment = Alignment.Center
        ) {
            Z17Label(text = "  0°", style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .clickable {
                    onDegreeSelected(ROTATION_90)
                }
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = if (degreesSelected == ROTATION_90) 0.6F else 0.3F),
                    shape = CircleShape
                )
                .padding(10.dp), contentAlignment = Alignment.Center
        ) {
            Z17Label(text = " 90°", style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .clickable {
                    onDegreeSelected(ROTATION_180)
                }
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = if (degreesSelected == ROTATION_180) 0.6F else 0.3F),
                    shape = CircleShape
                )
                .padding(10.dp), contentAlignment = Alignment.Center
        ) {
            Z17Label(text = "180°", style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .clickable {
                    onDegreeSelected(ROTATION_270)
                }
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = if (degreesSelected == ROTATION_270) 0.6F else 0.3F),
                    shape = CircleShape
                )
                .padding(10.dp), contentAlignment = Alignment.Center
        ) {
            Z17Label(text = "270°", style = MaterialTheme.typography.titleSmall)
        }
    }
}