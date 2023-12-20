package cu.z17.views.album.ui

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cu.z17.views.album.AlbumConfiguration
import cu.z17.views.album.data.AlbumImage
import cu.z17.views.album.data.PluckRepositoryImpl
import cu.z17.views.album.theme.AlbumDimens
import cu.z17.views.album.theme.AlbumDimens.HalfQuarter
import cu.z17.views.album.ui.Common.Select
import cu.z17.views.album.ui.Common.Three
import cu.z17.views.album.util.AlbumUriManager
import cu.z17.views.album.util.AlbumViewModelFactory
import cu.z17.views.picture.Z17BasePicture

/**
 * Doc
 * val permissionsState =
rememberMultiplePermissionsState(
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
listOf(
Manifest.permission.CAMERA,
Manifest.permission.READ_MEDIA_IMAGES,
Manifest.permission.READ_MEDIA_VIDEO
)
} else {
listOf(
Manifest.permission.CAMERA,
Manifest.permission.READ_EXTERNAL_STORAGE
)
}
)

LaunchedEffect(key1 = false) {
viewModel.resetNavigation()

if (!permissionsState.allPermissionsGranted) {
permissionsState.launchMultiplePermissionRequest()
}

}


if (permissionsState.allPermissionsGranted) {
Pluck(
modifier = Modifier.fillMaxSize(),
pluckConfiguration = PluckConfiguration(true),
onPhotoSelected = {
}
)
} else {

}*/

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Z17FullScreenAlbum(
    modifier: Modifier = Modifier,
    albumConfiguration: AlbumConfiguration = AlbumConfiguration(),
    onPhotoSelected: (List<AlbumImage>) -> Unit,
    optionsList: List<Any> = emptyList(),
    optionsFunctions: List<() -> Unit> = emptyList(),
    allowCamera: Boolean = true
) {
    val context = LocalContext.current
    val gridState: LazyGridState = rememberLazyGridState()

    val albumViewModel: AlbumViewModel = viewModel(
        factory = AlbumViewModelFactory(
            PluckRepositoryImpl(
                context,
            ),
            AlbumUriManager(context),
            albumConfiguration
        )
    )

    val lazyAlbumImages: LazyPagingItems<AlbumImage> =
         albumViewModel.getImages().collectAsLazyPagingItems()

    Scaffold(floatingActionButton = {

        ExtendedFloatingActionButton(
            modifier = Modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            text = { Text(text = Select) },
            onClick = { onPhotoSelected(albumViewModel.selectedImage.value) },
            icon = { Icon(Icons.Rounded.Check, "fab-icon") }
        )
    }, content = {
        val newModifier = modifier.padding(HalfQuarter)
        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                onPhotoSelected(listOf(albumViewModel.getPluckImage()) as List<AlbumImage>)
            }

        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            columns = GridCells.Fixed(Three)
        ) {
            if (allowCamera)
                item {
                    CameraIcon(
                        modifier = newModifier,
                        cameraLauncher = cameraLauncher,
                        albumViewModel = albumViewModel
                    )
                }

            itemsIndexed(optionsList) { index, i ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .size(AlbumDimens.Sixteen)
                        .clickable { optionsFunctions[index]() }
                        .then(Modifier.background(MaterialTheme.colorScheme.background))
                ) {
                    Z17BasePicture(
                        source = i, contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(AlbumDimens.Six)
                            .alpha(0.2F)
                    )
                }
            }

            items(lazyAlbumImages.itemCount) { index ->
                lazyAlbumImages[index]?.let { pluckImage ->
                    AlbumImageView(
                        modifier = newModifier,
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
                            }
                        }
                    )
                }
            }
        }
    })
}