package cu.z17.views.imageEditor2.sections.filter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cu.z17.views.picture.Z17BasePicture

@Composable
fun FilterSelector(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    onSelect: (ColorFilter?) -> Unit
) {
    Box(modifier) {
        val scrollState = rememberScrollState()

        val filtersArray = remember {
            arrayOf(
                ColorFilterWithName(
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                        setToSaturation(0F)
                    }),
                    name = "B&W" // black and white
                ),
                ColorFilterWithName(
                    colorFilter = ColorFilter.colorMatrix(
                        ColorMatrix(
                            floatArrayOf(
                                0.393f, 0.769f, 0.189f, 0f, 0f,
                                0.349f, 0.686f, 0.168f, 0f, 0f,
                                0.272f, 0.534f, 0.131f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f,
                            )
                        )
                    ),
                    name = "S" // sepia
                ),
                ColorFilterWithName(
                    colorFilter = ColorFilter.colorMatrix(
                        ColorMatrix(
                            floatArrayOf(
                                1.3f, 0f, 0f, 0f, 0f,
                                0f, 1.3f, 0f, 0f, 0f,
                                0f, 0f, 1.3f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f
                            )
                        )
                    ),
                    name = "LOFI" // alto contraste
                ),
                ColorFilterWithName(
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                        setToSaturation(1.5F)
                    }),
                    name = "HS" // staturacion aumentada
                ),
                ColorFilterWithName(
                    colorFilter = ColorFilter.colorMatrix(
                        ColorMatrix(
                            floatArrayOf(
                                1f, 0f, 0f, 0f, 100F,
                                0f, 1f, 0f, 0f, 100F,
                                0f, 0f, 1f, 0f, 100F,
                                0f, 0f, 0f, 1f, 0f
                            )
                        )
                    ),
                    name = "B" // brillo
                ),
                ColorFilterWithName(
                    colorFilter = ColorFilter.colorMatrix(
                        ColorMatrix(
                            floatArrayOf(
                                1f, 0f, 0f, 0f, -100F,
                                0f, 1f, 0f, 0f, -100F,
                                0f, 0f, 1f, 0f, -100F,
                                0f, 0f, 0f, 1f, 0f
                            )
                        )
                    ),
                    name = "D" // oscuro
                ),
                ColorFilterWithName(
                    colorFilter = ColorFilter.tint(Color.Blue, BlendMode.Color),
                    name = "BLUE"
                ),
                ColorFilterWithName(
                    colorFilter = ColorFilter.tint(Color.Red, BlendMode.Modulate),
                    name = "RED"
                ),
                ColorFilterWithName(
                    colorFilter = ColorFilter.tint(Color.Green, BlendMode.Multiply),
                    name = "GREEN"
                )
            )
        }

        var selectedFilter by remember {
            mutableStateOf("")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {

            Spacer(modifier = Modifier.width(10.dp))

            FilterItem(
                imageBitmap = imageBitmap,
                colorFilterWithName = null,
                onClick = {
                    selectedFilter = ""
                    onSelect(null)
                },
                isSelected = selectedFilter.isEmpty()
            )

            Spacer(modifier = Modifier.width(10.dp))

            filtersArray.forEach {
                FilterItem(
                    imageBitmap = imageBitmap,
                    colorFilterWithName = it,
                    onClick = {
                        selectedFilter = it.name
                        onSelect(it.colorFilter)
                    },
                    isSelected = selectedFilter == it.name
                )

                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
fun FilterItem(
    imageBitmap: ImageBitmap,
    colorFilterWithName: ColorFilterWithName?,
    onClick: () -> Unit,
    isSelected: Boolean,
) {
    Box(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(8.dp)
            .size(50.dp, 80.dp)
    ) {
        Z17BasePicture(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background
                ),
            source = imageBitmap.asAndroidBitmap(),
            filterQuality = FilterQuality.Low,
            colorFilter = colorFilterWithName?.colorFilter,
            contentScale = ContentScale.Crop
        )

        Z17BasePicture(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(24.dp)
                .offset(y = 7.dp)
                .background(color = MaterialTheme.colorScheme.background, shape = CircleShape),
            source = if (isSelected) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}