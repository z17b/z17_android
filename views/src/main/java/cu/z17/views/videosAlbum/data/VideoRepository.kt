package cu.z17.views.videosAlbum.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.paging.PagingSource
import com.google.gson.Gson
import cu.z17.views.picturesAlbum.data.AlbumImage
import cu.z17.views.utils.galleryUtility.interactor.GalleryPicker
import cu.z17.views.videosAlbum.util.createVideoCursor
import cu.z17.views.videosAlbum.util.fetchPageVideo

const val Zero = 0
const val One = 1

class VideoRepository(private val context: Context) {
    private val galleryPicker = GalleryPicker(ctx = context)

     fun getVideoPagingSource(): PagingSource<Int, VideoAlbum> {
        return VideoAlbumDataSource { limit, offset ->

            galleryPicker.getVideos(limit, offset).mapNotNull {
                try {


                    VideoAlbum(
                        uri = Uri.parse(it.DATA),
                        dateTaken = it.DATE_ADDED?.toLongOrNull(),
                        displayName = it.DISPLAY_NAME,
                        id = it.ID?.toLongOrNull(),
                        folderName = it.ALBUM_NAME,
                        duration = it.DURATION?.toLongOrNull(),
                        thumbnail = getThumbnail(it.ID?.toLongOrNull())
                    )
                } catch (e: Exception){
                    null
                }
            }
        }
    }

    private fun getThumbnail(id: Long?): Bitmap? {
        val cursor = id?.let {
            MediaStore.Video.Thumbnails.getThumbnail(context.contentResolver,
                it, MediaStore.Video.Thumbnails.MINI_KIND, null)
        }

        return cursor
    }

}
