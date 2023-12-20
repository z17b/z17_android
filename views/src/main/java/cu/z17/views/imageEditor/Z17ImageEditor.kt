package cu.z17.views.imageEditor

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cu.z17.views.loader.Z17SimpleCircleLoader
import cu.z17.views.picture.Z17BlurImage

@Composable
fun Z17ImageEditor(
    modifier: Modifier = Modifier,
    source: Uri,
    onStateChange: (Z17ImageEditorState) -> Unit,
    viewModel: Z17ImageEditorViewModel = viewModel(
        key = source.path
    ),
    context: Context = LocalContext.current
) {
    Box(modifier = modifier) {
        val currentState by viewModel.currentState.collectAsStateWithLifecycle()

        val history by viewModel.history.value.collectAsStateWithLifecycle(emptyList())

        history.firstOrNull()?.asAndroidBitmap()?.let { img ->
            Z17BlurImage(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6F),
                source = img,
                contentScale = ContentScale.Crop,
                blurRadio = 20F
            )
        }

        when (currentState) {
            Z17ImageEditorState.VIEW -> {
                onStateChange(Z17ImageEditorState.VIEW)
                history.lastOrNull()?.let {
                    View(
                        source = it.asAndroidBitmap(),
                        count = history.size,
                        requestStepBack = {
                            viewModel.requestStepBack(source.path!!)
                        },
                        requestCrop = viewModel::requestCrop,
                        requestFilter = viewModel::requestFilter,
                        requestText = viewModel::requestText
                    )
                }
            }

            Z17ImageEditorState.CROP -> {
                onStateChange(Z17ImageEditorState.CROP)
                history.lastOrNull()?.let {
                    Crop(
                        imageBitmap = it, onCropSuccess = {
                            viewModel.setBitmap(it, source.path!!)
                        }, onCancel = viewModel::returnToView
                    )
                }
            }

            Z17ImageEditorState.FILTER -> {
                onStateChange(Z17ImageEditorState.FILTER)
                history.lastOrNull()?.let {
                    Filter(
                        imageBitmap = it, onFilterSuccess = {
                            viewModel.setBitmap(it, source.path!!)
                        }, onCancel = viewModel::returnToView
                    )
                }
            }

            Z17ImageEditorState.TEXT -> {
                onStateChange(Z17ImageEditorState.TEXT)
                history.lastOrNull()?.let {
                    Text(
                        imageBitmap = it, onTextSuccess = {
                            viewModel.setBitmap(it, source.path!!)
                        }, onCancel = viewModel::returnToView
                    )
                }
            }

            Z17ImageEditorState.LOADING -> {
                onStateChange(Z17ImageEditorState.LOADING)
                Loading()
            }
        }

        LaunchedEffect(Unit) {
            viewModel.loadBitmap(source, context)
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

enum class Z17ImageEditorState {
    VIEW, CROP, FILTER, TEXT, LOADING
}