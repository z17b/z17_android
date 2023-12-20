@file:Suppress("UNCHECKED_CAST")

package cu.z17.views.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class Z17CameraViewModelFactory(
    private val imagePathToSave: String,
    private val videoPathToSave: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(Z17CameraViewModel::class.java)) {
            Z17CameraViewModel(this.imagePathToSave, this.videoPathToSave) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}