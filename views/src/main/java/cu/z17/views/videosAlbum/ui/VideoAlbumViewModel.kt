package cu.z17.views.videosAlbum.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cu.z17.views.utils.Z17MutableListFlow
import cu.z17.views.videosAlbum.data.VideoAlbum
import cu.z17.views.videosAlbum.data.VideoRepository
import kotlinx.coroutines.flow.Flow

class VideoAlbumViewModel(
    private val videoRepository: VideoRepository,
) : ViewModel() {

    val selectedList = Z17MutableListFlow<VideoAlbum>(emptyList())


    fun getVideos(): Flow<PagingData<VideoAlbum>> = Pager(
        config = PagingConfig(pageSize = 50, initialLoadSize = 50, enablePlaceholders = true)
    ) {
        videoRepository.getVideoPagingSource()
    }.flow.cachedIn(viewModelScope)

}
