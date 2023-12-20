package cu.z17.views.imageEditor

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cu.z17.views.R
import cu.z17.views.picture.Z17BasePicture


@Composable
fun View(
    source: Bitmap,
    count: Int,
    requestStepBack: () -> Unit,
    requestCrop: () -> Unit,
    requestFilter: () -> Unit,
    requestText: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Z17BasePicture(
            modifier = Modifier.fillMaxSize(),
            source = source
        )

        if (count > 1) Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .padding(top = 20.dp, start = 20.dp)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                    shape = CircleShape
                )
                .padding(10.dp)
                .clickable {
                    requestStepBack()
                }, contentAlignment = Alignment.Center
        ) {
            Z17BasePicture(
                modifier = Modifier.size(25.dp),
                source = Icons.Outlined.Undo,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        }

        Row(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .clickable {
                        requestText()
                    }, contentAlignment = Alignment.Center
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.TextFields,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .clickable {
                        requestFilter()
                    }, contentAlignment = Alignment.Center
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = R.drawable.filter,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3F),
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .clickable {
                        requestCrop()
                    }, contentAlignment = Alignment.Center
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.Crop,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}