package cu.z17.views.videosAlbum.data

import android.content.Context
import androidx.paging.PagingSource
import cu.z17.views.videosAlbum.util.createVideoCursor
import cu.z17.views.videosAlbum.util.fetchPageVideo

const val Zero = 0
const val One = 1

class VideoRepository(private val context: Context) {

    suspend fun getCount(): Int {
        val cursor = context.createVideoCursor(Int.MAX_VALUE, Zero) ?: return Zero
        val count = cursor.count
        cursor.close()
        return count
    }

     suspend fun getByOffset(offset: Int): VideoAlbum? {
        return context.fetchPageVideo(One, offset).firstOrNull()
    }

     fun getVideoPagingSource(): PagingSource<Int, VideoAlbum> {
        return VideoAlbumDataSource { limit, offset -> context.fetchPageVideo(limit, offset) }
    }
}
