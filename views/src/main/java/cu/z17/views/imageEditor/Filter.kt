package cu.z17.views.imageEditor

import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import cu.z17.views.R
import cu.z17.views.imageEditor.filter.FilterSelector
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.utils.findActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun Filter(
    imageBitmap: ImageBitmap,
    onFilterSuccess: (ImageBitmap) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        val view = LocalView.current
        var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

        var hideFilters by remember {
            mutableStateOf(false)
        }

        fun getAndSaveView() {
            coroutineScope.launch {
                hideFilters = true
                delay(200)
                capturingViewBounds?.let { rect ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Above Android O, use PixelCopy
                        val bitmap = Bitmap.createBitmap(
                            rect.width.roundToInt(),
                            rect.height.roundToInt(),
                            Bitmap.Config.ARGB_8888
                        )
                        val location = IntArray(2)
                        view.getLocationInWindow(location)
                        PixelCopy.request(
                            context.findActivity().window, android.graphics.Rect(
                                location[0],
                                location[1],
                                location[0] + rect.width.roundToInt(),
                                location[1] + rect.height.roundToInt()
                            ), bitmap, {
                                if (it == PixelCopy.SUCCESS) {
                                    onFilterSuccess(bitmap.asImageBitmap())
                                }
                            }, Handler(Looper.getMainLooper())
                        )
                    } else {
                        val bitmap = Bitmap.createBitmap(
                            rect.width.roundToInt(),
                            rect.height.roundToInt(),
                            Bitmap.Config.ARGB_8888
                        ).applyCanvas {
                            translate(-rect.left, -rect.top)
                            view.draw(this)
                        }

                        onFilterSuccess(bitmap.asImageBitmap())
                    }

                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1F)
        ) {
            val filter = remember {
                mutableStateOf<ColorFilter?>(null)
            }

            Z17BasePicture(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        capturingViewBounds = it.boundsInRoot()
                    },
                source = imageBitmap.asAndroidBitmap(),
                colorFilter = filter.value
            )

            if (!hideFilters) FilterSelector(modifier = Modifier.align(alignment = Alignment.BottomStart),
                imageBitmap = imageBitmap,
                onSelect = {
                    filter.value = it
                })
        }

        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(20.dp)
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .border(
                        1.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape
                    )
                    .padding(10.dp)
                    .clickable {
                        onCancel()
                    }, contentAlignment = Alignment.Center
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.Clear,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .border(
                        1.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape
                    )
                    .padding(10.dp)
                    .clickable {
                        getAndSaveView()
                    }, contentAlignment = Alignment.Center
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = R.drawable.filter,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        }

        BackHandler {
            onCancel()
        }
    }
}
