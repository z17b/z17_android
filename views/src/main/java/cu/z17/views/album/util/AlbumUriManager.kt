package cu.z17.views.album.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import cu.z17.views.album.data.AlbumImage

class AlbumUriManager(private val context: Context) {

    private val photoCollection by lazy {
        if (Build.VERSION.SDK_INT > 28) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }

    fun getNewUri() = context.contentResolver.insert(photoCollection, setupPhotoDetails())

    /**
     * generates a new pluck image
     */
    fun getPluckImage(uri: Uri?): AlbumImage? = uri?.let {
        AlbumImage(
            it,
            System.currentTimeMillis(),
            setupPhotoDetails().getAsString(MediaStore.Images.Media.DISPLAY_NAME), null, null
        )
    }

    /**
     * generates a photo detail
     */
    private fun setupPhotoDetails() = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, getFileName())
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    private fun getFileName() = "pluck-camera-${System.currentTimeMillis()}.jpg"
}
