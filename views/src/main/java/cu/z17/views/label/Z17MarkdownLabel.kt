package cu.z17.views.label

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import cu.z17.views.utils.Z17Markown

@Composable
fun Z17MarkdownLabel(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    AndroidView(
        modifier = modifier,
        update = { Z17Markown.getInstance().markwon.setMarkdown(it, text) },
        factory = { c ->
            val spacingReady =
                java.lang.Float.max(
                    style.lineHeight.value - style.fontSize.value - 3f,
                    0f
                )
            val extraSpacing = spToPx(spacingReady.toInt(), c)
            val gravity = when (style.textAlign) {
                TextAlign.Center -> Gravity.CENTER
                TextAlign.End -> Gravity.END
                else -> Gravity.START
            }

            val tV = TextView(c).apply {
                textSize = style.fontSize.value
                setLineSpacing(extraSpacing, 1f)
                setTextColor(style.color.toArgb())
                setGravity(gravity)

                this.setOnClickListener {
                    onClick()
                }

                this.setOnLongClickListener {
                    onLongClick()
                    true
                }
            }

            Z17Markown.createInstance { Z17Markown(context = c) }.markwon.setMarkdown(tV, text)

            tV
        })
}

fun spToPx(sp: Int, context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics
)