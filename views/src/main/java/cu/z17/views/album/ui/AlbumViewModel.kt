package cu.z17.views.album.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cu.z17.views.album.AlbumConfiguration
import cu.z17.views.album.data.AlbumImage
import cu.z17.views.album.data.PluckRepository
import cu.z17.views.album.util.AlbumUriManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AlbumViewModel(
    private val pluckRepository: PluckRepository,
    private val albumConfiguration: AlbumConfiguration,
    private val albumUriManager: AlbumUriManager,
) : ViewModel() {

    private val _selectedImageList: MutableList<AlbumImage> = ArrayList()
    val selectedImageList: MutableList<AlbumImage> = _selectedImageList
    private val _selectedImage = MutableStateFlow(emptyList<AlbumImage>())
    private var uri: Uri? = null

    val selectedImage: StateFlow<List<AlbumImage>> = _selectedImage

    fun getPluckImage() = albumUriManager.getPluckImage(uri)

    fun getImages(): Flow<PagingData<AlbumImage>> = Pager(
        config = PagingConfig(pageSize = 50, initialLoadSize = 50, enablePlaceholders = true)
    ) {
        pluckRepository.getPicturePagingSource()
    }.flow.cachedIn(viewModelScope)

    fun isPhotoSelected(albumImage: AlbumImage, isSelected: Boolean) {
        if (isSelected) {
            if (albumConfiguration.multipleImagesAllowed) {
                _selectedImageList.add(albumImage)
            } else {
                if (_selectedImageList.isEmpty() && _selectedImageList.count() < 1) {
                    _selectedImageList.add(albumImage)
                }
            }
        } else {
            _selectedImageList.filter { it.id == albumImage.id }
                .forEach { _selectedImageList.remove(it) }
        }
        _selectedImage.value = (_selectedImageList).toSet().toList()
    }

    fun getCameraImageUri(): Uri? {
        uri = albumUriManager.getNewUri()
        return uri
    }
}
