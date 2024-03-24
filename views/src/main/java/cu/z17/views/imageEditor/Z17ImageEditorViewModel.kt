package cu.z17.views.imageEditor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.z17.compress.Compressor
import cu.z17.compress.constraint.resolution
import cu.z17.compress.constraint.size
import cu.z17.views.utils.Z17MutableListFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
            val file = File(imagePathToSave)

            try {
                val outputStream = FileOutputStream(file)
                imageBitmap
                    .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _currentState.value = Z17EditorState.VIEW
        }
    }

    fun loadBitmap(imageUri: Uri, context: Context, maxSize: Long) {
        viewModelScope.launch {
            try {
                imageUri.path?.let {
                    try {
                        val b = Compressor.compressAndGetBitmap(context, File(it)) {
                            size(maxSize, 2, 30)
                            resolution(1920, 1080)
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
        viewModelScope.launch(Dispatchers.IO) {
            _currentState.value = Z17EditorState.LOADING

            history.removeLast()

            val file = File(imagePath)

            delay(10)

            try {
                val outputStream = FileOutputStream(file)
                history.value.first().lastOrNull()?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _currentState.value = Z17EditorState.VIEW
        }
    }

    fun requestState(z17EditorState: Z17EditorState) {
        _currentState.value = z17EditorState
    }

    // region Filter
    var filteredBitmaps = Z17MutableListFlow<Bitmap>()
    // endregion Filter
}