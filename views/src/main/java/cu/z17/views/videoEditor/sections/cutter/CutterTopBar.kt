package cu.z17.views.videoEditor.sections.cutter

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.utils.convertToMS
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CutterTopBar(
    modifier: Modifier = Modifier,
    cutPoints: ClosedFloatingPointRange<Float>,
    maxEnd: Float,
    thumbnails: List<Bitmap>,
    onCutPointsChange: (ClosedFloatingPointRange<Float>) -> Unit,
) {
    var previewsCutPoints by remember {
        mutableStateOf(cutPoints.start..cutPoints.endInclusive)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        ) {
            thumbnails.forEach {
                Z17BasePicture(
                    modifier = Modifier
                        .weight(1F),
                    source = it,
                    contentScale = ContentScale.Crop
                )
            }
        }

        RangeSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            value = previewsCutPoints,
            onValueChange = {
                if (abs((it.start / 1000L) - (it.endInclusive / 1000L)) > 3)
                    previewsCutPoints = it
            },
            valueRange = 0F..maxEnd,
            onValueChangeFinished = {
                onCutPointsChange(previewsCutPoints)
            },
            startThumb = {
                Thumb(position = (previewsCutPoints.start / 1000L))
            },
            endThumb = {
                Thumb(position = (previewsCutPoints.endInclusive / 1000L))
            },
            track = {
                Column {
                    SliderDefaults.Track(
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.Black.copy(alpha = 0.6F),
                            inactiveTrackColor = Color.Black.copy(alpha = 0.3F),
                        ),
                        sliderPositions = it
                    )
                    Box(
                        modifier = Modifier
                            .weight(1F)
                            .background(color = Color.Black.copy(alpha = 0.2F))
                    )
                }
            }
        )
    }
}

@Composable
fun Thumb(position: Float) {
    Column(
        modifier = Modifier
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1F)
                .width(2.dp)
                .background(color = MaterialTheme.colorScheme.surface)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Z17Label(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface, shape = CircleShape)
                .padding(2.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            text = position.convertToMS()
        )
    }
}