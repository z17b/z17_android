package cu.z17.views.picturesAlbum.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cu.z17.views.check.Z17Check
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.picturesAlbum.data.AlbumImage
import cu.z17.views.picturesAlbum.theme.AlbumDimens

object Common {
    const val One = 1
    const val Three = 3
}

@Composable
fun AlbumImageItem(
    modifier: Modifier,
    albumImage: AlbumImage,
    multipleImagesAllowed: Boolean,
    isSelected: Boolean,
    onSelectedPhoto: (AlbumImage) -> Unit,
    size: Dp = AlbumDimens.Ten,
) {
    Box(
        modifier = modifier
            .clickable {
                onSelectedPhoto(albumImage)
            }
            .size(size)
            .scale(1F)
            .border(if (isSelected) 2.dp else 0.dp, color = MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        Z17BasePicture(
            source = albumImage.uri,
            description = "image from gallery",
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )

        if (multipleImagesAllowed)
            Z17Check(
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .padding(3.dp),
                checked = isSelected,
                onCheckedChange = {
                    onSelectedPhoto(albumImage)
                }
            )
    }
}