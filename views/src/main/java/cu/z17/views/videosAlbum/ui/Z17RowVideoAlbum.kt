package cu.z17.views.videosAlbum.ui

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
import cu.z17.views.picturesAlbum.data.AlbumOption
import cu.z17.views.picturesAlbum.theme.AlbumDimens
import cu.z17.views.picturesAlbum.ui.Common
import cu.z17.views.videosAlbum.data.VideoAlbum
import cu.z17.views.videosAlbum.data.VideoAlbumConfiguration
import cu.z17.views.videosAlbum.data.VideoRepository
import cu.z17.views.videosAlbum.theme.VideoAlbumDimens.HalfQuarter

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Z17RowVideoAlbum(
    modifier: Modifier = Modifier,
    albumConfiguration: VideoAlbumConfiguration = VideoAlbumConfiguration(),
    onVideoSelected: (List<VideoAlbum>) -> Unit,
    optionsList: List<AlbumOption> = emptyList(),
    albumViewModel: VideoAlbumViewModel = viewModel(
        factory = VideoAlbumViewModelFactory(
            VideoRepository(
                LocalContext.current,
            )
        ),
        key = "1"
    ),
    size: Dp = AlbumDimens.Ten,
    onRequestRealPath: (String) -> String = { it },
) {
    val gridState: LazyGridState = rememberLazyGridState()

    val lazyAlbumImages: LazyPagingItems<VideoAlbum> =
        albumViewModel.getVideos().collectAsLazyPagingItems()

    val selectedList by albumViewModel.selectedList.value.collectAsState(initial = emptyList())

    LazyHorizontalGrid(
        state = gridState,
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        rows = GridCells.Fixed(Common.One)
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
                AlbumVideoItem(
                    modifier = Modifier
                        .padding(HalfQuarter),
                    size = size,
                    videoAlbum = item,
                    multipleImagesAllowed = albumConfiguration.multipleVideosAllowed,
                    isSelected = selectedList.contains(item),
                    onSelected = { video ->
                        if (!albumConfiguration.multipleVideosAllowed) {
                            onVideoSelected(arrayListOf(video))
                        } else {
                            if (selectedList.contains(item)) {
                                onVideoSelected(albumViewModel.selectedList.removeAll { it.id == video.id })
                            } else {
                                onVideoSelected(albumViewModel.selectedList.add(item))
                            }
                        }
                    },
                    onRequestRealPath = onRequestRealPath
                )
            }
        }
    }
}
