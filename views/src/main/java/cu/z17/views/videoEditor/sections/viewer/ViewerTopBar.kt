package cu.z17.views.videoEditor.sections.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cu.z17.views.R
import cu.z17.views.imageEditor.Z17EditorState
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.videoEditor.VideoEditorConfigurations

@Composable
fun ViewerTopBar(
    modifier: Modifier = Modifier,
    canCancel: Boolean,
    requestState: (Z17EditorState) -> Unit,
    requestCancel: () -> Unit,
    configs: VideoEditorConfigurations,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canCancel)
            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    ),
                onClick = {
                    requestCancel()
                }
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.Undo,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }

        Spacer(modifier = Modifier.width(10.dp))

        if (configs.allowCrop)
            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    ),
                onClick = {
                    requestState(Z17EditorState.CROP)
                }
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.ContentCut,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }

        Spacer(modifier = Modifier.width(10.dp))

        if (configs.allowFilters)
            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    ),
                onClick = {
                    requestState(Z17EditorState.FILTER)
                }
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = R.drawable.filter,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }
    }
}