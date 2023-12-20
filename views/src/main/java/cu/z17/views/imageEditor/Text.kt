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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import cu.z17.views.inputText.Z17InputText
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.utils.findActivity
import cu.z17.views.zoomables.rememberZoomState
import cu.z17.views.zoomables.zoom
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Text(
    imageBitmap: ImageBitmap,
    onTextSuccess: (ImageBitmap) -> Unit,
    onCancel: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {

        val focusRequester = remember {
            FocusRequester()
        }

        val colors = remember {
            listOf(
                Color.White,
                Color.Black,
                Color(0xFFFF9800),
                Color(0xFFc62828),
                Color(0xFF4CAF50),
                Color(0xFF9C27B0)
            )
        }

        val textValue = remember {
            mutableStateOf("")
        }

        val textColor = remember {
            mutableIntStateOf(0)
        }

        val scrollState = rememberScrollState()

        var isEditing by remember {
            mutableStateOf(true)
        }

        val keyboardController = LocalSoftwareKeyboardController.current

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        val view = LocalView.current
        var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

        fun getAndSaveView() {
            coroutineScope.launch {
                isEditing = false
                focusRequester.freeFocus()
                keyboardController?.hide()

                delay(600)
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
                                    onTextSuccess(bitmap.asImageBitmap())
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

                        onTextSuccess(bitmap.asImageBitmap())
                    }

                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1F)
        ) {
            Box {
                Z17BasePicture(
                    modifier = Modifier
                        .onGloballyPositioned {
                            capturingViewBounds = it.boundsInRoot()
                        }
                        .fillMaxSize(),
                    source = imageBitmap.asAndroidBitmap()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zoom(
                            clip = false,
                            zoomState = rememberZoomState(limitPan = true, rotatable = true),
                            onGestureStart = {
                                keyboardController?.hide()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Z17InputText(
                        modifier = Modifier.focusRequester(focusRequester),
                        readOnly = !isEditing,
                        value = textValue.value,
                        onTextChange = {
                            textValue.value = it
                        },
                        labelText = "Abc...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = colors[textColor.intValue],
                            textAlign = TextAlign.Center
                        ),
                        shape = CircleShape
                    )
                }
            }

            if (isEditing)
                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.Center
                ) {
                    colors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .clickable {
                                    textColor.intValue = index
                                }
                                .padding(horizontal = 10.dp)
                                .size(30.dp)
                                .background(color = color, shape = CircleShape)
                                .border(
                                    1.dp,
                                    color = if (textColor.intValue == index) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                                    shape = CircleShape
                                )
                        )
                    }
                }
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
                        1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape
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
                        1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape
                    )
                    .padding(10.dp)
                    .clickable {
                        getAndSaveView()
                    }, contentAlignment = Alignment.Center
            ) {
                Z17BasePicture(
                    modifier = Modifier.size(25.dp),
                    source = Icons.Outlined.Check,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        BackHandler {
            onCancel()
        }
    }
}
