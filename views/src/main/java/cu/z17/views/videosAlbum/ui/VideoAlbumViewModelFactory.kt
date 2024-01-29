@file:Suppress("UNCHECKED_CAST")

package cu.z17.views.videosAlbum.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cu.z17.views.videosAlbum.data.VideoAlbumConfiguration
import cu.z17.views.videosAlbum.data.VideoRepository

internal class VideoAlbumViewModelFactory(
    private val videoRepository: VideoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(VideoAlbumViewModel::class.java)) {
            VideoAlbumViewModel(this.videoRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
