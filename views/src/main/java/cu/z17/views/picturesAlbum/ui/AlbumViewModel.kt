package cu.z17.views.picturesAlbum.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cu.z17.views.picturesAlbum.data.AlbumImage
import cu.z17.views.picturesAlbum.data.ImageRepository
import cu.z17.views.utils.Z17MutableListFlow
import kotlinx.coroutines.flow.Flow

class AlbumViewModel(
    private val imageRepository: ImageRepository,
) : ViewModel() {

    val selectedList = Z17MutableListFlow<AlbumImage>(emptyList())

    fun getImages(): Flow<PagingData<AlbumImage>> = Pager(
        config = PagingConfig(pageSize = 50, initialLoadSize = 50, enablePlaceholders = true)
    ) {
        imageRepository.getPicturePagingSource()
    }.flow.cachedIn(viewModelScope)
}
