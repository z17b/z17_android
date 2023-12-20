package cu.z17.views.imageEditor2.sections.cropper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cu.z17.views.picture.Z17BasePicture

@Composable
fun CropperBottomBar(
    onOk: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(20.dp)
            .padding(bottom = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            modifier = Modifier
                .border(
                    1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = CircleShape
                ),
            onClick = {
                onCancel()
            }
        ) {
            Z17BasePicture(
                modifier = Modifier.size(25.dp),
                source = Icons.Outlined.Clear,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            modifier = Modifier
                .border(
                    1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = CircleShape
                ),
            onClick = {
                onOk()
            }
        ) {
            Z17BasePicture(
                modifier = Modifier.size(25.dp),
                source = Icons.Outlined.Crop,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}