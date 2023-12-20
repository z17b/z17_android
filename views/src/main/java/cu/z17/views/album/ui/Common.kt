package cu.z17.views.album.ui

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import cu.z17.views.album.AlbumConfiguration
import cu.z17.views.album.data.AlbumImage
import cu.z17.views.album.theme.AlbumDimens
import cu.z17.views.album.ui.Common.One
import cu.z17.views.picture.Z17BasePicture
import kotlinx.coroutines.flow.StateFlow

object Common {
    const val One = 1
    const val Select = "SELECT"
    const val Three = 3
}


@Composable
internal fun CameraIcon(
    modifier: Modifier,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    albumViewModel: AlbumViewModel,
) {
    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .size(AlbumDimens.Sixteen)
            .clickable { handleCamera(albumViewModel, cameraLauncher) }
            .then(Modifier.background(MaterialTheme.colorScheme.background))) {
        Image(
            imageVector = Icons.Outlined.Camera,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(AlbumDimens.Six)
                .alpha(0.2F)
        )
    }
}

private fun handleCamera(
    albumViewModel: AlbumViewModel,
    onPhotoClicked: ManagedActivityResultLauncher<Uri, Boolean>,
) {
    onPhotoClicked.launch(albumViewModel.getCameraImageUri())
}

@Composable
internal fun AlbumImageView(
    modifier: Modifier,
    albumImage: AlbumImage,
    selectedImages: StateFlow<List<AlbumImage>>,
    albumConfiguration: AlbumConfiguration,
    onSelectedPhoto: (AlbumImage, isSelected: Boolean) -> Unit,
) {
    val selected = remember { mutableStateOf(false) }
    val images by selectedImages.collectAsStateWithLifecycle(emptyList())
    val backgroundColor = if (selected.value) Color.Black else Color.Transparent

    Box(
        modifier = modifier.size(AlbumDimens.Sixteen),
        contentAlignment = Alignment.Center,
    ) {
        Z17BasePicture(
            source = albumImage.uri,
            description = "image from gallery",
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .clickable {
                    if (!albumConfiguration.multipleImagesAllowed) {
                        onSelectedPhoto(albumImage, true)
                    } else {
                        selected.value = !selected.value
                        onSelectedPhoto(albumImage, selected.value)
                    }
                }
                .fillMaxSize()
                .alpha(0.5F)
                .background(color = backgroundColor),
        ) {
            AlbumImageIndicator(
                text = images.indexOf(albumImage).plus(One).toString()
            )
        }
    }
}

@Composable
internal fun AlbumImageIndicator(modifier: Modifier = Modifier, text: String) {
    if (text.toInt() > 0) {
        Icon(
            modifier = modifier.size(15.dp),
            imageVector = Icons.Outlined.CheckCircleOutline, contentDescription = "check"
        )
    }
}
