package cu.z17.views.album.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cu.z17.views.album.AlbumConfiguration
import cu.z17.views.album.data.AlbumImage
import cu.z17.views.album.data.AlbumOption
import cu.z17.views.album.data.PluckRepositoryImpl
import cu.z17.views.album.theme.AlbumDimens
import cu.z17.views.album.util.AlbumUriManager
import cu.z17.views.album.util.AlbumViewModelFactory
import cu.z17.views.picture.Z17BasePicture

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun Z17ModalAlbum(
    modifier: Modifier = Modifier,
    albumConfiguration: AlbumConfiguration = AlbumConfiguration(),
    onPhotoSelected: (List<AlbumImage>) -> Unit,
    state: ModalBottomSheetState,
    optionsList: List<AlbumOption> = emptyList(),
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

    ModalBottomSheetLayout(
        sheetState = state,
        sheetShape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        sheetContent = {
            val newModifier = modifier.padding(AlbumDimens.HalfQuarter)
            val cameraLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                    onPhotoSelected(listOf(albumViewModel.getPluckImage()) as List<AlbumImage>)
                }

            Box(
                modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE8E8E8))
                )
            }

            LazyVerticalGrid(
                state = gridState,
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                columns = GridCells.Fixed(Common.Three)
            ) {
                if (allowCamera)
                    item {
                        CameraIcon(
                            modifier = newModifier,
                            cameraLauncher = cameraLauncher,
                            albumViewModel = albumViewModel
                        )
                    }

                itemsIndexed(optionsList) { index, option ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = modifier
                            .size(AlbumDimens.Sixteen)
                            .clickable { option.function() }
                            .then(Modifier.background(MaterialTheme.colorScheme.background))
                    ) {
                        Z17BasePicture(
                            source = option.preview,
                            contentScale = ContentScale.Crop,
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
        }
    ) {}
}