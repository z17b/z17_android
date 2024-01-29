package cu.z17.views.imageEditor.sections.filter

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cu.z17.views.picture.Z17BasePicture
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FilterTopBar(
    bitmap: Bitmap,
    filteredBitmaps: List<Bitmap>,
    filterSelected: Int,
    onSelected: (Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Z17BasePicture(
            modifier = Modifier
                .clickable {
                    coroutineScope.launch {
                        onSelected(-1)
                        delay(10)
                        onSelected(0)
                    }
                }
                .border(
                    if (0 == filterSelected) 2.dp else 0.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(5.dp)
                .size(70.dp),
            source = bitmap,
            placeholder = MaterialTheme.colorScheme.background,
            contentScale = ContentScale.Crop
        )

        filteredBitmaps.forEachIndexed { index, bitmap ->
            Z17BasePicture(
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            onSelected(-1)
                            delay(10)
                            onSelected(index + 1)
                        }
                    }
                    .border(
                        if ((index + 1) == filterSelected) 2.dp else 0.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(5.dp)
                    .size(70.dp),
                source = bitmap,
                placeholder = MaterialTheme.colorScheme.background,
                contentScale = ContentScale.Crop
            )
        }
    }
}