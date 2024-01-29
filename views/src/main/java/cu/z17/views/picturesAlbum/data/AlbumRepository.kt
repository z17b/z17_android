package cu.z17.views.picturesAlbum.data

import android.content.Context
import androidx.paging.PagingSource
import cu.z17.views.picturesAlbum.util.createCursor
import cu.z17.views.picturesAlbum.util.fetchPagePicture

private const val Zero = 0
private const val One = 1

class ImageRepository(private val context: Context) {

    suspend fun getCount(): Int {
        val cursor = context.createCursor(Int.MAX_VALUE, Zero) ?: return Zero
        val count = cursor.count
        cursor.close()
        return count
    }

    suspend fun getByOffset(offset: Int): AlbumImage? {
        return context.fetchPagePicture(One, offset).firstOrNull()
    }

    fun getPicturePagingSource(): PagingSource<Int, AlbumImage> {
        return AlbumDataSource { limit, offset -> context.fetchPagePicture(limit, offset) }
    }
}
