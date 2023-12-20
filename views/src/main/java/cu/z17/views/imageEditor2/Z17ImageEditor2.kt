package cu.z17.views.imageEditor2

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cu.z17.views.imageEditor2.sections.EditorAnimateVisibility
import cu.z17.views.imageEditor2.sections.cropper.Cropper
import cu.z17.views.imageEditor2.sections.rotater.ROTATION_0
import cu.z17.views.imageEditor2.sections.rotater.Rotate
import cu.z17.views.imageEditor2.sections.rotater.RotaterTopBar
import cu.z17.views.imageEditor2.sections.text.Text2
import cu.z17.views.imageEditor2.sections.text.TextBottomBar
import cu.z17.views.imageEditor2.sections.text.TextTopBar
import cu.z17.views.imageEditor2.sections.viewer.Viewer2
import cu.z17.views.imageEditor2.sections.viewer.ViewerTopBar
import cu.z17.views.loader.Z17SimpleCircleLoader
import cu.z17.views.picture.Z17BlurImage
import cu.z17.views.utils.findActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Z17ImageEditor2(
    modifier: Modifier = Modifier,
    source: Uri,
    onViewState: (Boolean) -> Unit,
    viewModel: Z17ImageEditorViewModel = viewModel(
        key = source.path
    ),
    context: Context = LocalContext.current,
) {
    Box(modifier = modifier) {
        val currentState by viewModel.currentState.collectAsStateWithLifecycle()

        val history by viewModel.history.value.collectAsStateWithLifecycle(emptyList())

        fun setState(newState: Z17ImageEditorState) {
            viewModel.requestState(newState)
        }

        var actualBitmap by remember {
            mutableStateOf<Bitmap?>(null)
        }

        actualBitmap?.let { img ->
            Z17BlurImage(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6F),
                source = img,
                contentScale = ContentScale.Crop,
                blurRadio = 20F
            )
        }

        val coroutineScope = rememberCoroutineScope()

        val focusRequester = remember {
            FocusRequester()
        }

        val keyboardController = LocalSoftwareKeyboardController.current

        val view = LocalView.current
        var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

        val isOnView by remember {
            derivedStateOf {
                currentState == Z17ImageEditorState.VIEW
            }
        }

        fun onStepCompleted(image: Bitmap) {
            viewModel.requestState(Z17ImageEditorState.VIEW)
            viewModel.setBitmap(image, source.path!!)
        }

        fun getAndSaveView() {
            coroutineScope.launch {
                try {
                    focusRequester.freeFocus()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    // when is not focus requester set
                }
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
                                    onStepCompleted(bitmap)
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

                        onStepCompleted(bitmap)
                    }
                }
            }
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

        var currentColor by remember {
            mutableIntStateOf(0)
        }

        var degrees by remember {
            mutableFloatStateOf(ROTATION_0)
        }

        Column {
            // region TOP BAR
            EditorAnimateVisibility(
                lastImage = actualBitmap,
                content = {
                    ViewerTopBar(
                        requestState = ::setState,
                        path = source.path!!,
                        count = history.size,
                        requestStepBack = viewModel::requestStepBack
                    )
                },
                show = { currentState == Z17ImageEditorState.VIEW }
            )

            EditorAnimateVisibility(
                lastImage = actualBitmap,
                content = {
                    TextTopBar(
                        colors = colors,
                        currentColor = currentColor,
                        onTextColorChange = {
                            currentColor = it
                        }
                    )
                },
                show = { currentState == Z17ImageEditorState.TEXT }
            )

            EditorAnimateVisibility(
                lastImage = actualBitmap,
                content = {
                    RotaterTopBar(
                        degreesSelected = degrees,
                        onDegreeSelected = {
                            degrees = it
                        }
                    )
                },
                show = { currentState == Z17ImageEditorState.ROTATE }
            )
            // endregion TOP BAR

            // region CONTENT
            Box(
                modifier = Modifier
                    .weight(1F)
            ) {
                EditorAnimateVisibility(
                    lastImage = actualBitmap,
                    content = {
                        Viewer2(
                            source = it
                        )
                    },
                    show = { currentState == Z17ImageEditorState.VIEW }
                )

                EditorAnimateVisibility(
                    lastImage = actualBitmap,
                    content = {
                        Text2(
                            imageBitmap = it,
                            focusRequester = focusRequester,
                            textColor = currentColor,
                            colors = colors,
                            onGloballyPositioned = {
                                capturingViewBounds =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        it.boundsInWindow()
                                    } else {
                                        it.boundsInRoot()
                                    }
                            }
                        )
                    },
                    show = { currentState == Z17ImageEditorState.TEXT }
                )

                EditorAnimateVisibility(
                    lastImage = actualBitmap,
                    content = {
                        Cropper(
                            imageBitmap = it.asImageBitmap(),
                            onCropSuccess = {
                                viewModel.setBitmap(it.asAndroidBitmap(), source.path!!)
                            },
                            onCancel = {
                                viewModel.requestState(Z17ImageEditorState.VIEW)
                            }
                        )
                    },
                    show = { currentState == Z17ImageEditorState.CROP }
                )

                EditorAnimateVisibility(
                    lastImage = actualBitmap,
                    content = {
                        Rotate(
                            imageBitmap = it,
                            degrees = degrees,
                            onImageRotated = {
                                viewModel.setBitmap(it, source.path!!)
                            },
                            onCancel = {
                                viewModel.requestState(Z17ImageEditorState.VIEW)
                            }
                        )
                    },
                    show = { currentState == Z17ImageEditorState.ROTATE }
                )

                EditorAnimateVisibility(
                    lastImage = actualBitmap,
                    content = {
                        Loading()
                    },
                    show = { currentState == Z17ImageEditorState.LOADING || actualBitmap == null }
                )
            }
            // endregion CONTENT

            // region BOTTOMBAR
            EditorAnimateVisibility(
                lastImage = actualBitmap,
                content = {
                    TextBottomBar(
                        onOk = {
                            getAndSaveView()
                        },
                        onCancel = {
                            viewModel.requestState(Z17ImageEditorState.VIEW)
                        }
                    )
                },
                show = { currentState == Z17ImageEditorState.TEXT }
            )
            // endregion BOTTOMBAR
        }

        if (!isOnView)
            BackHandler {
                viewModel.requestState(Z17ImageEditorState.VIEW)
            }

        LaunchedEffect(Unit) {
            viewModel.loadBitmap(source, context)
        }

        LaunchedEffect(currentState) {
            onViewState(currentState == Z17ImageEditorState.VIEW)
        }

        LaunchedEffect(history.size) {
            actualBitmap = null
            delay(100)
            actualBitmap = history.lastOrNull()
        }

        DisposableEffect(Unit) {
            onDispose {
                viewModel.history.removeAll()
            }
        }
    }
}

@Composable
fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.8F)),
        contentAlignment = Alignment.Center
    ) {
        Z17SimpleCircleLoader()
    }
}

enum class Z17ImageEditorState {
    VIEW,
    CROP,
    FILTER,
    TEXT,
    ROTATE,
    LOADING
}