package cu.z17.views.picturesAlbum.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.picturesAlbum.data.AlbumConfiguration
import cu.z17.views.picturesAlbum.data.AlbumImage
import cu.z17.views.picturesAlbum.data.AlbumOption
import cu.z17.views.picturesAlbum.data.ImageRepository
import cu.z17.views.picturesAlbum.theme.AlbumDimens
import cu.z17.views.picturesAlbum.theme.AlbumDimens.HalfQuarter
import cu.z17.views.picturesAlbum.ui.Common.One

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Z17RowAlbum(
    modifier: Modifier = Modifier,
    albumConfiguration: AlbumConfiguration = AlbumConfiguration(),
    onPhotoSelected: (List<AlbumImage>) -> Unit,
    optionsList: List<AlbumOption> = emptyList(),
    albumViewModel: AlbumViewModel = viewModel(
        factory = AlbumViewModelFactory(
            ImageRepository(
                LocalContext.current,
            )
        ),
        key = "1"
    ),
    size: Dp = AlbumDimens.Ten,
) {
    val listState = rememberLazyGridState()

    val lazyAlbumImages: LazyPagingItems<AlbumImage> =
        albumViewModel.getImages().collectAsLazyPagingItems()

    val selectedList by albumViewModel.selectedList.value.collectAsState(initial = emptyList())

    LazyHorizontalGrid(
        state = listState,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
        rows = GridCells.Fixed(One)
    ) {
        itemsIndexed(optionsList) { _, option ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size)
                    .clickable { option.function() }
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Z17BasePicture(
                    source = option.preview,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(size / 3),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                )
            }
        }

        items(lazyAlbumImages.itemCount) { index ->
            lazyAlbumImages[index]?.let { item ->
                AlbumImageItem(
                    modifier = Modifier
                        .padding(HalfQuarter),
                    size = size,
                    albumImage = item,
                    multipleImagesAllowed = albumConfiguration.multipleImagesAllowed,
                    isSelected = selectedList.contains(item),
                    onSelectedPhoto = { image ->
                        if (!albumConfiguration.multipleImagesAllowed) {
                            onPhotoSelected(arrayListOf(image))
                        } else {
                            if (selectedList.contains(item)) {
                                onPhotoSelected(albumViewModel.selectedList.removeAll { it.id == image.id })
                            } else {
                                onPhotoSelected(albumViewModel.selectedList.add(item))
                            }
                        }
                    }
                )
            }
        }
    }
}

