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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import okhttp3.Headers

@Composable
fun Z17PictureAvatar(
    modifier: Modifier = Modifier,
    source: Any?,
    placeholder: Any = Icons.Outlined.Person,
    colorFilter: ColorFilter? = null,
    description: String = "avatar image",
    filterQuality: FilterQuality = FilterQuality.None,
    size: Int? = null,
    canEdit: Boolean = false,
    customHeaders: Headers? = null,
) {
    Box(
        modifier = modifier
            .aspectRatio(1F)
            .clip(CircleShape)
    ) {
        Z17BasePicture(
            modifier = Modifier
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
                    .fillMaxSize(),
                source = cu.z17.views.R.drawable.edit_profile_banner,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
                description = "edit profile banner",
                size = size
            )
    }
}