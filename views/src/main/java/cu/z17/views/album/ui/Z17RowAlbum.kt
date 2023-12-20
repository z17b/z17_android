package cu.z17.views.album.ui

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cu.z17.views.album.AlbumConfiguration
import cu.z17.views.album.data.AlbumImage
import cu.z17.views.album.data.AlbumOption
import cu.z17.views.album.data.PluckRepositoryImpl
import cu.z17.views.album.theme.AlbumDimens
import cu.z17.views.album.theme.AlbumDimens.HalfQuarter
import cu.z17.views.album.ui.Common.One
import cu.z17.views.album.util.AlbumUriManager
import cu.z17.views.album.util.AlbumViewModelFactory
import cu.z17.views.picture.Z17BasePicture
import java.util.UUID

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Z17RowAlbum(
    modifier: Modifier = Modifier,
    albumConfiguration: AlbumConfiguration = AlbumConfiguration(),
    onPhotoSelected: (List<AlbumImage>) -> Unit,
    optionsList: List<AlbumOption> = emptyList(),
    allowCamera: Boolean = true,
    albumViewModel: AlbumViewModel = viewModel(
        factory = AlbumViewModelFactory(
            PluckRepositoryImpl(
                LocalContext.current,
            ),
            AlbumUriManager(LocalContext.current),
            albumConfiguration
        ),
        key = UUID.randomUUID().toString()
    )
) {
    val listState = rememberLazyGridState()

    val lazyAlbumImages: LazyPagingItems<AlbumImage> =
        albumViewModel.getImages().collectAsLazyPagingItems()

    val newModifier = remember {
        modifier.padding(HalfQuarter)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            onPhotoSelected(listOf(albumViewModel.getPluckImage()) as List<AlbumImage>)
        }

    LazyHorizontalGrid(
        state = listState,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .height(AlbumDimens.Seven),
        rows = GridCells.Fixed(One)
    ) {
        if (allowCamera)
            item {
                CameraIcon(
                    modifier = newModifier,
                    cameraLauncher = cameraLauncher,
                    albumViewModel = albumViewModel
                )
            }

        itemsIndexed(optionsList) { _, option ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .size(AlbumDimens.Seven)
                    .clickable { option.function() }
                    .then(Modifier.background(MaterialTheme.colorScheme.background))
            ) {
                Z17BasePicture(
                    source = option.preview,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(AlbumDimens.Seven)
                        .alpha(0.2F),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        }

        items(lazyAlbumImages.itemCount) { index ->
            lazyAlbumImages[index]?.let { pluckImage ->
                AlbumImageView(
                    modifier = newModifier.size(AlbumDimens.Seven),
                    albumImage = pluckImage,
                    albumConfiguration = albumConfiguration,
                    selectedImages = albumViewModel.selectedImage,
                    onSelectedPhoto = { image, isSelected ->
                        if (!albumConfiguration.multipleImagesAllowed) {
                            onPhotoSelected(arrayListOf(image))
                        } else {
                            albumViewModel.isPhotoSelected(
                                albumImage = image,
                                isSelected = isSelected
                            )
                            onPhotoSelected(albumViewModel.selectedImageList)
                        }
                    }
                )
            }
        }
    }
}

