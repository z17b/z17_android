package cu.z17.views.label

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun Z17Label(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onBackground,
    maxLines: Int = -1,
    maxLength: Int = -1
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            append(
                if (maxLength == -1 || text.length < maxLength) text else "${
                    text.substring(
                        0,
                        maxLength - 1
                    )
                }..."
            )
        },
        inlineContent = inlineContent,
        style = style,
        color = color,
        maxLines = if (maxLines != -1) maxLines else Int.MAX_VALUE,
        overflow = if (maxLines != -1) TextOverflow.Ellipsis else TextOverflow.Clip
    )
}

@Composable
fun Z17Label(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onBackground,
    maxLines: Int = -1,
    maxLength: Int = -1
) {
    Text(
        modifier = modifier,
        text = if (maxLength == -1 || text.length < maxLength) text else "${
            text.substring(
                0,
                maxLength - 1
            )
        }...",
        style = style,
        color = color,
        maxLines = if (maxLines != -1) maxLines else Int.MAX_VALUE,
        overflow = if (maxLines != -1) TextOverflow.Ellipsis else TextOverflow.Clip
    )
}

@Composable
fun Z17LabelWithShadow(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onBackground,
    maxLines: Int = -1,
    maxLength: Int = -1,
    shadowColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Box(modifier = modifier) {
        Z17Label(
            text = text,
            color = shadowColor,
            style = style,
            maxLength = maxLength,
            maxLines = maxLines,
            modifier = Modifier
                .fillMaxWidth()
                .offset(
                    x = 0.7.dp,
                    y = 0.7.dp
                )
                .alpha(0.75f)
        )

        Z17Label(
            text = text,
            color = color,
            style = style,
            maxLength = maxLength,
            maxLines = maxLines,
            modifier = Modifier.fillMaxWidth()
        )
    }
}