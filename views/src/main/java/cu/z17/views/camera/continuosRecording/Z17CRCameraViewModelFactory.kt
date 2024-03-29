@file:Suppress("UNCHECKED_CAST")

package cu.z17.views.camera.continuosRecording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class Z17CRCameraViewModelFactory(
    private val videoPathToSave: String,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(Z17CRCameraViewModel::class.java)) {
            Z17CRCameraViewModel(this.videoPathToSave) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}