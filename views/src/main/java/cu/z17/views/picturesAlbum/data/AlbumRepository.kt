package cu.z17.views.picturesAlbum.data

import android.content.Context
import android.net.Uri
import androidx.paging.PagingSource
import cu.z17.views.picturesAlbum.util.createCursor
import cu.z17.views.picturesAlbum.util.fetchPagePicture
import cu.z17.views.utils.galleryUtility.interactor.GalleryPicker

private const val Zero = 0
private const val One = 1

class ImageRepository(private val context: Context) {

    private val galleryPicker = GalleryPicker(ctx = context)

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
        return AlbumDataSource { limit, offset ->
            galleryPicker.getImages(limit, offset).mapNotNull {
                try {
                    AlbumImage(
                        uri = Uri.parse(it.DATA),
                        dateTaken = it.DATE_ADDED?.toLongOrNull(),
                        displayName = it.DISPLAY_NAME,
                        id = it.ID?.toLongOrNull(),
                        folderName = it.ALBUM_NAME
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}
