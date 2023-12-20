package cu.z17.views.label

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun Z17ClickableLabel(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    text: String,
    onClick: (Z17ClickableLabelClickType, String) -> Unit,
    onLongClick: () -> Unit = {},
    maxLines: Int = -1,
    maxLength: Int = -1,
    linkColor: Color = Color(0xFF0088CC),
    mentionColor: Color = Color(0xFFb22f1f),
) {
    val styledMessage = messageFormatter(
        text = if (maxLength == -1 || text.length < maxLength) text else "${
            text.substring(
                0,
                maxLength - 1
            )
        }...",
        mentionColor = mentionColor,
        linkColor = linkColor,
        backgroundColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1F),
        textSize = style.fontSize.value
    )

    LongClickableText(
        modifier = modifier,
        text = styledMessage,
        style = style,
        maxLines = if (maxLines != -1) maxLines else Int.MAX_VALUE,
        overflow = if (maxLines != -1) TextOverflow.Ellipsis else TextOverflow.Clip,
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        Z17ClickableLabelClickType.LINK.name -> {
                            onClick(
                                Z17ClickableLabelClickType.LINK,
                                annotation.item
                            )

                            return@LongClickableText
                        }

                        Z17ClickableLabelClickType.MENTION.name -> {
                            onClick(
                                Z17ClickableLabelClickType.MENTION,
                                annotation.item
                            )

                            return@LongClickableText
                        }

                        Z17ClickableLabelClickType.NUMBER.name -> {
                            onClick(
                                Z17ClickableLabelClickType.NUMBER,
                                annotation.item
                            )

                            return@LongClickableText
                        }

                        Z17ClickableLabelClickType.TAG.name -> {
                            onClick(
                                Z17ClickableLabelClickType.TAG,
                                annotation.item
                            )

                            return@LongClickableText
                        }

                        else -> {
                            onClick(
                                Z17ClickableLabelClickType.REGULAR,
                                annotation.item
                            )

                            return@LongClickableText
                        }
                    }
                }

            onClick(
                Z17ClickableLabelClickType.REGULAR,
                ""
            )
        },
        onLongClick = onLongClick
    )
}

@Composable
private fun LongClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: (Int) -> Unit,
    onLongClick: () -> Unit = {}
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures(
            onTap = { pos ->
                layoutResult.value?.let { layoutResult ->
                    onClick(layoutResult.getOffsetForPosition(pos))
                }
            },
            onLongPress = {
                onLongClick()
            }
        )
    }

    BasicText(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        }
    )
}