package cu.z17.views.imageEditor2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val _currentState = MutableStateFlow(Z17ImageEditorState.LOADING)
    val currentState = _currentState.asStateFlow()

    val history = Z17MutableListFlow<Bitmap>(emptyList())

    fun setBitmap(imageBitmap: Bitmap, imagePath: String) {
        _currentState.value = Z17ImageEditorState.LOADING

        history.add(imageBitmap)

        viewModelScope.launch(Dispatchers.IO) {
            /*val file = File(imagePath)

            try {
                val outputStream = FileOutputStream(file)
                imageBitmap
                    .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
*/
            _currentState.value = Z17ImageEditorState.VIEW
        }
    }

    fun loadBitmap(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val b = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            context.contentResolver,
                            imageUri
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                }

                history.add(b)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _currentState.value = Z17ImageEditorState.VIEW
        }
    }

    fun requestStepBack(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentState.value = Z17ImageEditorState.LOADING

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

            _currentState.value = Z17ImageEditorState.VIEW
        }
    }

    fun requestState(z17ImageEditorState: Z17ImageEditorState) {
        _currentState.value = z17ImageEditorState
    }

}