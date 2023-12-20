package cu.z17.views.album.data

import android.content.Context
import androidx.paging.PagingSource
import cu.z17.views.album.util.createCursor
import cu.z17.views.album.util.fetchPagePicture

private const val Zero = 0
private const val One = 1

interface PluckRepository {
    suspend fun getCount(): Int
    suspend fun getByOffset(offset: Int): AlbumImage?
    fun getPicturePagingSource(): PagingSource<Int, AlbumImage>
}

class PluckRepositoryImpl(private val context: Context) : PluckRepository {

    override suspend fun getCount(): Int {
        val cursor = context.createCursor(Int.MAX_VALUE, Zero) ?: return Zero
        val count = cursor.count
        cursor.close()
        return count
    }

    override suspend fun getByOffset(offset: Int): AlbumImage? {
        return context.fetchPagePicture(One, offset).firstOrNull()
    }

    override fun getPicturePagingSource(): PagingSource<Int, AlbumImage> {
        return AlbumDataSource { limit, offset -> context.fetchPagePicture(limit, offset) }
    }
}
