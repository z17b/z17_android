package cu.z17.views.imageEditor2.sections.viewer

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
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material.icons.outlined.Rotate90DegreesCw
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cu.z17.views.R
import cu.z17.views.imageEditor2.Z17ImageEditorState
import cu.z17.views.picture.Z17BasePicture

@Composable
fun ViewerTopBar(
    modifier: Modifier = Modifier,
    count: Int,
    path: String,
    requestState: (Z17ImageEditorState) -> Unit,
    requestStepBack: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (count > 1)
            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    ),
                onClick = {
                    requestStepBack(path)
                }
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.Undo,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                    shape = CircleShape
                ),
            onClick = {
                requestState(Z17ImageEditorState.TEXT)
            }
        ) {
            Z17BasePicture(
                modifier = Modifier.size(25.dp),
                source = Icons.Outlined.TextFields,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                    shape = CircleShape
                ),
            onClick = {
                requestState(Z17ImageEditorState.FILTER)
            }
        ) {
            Z17BasePicture(
                modifier = Modifier.size(25.dp),
                source = R.drawable.filter,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                    shape = CircleShape
                ),
            onClick = {
                requestState(Z17ImageEditorState.CROP)
            }
        ) {
            Z17BasePicture(
                modifier = Modifier.size(25.dp),
                source = Icons.Outlined.Crop,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                    shape = CircleShape
                ),
            onClick = {
                requestState(Z17ImageEditorState.ROTATE)
            }
        ) {
            Z17BasePicture(
                modifier = Modifier.size(25.dp),
                source = Icons.Outlined.Rotate90DegreesCw,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}