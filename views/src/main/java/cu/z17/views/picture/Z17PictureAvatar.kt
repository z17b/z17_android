package cu.z17.views.picture

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import cu.z17.views.utils.asBitmap
import okhttp3.Headers

@Composable
fun Z17PictureAvatar(
    modifier: Modifier = Modifier,
    source: Any?,
    placeholder: Any = Icons.Outlined.Person,
    colorFilter: ColorFilter? = null,
    description: String = "avatar image",
    filterQuality: FilterQuality = FilterQuality.High,
    size: Int? = null,
    canEdit: Boolean = false,
    customHeaders: Headers? = null,
    textPlaceholder: String = "",
    useTextPlaceholder: Boolean = false,
    defaultAvatarSize: Int = 256,
) {
    Box(
        modifier = modifier
            .aspectRatio(1F)
    ) {
        if ((source == null || (source is String && source.isBlank())) && useTextPlaceholder)
            Z17BasePicture(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize(),
                source = textPlaceholder.asBitmap(
                    textColor = if (placeholder is Color) placeholder.toArgb()
                        .toLong() else android.graphics.Color.parseColor(
                        "#9c9c9c"
                    ).toLong(),
                    height = defaultAvatarSize,
                    width = defaultAvatarSize,
                    circle = true
                ),
                contentScale = ContentScale.Crop,
                colorFilter = colorFilter,
                filterQuality = filterQuality,
                description = description,
                size = size,
                blurHash = ""
            )
        else
            Z17BasePicture(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize(),
                source = source,
                placeholder = placeholder,
                contentScale = ContentScale.Crop,
                colorFilter = colorFilter,
                filterQuality = filterQuality,
                description = description,
                size = size,
                customHeaders = customHeaders
            )

        if (canEdit)
            Z17BasePicture(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize(),
                source = cu.z17.views.R.drawable.edit_profile_banner,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
                description = "edit profile banner",
                size = size
            )
    }
}