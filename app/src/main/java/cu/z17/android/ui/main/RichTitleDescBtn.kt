package cu.z17.android.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cu.z17.views.button.Z17PrimaryDialogButton
import cu.z17.views.label.Z17ClickableLabel
import cu.z17.views.label.Z17ClickableLabelClickType
import cu.z17.views.label.Z17Label
import cu.z17.views.label.Z17MarkdownLabel
import cu.z17.views.picture.Z17BasePicture

@Composable
fun RichTitleDescBtn(image: Any, title: String, content: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .clickable { onClick() }
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        val context = LocalContext.current

        Box(
            contentAlignment = Alignment.Center
        ) {
            Z17BasePicture(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                source = image,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Z17Label(
                text = title, style = MaterialTheme.typography.bodyLarge
            )
            TextContent(msgContent = content, onClick = { p1, p2 ->

            }, onLongClick = { p1, p2 ->

            }, onMsgEntityClick = { messageAction, value ->
                when (messageAction) {
                    MessageAction.MENTION_CLICK -> {

                    }

                    MessageAction.TAG_CLICK -> {

                    }

                    MessageAction.NUMBER_CLICK -> {

                    }

                    MessageAction.LINK_CLICK -> {

                    }

                    else -> {
                    }
                }
            })
        }
    }
}

@Composable
fun TextContent(
    msgContent: String,
    onClick: (MessageAction, Int) -> Unit,
    onLongClick: (MessageAction, Int) -> Unit,
    maxPossibleLines: Int = 8,
    onMsgEntityClick: (MessageAction, String) -> Unit = { _, _ -> },
) {
    Column {
        var fullSize by remember {
            mutableStateOf((msgContent.length < maxPossibleLines * 40) || msgContent.lines().size < maxPossibleLines)
        }

        if (false) {
            Z17MarkdownLabel(
                modifier = Modifier
                    .heightIn(
                        0.dp,
                        if (fullSize) Dp.Unspecified else 100.dp
                    ),
                text = msgContent,
                style = MaterialTheme.typography.bodyLarge,
                onClick = {
                    onClick(MessageAction.REGULAR_CLICK, 0)
                },
                onLongClick = {
                    onLongClick(MessageAction.REGULAR_LONG_CLICK, 0)
                }
            )
        } else {
            Z17ClickableLabel(
                text = msgContent,
                style = MaterialTheme.typography.bodyLarge,
                onClick = { type, value ->
                    when (type) {
                        Z17ClickableLabelClickType.LINK -> {
                            onMsgEntityClick(MessageAction.LINK_CLICK, value)
                        }

                        Z17ClickableLabelClickType.NUMBER -> {
                            onMsgEntityClick(MessageAction.NUMBER_CLICK, value)
                        }

                        Z17ClickableLabelClickType.TAG -> {
                            onMsgEntityClick(MessageAction.TAG_CLICK, value)
                        }

                        Z17ClickableLabelClickType.MENTION -> {
                            onMsgEntityClick(MessageAction.MENTION_CLICK, value)
                        }

                        else -> {
                            onClick(MessageAction.REGULAR_CLICK, 0)
                        }
                    }
                },
                onLongClick = {
                    onLongClick(MessageAction.REGULAR_LONG_CLICK, 0)
                },
                maxLines = if (fullSize) Int.MAX_VALUE else 12
            )
        }

        if (!fullSize)
            Z17PrimaryDialogButton(
                simple = true,
                onClick = {
                    fullSize = true
                },
                text = "leermas"
            )
    }
}

enum class MessageAction {
    OPEN_ATTACHMENT_PREVIEW,
    PLAY_AUDIO_FILE,
    STOP_AUDIO_FILE,
    CHECK_CONTACT,
    CHECK_EVENT,
    OPEN_FILE,
    SEE_LOCATION,
    CLICK_ON_BUTTON,
    REGULAR_CLICK,
    REGULAR_LONG_CLICK,
    MENTION_CLICK,
    TAG_CLICK,
    NUMBER_CLICK,
    LINK_CLICK,
    COMMAND_CLICK
}

@Composable
fun TitleDescBtn(
    image: Any,
    title: String,
    content: String = "",
    color: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit = {},
) {
    Row(modifier = Modifier
        .clickable { onClick() }
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Z17BasePicture(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                source = image,
                colorFilter = ColorFilter.tint(color)
            )
        }
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Z17Label(
                text = title, style = MaterialTheme.typography.bodyLarge, color = color
            )

            if (content.isNotBlank()) Z17Label(
                text = content, color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
