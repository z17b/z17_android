@file:Suppress("UNCHECKED_CAST")

package cu.z17.views.album.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cu.z17.views.album.AlbumConfiguration
import cu.z17.views.album.data.PluckRepository
import cu.z17.views.album.ui.AlbumViewModel

internal class AlbumViewModelFactory(
    private val pluckRepository: PluckRepository,
    private val albumUriManager: AlbumUriManager,
    private val albumConfiguration: AlbumConfiguration,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            AlbumViewModel(this.pluckRepository, this.albumConfiguration, this.albumUriManager) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
