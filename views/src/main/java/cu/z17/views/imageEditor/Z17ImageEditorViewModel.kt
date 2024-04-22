package cu.z17.views.imageEditor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.z17.compress.Compressor
import cu.z17.compress.constraint.format
import cu.z17.compress.constraint.resolution
import cu.z17.compress.constraint.size
import cu.z17.views.camera.Z17CameraModule
import cu.z17.views.utils.Z17MutableListFlow
import cu.z17.views.utils.getBounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class Z17ImageEditorViewModel : ViewModel() {
    private val _currentState = MutableStateFlow(Z17EditorState.LOADING)
    val currentState = _currentState.asStateFlow()

    val history = Z17MutableListFlow<Bitmap>(emptyList())

    fun setBitmap(imageBitmap: Bitmap, imagePathToSave: String) {
        _currentState.value = Z17EditorState.LOADING

        history.add(imageBitmap)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.IO) {
                    val file = File(imagePathToSave)

                    val outputStream = FileOutputStream(file)
                    imageBitmap
                        .compress(
                            Z17CameraModule.getInstance().defaultFormat ?: if (android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP,
                            90,
                            outputStream
                        )

                    outputStream.flush()
                    outputStream.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _currentState.value = Z17EditorState.VIEW
        }
    }

    fun loadBitmap(
        imageUri: Uri,
        imagePathToSave: String,
        context: Context,
        firstCompression: Boolean,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                imageUri.path?.let {
                    try {
                        val bounds = imageUri.getBounds()
                        val b = Compressor.compressAndGetBitmap(context, File(it)) {
                            if (bounds.first > 3000 && bounds.second > 3000)
                                resolution(1920, 1080)

                            if (firstCompression)
                                size(File(it).length() / 2, 10, 10)

                            format(Z17CameraModule.getInstance().defaultFormat ?: if (android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP)
                        }

                        withContext(Dispatchers.IO) {
                            val file = File(imagePathToSave)

                            val outputStream = FileOutputStream(file)
                            b.compress(
                                Z17CameraModule.getInstance().defaultFormat ?: if (android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP,
                                90,
                                outputStream
                            )

                            outputStream.flush()
                            outputStream.close()
                        }

                        history.add(b)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                _currentState.value = Z17EditorState.VIEW
            } catch (e: Exception) {
                e.printStackTrace()
                _currentState.value = Z17EditorState.ERROR
            }
        }
    }

    fun requestStepBack(imagePath: String) {
        _currentState.value = Z17EditorState.LOADING
        viewModelScope.launch(Dispatchers.IO) {

            history.removeLast()

            val file = File(imagePath)

            delay(10)

            try {
                withContext(Dispatchers.IO) {
                    val outputStream = FileOutputStream(file)
                    history.value.first().lastOrNull()
                        ?.compress(
                            Z17CameraModule.getInstance().defaultFormat ?: if (android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP,
                            90,
                            outputStream
                        )

                    outputStream.flush()
                    outputStream.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _currentState.value = Z17EditorState.VIEW
        }
    }

    fun requestCompress(imagePathToSave: String, source: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentState.value = Z17EditorState.LOADING

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    var file = File(imagePathToSave)

                    if (!file.exists()) file = File(source.path!!)

                    val firstSize = file.length()

                    // compress
                    val b = Compressor.compressAndGetBitmap(context, file) {
                        size(file.length() / 2, 10, 10)
                        format(Z17CameraModule.getInstance().defaultFormat ?: if (android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP)
                    }

                    // save compressed
                    withContext(Dispatchers.IO) {
                        val outputStream = FileOutputStream(file)
                        b.compress(
                            Z17CameraModule.getInstance().defaultFormat ?: if (android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP,
                            90,
                            outputStream
                        )

                        outputStream.flush()
                        outputStream.close()
                    }

                    val finalSize = file.length()

                    if (finalSize > firstSize) {
                        Toast.makeText(
                            context,
                            context.getText(cu.z17.views.R.string.cannot_compressed_this_time),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    history.add(b)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                _currentState.value = Z17EditorState.VIEW
            }
        }
    }

    fun requestState(z17EditorState: Z17EditorState) {
        _currentState.value = z17EditorState
    }

    // region Filter
    var filteredBitmaps = Z17MutableListFlow<Bitmap>()
    // endregion Filter
}