package cu.z17.views.videosAlbum.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cu.z17.views.check.Z17Check
import cu.z17.views.label.Z17Label
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.picturesAlbum.theme.AlbumDimens
import cu.z17.views.utils.ThumbnailGenerator
import cu.z17.views.utils.convertToMS
import cu.z17.views.videosAlbum.data.VideoAlbum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AlbumVideoItem(
    modifier: Modifier,
    videoAlbum: VideoAlbum,
    multipleImagesAllowed: Boolean,
    isSelected: Boolean,
    onSelected: (VideoAlbum) -> Unit,
    size: Dp = AlbumDimens.Ten,
    onRequestRealPath: (String) -> String,
) {
    Box(
        modifier = modifier
            .clickable {
                onSelected(videoAlbum)
            }
            .size(size)
            .scale(1F)
            .border(if (isSelected) 2.dp else 0.dp, color = MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        var thumbnailPath by remember {
            mutableStateOf<String?>(null)
        }

        Z17BasePicture(
            source = if (thumbnailPath != null) Uri.fromFile(File(thumbnailPath!!)) else null,
            placeholder = Icons.Default.VideoFile,
            description = "video from gallery",
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
                    onSelected(videoAlbum)
                }
            )

        Z17Label(
            text = ((videoAlbum.duration ?: 0L) / 1000L).convertToMS(),
            modifier = Modifier
                .align(alignment = Alignment.BottomStart)
                .padding(3.dp)
                .background(color = Color.Black, shape = RoundedCornerShape(5.dp))
                .padding(horizontal = 5.dp, vertical = 3.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )

        DisposableEffect(Unit) {
            val job = scope.launch(Dispatchers.IO) {
                val path = onRequestRealPath(videoAlbum.uri.path ?: "/")

                val possibleThumbnail =
                    ThumbnailGenerator.generateVideoThumbnailInFile(
                        videoPath = Uri.fromFile(
                            File(path)
                        ).path ?: "/",
                        context = context
                    )

                thumbnailPath = possibleThumbnail?.path
            }

            onDispose {
                job.cancel()
            }
        }
    }
}


