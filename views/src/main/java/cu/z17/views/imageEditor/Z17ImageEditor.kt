package cu.z17.views.imageEditor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cu.z17.views.imageEditor.sections.EditorAnimateVisibility
import cu.z17.views.imageEditor.sections.cropper.Cropper
import cu.z17.views.imageEditor.sections.filter.Filter
import cu.z17.views.imageEditor.sections.filter.FilterBottomBar
import cu.z17.views.imageEditor.sections.filter.FilterTopBar
import cu.z17.views.imageEditor.sections.rotater.ROTATION_0
import cu.z17.views.imageEditor.sections.rotater.Rotate
import cu.z17.views.imageEditor.sections.rotater.RotaterTopBar
import cu.z17.views.imageEditor.sections.text.Text2
import cu.z17.views.imageEditor.sections.text.TextBottomBar
import cu.z17.views.imageEditor.sections.text.TextTopBar
import cu.z17.views.imageEditor.sections.viewer.Viewer
import cu.z17.views.imageEditor.sections.viewer.ViewerTopBar
import cu.z17.views.loader.Z17SimpleCircleLoader
import cu.z17.views.picture.Z17BlurImage
import kotlinx.coroutines.delay

@Composable
fun Z17ImageEditor(
    modifier: Modifier = Modifier,
    source: Uri,
    imagePathToSave: String,
    onViewState: (Boolean) -> Unit,
    onError: () -> Unit,
    onEdited: (Boolean) -> Unit,
    viewModel: Z17ImageEditorViewModel = viewModel(
        key = source.path
    ),
    context: Context = LocalContext.current,
    configs: ImageEditorConfigurations = ImageEditorConfigurations(allowText = false),
) {
    Box(modifier = modifier) {
        val currentState by viewModel.currentState.collectAsStateWithLifecycle()

        val history by viewModel.history.value.collectAsStateWithLifecycle(emptyList())

        fun setState(newState: Z17EditorState) {
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

        val focusRequester = remember {
            FocusRequester()
        }

        var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }

        val isOnView by remember {
            derivedStateOf {
                currentState == Z17EditorState.VIEW
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

        val filteredBitmaps by viewModel.filteredBitmaps.value.collectAsStateWithLifecycle(
            initialValue = emptyList()
        )

        var filterSelected by remember {
            mutableIntStateOf(0)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // region TOP BAR
            EditorAnimateVisibility(
                last = actualBitmap,
                content = {
                    ViewerTopBar(
                        requestState = ::setState,
                        path = source.path!!,
                        count = history.size,
                        requestStepBack = viewModel::requestStepBack,
                        configs = configs
                    )
                },
                show = { currentState == Z17EditorState.VIEW }
            )

            EditorAnimateVisibility(
                last = actualBitmap,
                content = {
                    TextTopBar(
                        colors = colors,
                        currentColor = currentColor,
                        onTextColorChange = {
                            currentColor = it
                        }
                    )
                },
                show = { currentState == Z17EditorState.TEXT }
            )

            EditorAnimateVisibility(
                last = actualBitmap,
                content = {
                    RotaterTopBar(
                        degreesSelected = degrees,
                        onDegreeSelected = {
                            degrees = it
                        }
                    )
                },
                show = { currentState == Z17EditorState.ROTATE }
            )

            EditorAnimateVisibility(
                last = actualBitmap,
                content = {
                    FilterTopBar(
                        bitmap = it,
                        filteredBitmaps = filteredBitmaps,
                        filterSelected = filterSelected,
                        onSelected = {
                            filterSelected = it
                        }
                    )
                },
                show = { currentState == Z17EditorState.FILTER }
            )
            // endregion TOP BAR

            // region CONTENT
            Box(
                modifier = Modifier
                    .weight(1F)
            ) {
                EditorAnimateVisibility(
                    last = actualBitmap,
                    content = {
                        Viewer(
                            source = it
                        )
                    },
                    show = { currentState == Z17EditorState.VIEW }
                )

                EditorAnimateVisibility(
                    last = actualBitmap,
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
                    show = { currentState == Z17EditorState.TEXT }
                )

                EditorAnimateVisibility(
                    last = actualBitmap,
                    content = {
                        Cropper(
                            imageBitmap = it.asImageBitmap(),
                            onCropSuccess = {
                                viewModel.setBitmap(it.asAndroidBitmap(), imagePathToSave)
                                onEdited(true)
                            },
                            onCancel = {
                                viewModel.requestState(Z17EditorState.VIEW)
                            }
                        )
                    },
                    show = { currentState == Z17EditorState.CROP }
                )

                EditorAnimateVisibility(
                    last = actualBitmap,
                    content = {
                        Rotate(
                            imageBitmap = it,
                            degrees = degrees,
                            onImageRotated = {
                                viewModel.setBitmap(it, imagePathToSave)
                                onEdited(true)
                            },
                            onCancel = {
                                viewModel.requestState(Z17EditorState.VIEW)
                            }
                        )
                    },
                    show = { currentState == Z17EditorState.ROTATE }
                )

                EditorAnimateVisibility(
                    last = actualBitmap,
                    content = {
                        Filter(
                            bitmap = it,
                            filteredBitmaps = filteredBitmaps,
                            filterSelected = filterSelected,
                            onFiltersLoad = {
                                viewModel.filteredBitmaps.removeAll()
                                viewModel.filteredBitmaps.addAll(it)
                            }
                        )
                    },
                    show = { currentState == Z17EditorState.FILTER }
                )

                if (currentState == Z17EditorState.LOADING || actualBitmap == null) {
                    Loading()
                }
            }
            // endregion CONTENT

            // region BOTTOMBAR
            EditorAnimateVisibility(
                last = actualBitmap,
                content = {
                    TextBottomBar(
                        onOk = {
                            //getAndSaveView()
                        },
                        onCancel = {
                            viewModel.requestState(Z17EditorState.VIEW)
                        }
                    )
                },
                show = { currentState == Z17EditorState.TEXT }
            )

            EditorAnimateVisibility(
                last = actualBitmap,
                content = {
                    FilterBottomBar(
                        onOk = {
                            val selected =
                                if (filterSelected == 0) it else filteredBitmaps[filterSelected - 1]

                            viewModel.setBitmap(selected, imagePathToSave)
                            onEdited(true)
                        },
                        onCancel = {
                            viewModel.requestState(Z17EditorState.VIEW)
                        }
                    )
                },
                show = { currentState == Z17EditorState.FILTER }
            )
            // endregion BOTTOMBAR
        }

        if (!isOnView)
            BackHandler {
                viewModel.requestState(Z17EditorState.VIEW)
            }

        LaunchedEffect(Unit) {
            viewModel.loadBitmap(source, context)
        }

        LaunchedEffect(currentState) {
            onViewState(currentState == Z17EditorState.VIEW)

            if (currentState == Z17EditorState.ERROR) onError()
        }

        LaunchedEffect(history.size) {
            actualBitmap = null
            delay(100)
            actualBitmap = history.lastOrNull()

            if (history.isEmpty())
                onEdited(false)
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
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Z17SimpleCircleLoader()
    }
}

enum class Z17EditorState {
    VIEW,
    CROP,
    FILTER,
    TEXT,
    ROTATE,
    LOADING,
    ERROR
}

@Immutable
data class ImageEditorConfigurations(
    val allowCrop: Boolean = true,
    val allowFilters: Boolean = true,
    val allowText: Boolean = true,
    val allowRotate: Boolean = true,
)