package cu.z17.views.imageEditor.sections.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp
import cu.z17.views.R
import cu.z17.views.imageEditor.ImageEditorConfigurations
import cu.z17.views.imageEditor.Z17EditorState
import cu.z17.views.label.Z17Label
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.utils.convertToByteSize
import java.io.File

@Composable
fun ViewerTopBar(
    modifier: Modifier = Modifier,
    count: Int,
    path: String,
    requestState: (Z17EditorState) -> Unit,
    requestStepBack: (String) -> Unit,
    requestCompress: () -> Unit,
    configs: ImageEditorConfigurations,
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

        if (configs.allowText)
            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    ),
                onClick = {
                    requestState(Z17EditorState.TEXT)
                }
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.TextFields,
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
                    source = Icons.Outlined.Crop,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }

        Spacer(modifier = Modifier.width(10.dp))

        if (configs.allowRotate)
            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    ),
                onClick = {
                    requestState(Z17EditorState.ROTATE)
                }
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.Rotate90DegreesCw,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }

        Spacer(modifier = Modifier.width(10.dp))

        if (configs.allowLowerSize)
            Row(
                modifier = Modifier
                    .padding(2.dp)
                    .clickable {
                        requestCompress()
                    }
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    )
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Z17BasePicture(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(end = 2.dp),
                    source = R.drawable.compress,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )

                Z17Label(
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                    text = File(path).length().convertToByteSize()
                )
            }
    }
}