@file:Suppress("UNCHECKED_CAST")

package cu.z17.views.picturesAlbum.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cu.z17.views.picturesAlbum.data.ImageRepository

internal class AlbumViewModelFactory(
    private val imageRepository: ImageRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            AlbumViewModel(this.imageRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
