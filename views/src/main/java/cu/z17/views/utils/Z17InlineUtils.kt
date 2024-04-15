package cu.z17.views.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.pow
import kotlin.math.roundToInt


fun String.isAnUrl(): Boolean {
    val isMatch1: Boolean =
        Pattern.compile("^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$")
            .matcher(this)
            .find()

    val isMatch2 =
        Pattern.compile("^[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$")
            .matcher(this)
            .find()

    return isMatch1 || isMatch2//urlRegexHTPP.matches(this) || urlRegexNoHTTP.matches(this)
}

fun String.isAMention(): Boolean {
    val isMatch1: Boolean =
        Pattern.compile("@[A-Za-z0-9]+")
            .matcher(this)
            .find()

    return isMatch1
}

fun String.isAPhoneNUmber(): Boolean {
    val isMatch1: Boolean =
        Pattern.compile("""^\+?\d{1,3}[-.\s]?(\d{2,3}[-.\s]?){2}\d{3,}\D*$""")
            .matcher(this)
            .find()

    return isMatch1
}

fun String.isTag(): Boolean {
    return this.startsWith("#")
}

fun Color.lighter(amount: Float = 0.1f): Color {
    var red = this.red
    var green = this.green
    var blue = this.blue
    val alpha = this.alpha

    red += ((1f - red) * amount)
    green += ((1f - green) * amount)
    blue += ((1f - blue) * amount)

    return Color(red, green, blue, alpha)
}

fun Color.darker(amount: Float = 0.1f): Color {
    val red = (this.red * (1 - amount)).coerceIn(0f, 1f)
    val green = (this.green * (1 - amount)).coerceIn(0f, 1f)
    val blue = (this.blue * (1 - amount)).coerceIn(0f, 1f)
    return Color(red, green, blue, alpha)
}

fun Modifier.cristalBlur(radius: Dp = 28.dp) = this
    .blur(
        radius = radius,
        edgeTreatment = BlurredEdgeTreatment.Unbounded
    )
    .background(
        Brush.radialGradient(
            listOf(
                Color(0x12FFFFFF),
                Color(0xDFFFFFFF),
                Color(0x9FFFFFFF)

            ),
            radius = 2200f,
            center = Offset.Infinite
        )
    )

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}


fun String.hasMarkdown(): Boolean {
    if (!contains("# ")) return false

    val m0 = "/\n[0-9]+\\.(.*)/" // "elf::ol_list"                    // ol lists
    val m1 = "/(#+)(.*)/" // "self::header",                           // headers
    val m2 = "/\\[([^\\[]+)\\]\\(([^\\)]+)\\)/" // "<a href=\"\2\">\1</a>",  // links
    val m3 = "/(\\*\\*|__)(.*?)\\1/" // "<strong>\2</strong>",            // bold
    val m5 = "/(\\*|_)(.*?)\\1/" // "<em>\2</em>",                       // emphasis
    val m4 = "/<\\/blockquote><blockquote>/" // "\n"                    // fix extra blockquote
    val m6 = "/\\~\\~(.*?)\\~\\~/" // "<del>\1</del>",                     // del
    val m7 = "/\\:\"(.*?)\"\\:/"// "<q>\1</q>",                         // quote
    val m8 = "/`(.*?)`/" // "<code>\1</code>",                         // inline code
    val m9 = "/\n\\*(.*)/" // "elf::ul_list"                          // ul lists
    val m11 = "/\n(&gt;|\\>)(.*)/" // "elf::blockquote "               // blockquotes
    val m12 = "/\n-{5,}/" // "\n<hr />",                                // horizontal rule
    val m13 = "/\n([^\n]+)\n/" // "elf::para"                         // add paragraphs
    val m14 = "/<\\/ul>\\s?<ul>/" // ",                                  // fix extra ul
    val m15 = "/<\\/ol>\\s?<ol>/" // ",                                  // fix extra ol

    val matchs = listOf(
        m1, m2, m3, m4, m5, m6, m7, m8, m9, m0, m11, m12, m13, m14, m15
    )

    var result = false

    matchs.forEach {
        result = result || Pattern.compile("")
            .matcher(this)
            .find()
    }

    return result
}

fun String.hasLatex(): Boolean {
    val isMatch1: Boolean =
        Pattern.compile("(?s)\\\\\\[.*?\\\\]|\\$\\$.*?\\$\\$|\\\\\\(.*?\\\\\\)")
            .matcher(this)
            .find()

    return isMatch1
}

fun String.hasHtml(): Boolean {
    val isMatch1: Boolean =
        Pattern.compile("\\<(.*?)\\>")
            .matcher(this)
            .find()

    return isMatch1
}

private fun nameInitials(text: String): String {
    if (text.isEmpty())
        return "?"

    var result = ""

    var ascii = text

    val re = Regex("[^A-Za-z0-9 ]")
    ascii = re.replace(ascii, "")

    val list = ascii.split(" ")
    if (list.size > 1) {
        list.asSequence()
            .filterNot { it.isEmpty() }
            .map {

                return@map it.get(0).toString()

            }
            .forEach {
                result += it
            }

    } else {
        result = ascii
    }

    return if (result.length > 2) result.substring(0, 2)
        .uppercase(Locale.ROOT) else result.uppercase(
        Locale.ROOT
    )
}

fun String.asBitmap(
    textColor: Long,
    height: Int,
    width: Int,
    circle: Boolean = true,
): Bitmap {
    try {
        val textInitials = nameInitials(this)

        // text paint settings
        val fontSize = (width.coerceAtMost(height) / 3)
        val textPaint = Paint()
        textPaint.color = android.graphics.Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.isFakeBoldText = false
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = fontSize.toFloat()
        textPaint.textAlign = Paint.Align.CENTER

        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(image)

        if (circle)
            canvas.drawCircle(
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                (width / 2).toFloat(),
                Paint().apply {
                    color = textColor.toInt()
                })
        else
            canvas.drawRect(
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                Paint().apply {
                    color = textColor.toInt()
                })

        canvas.drawText(
            textInitials,
            (width / 2).toFloat(),
            height / 2 - (textPaint.descent() + textPaint.ascent()) / 2,
            textPaint
        )

        return image
    } catch (e: Exception) {
        return Bitmap.createBitmap(15, 15, Bitmap.Config.ARGB_8888)
    }
}

fun Long.convertToMS(): String {
    val minutes = this / 60
    val hours = minutes / 60

    return if (hours > 0) {
        "${hours}h ${(minutes % 60)}m ${(this % 60)}s"
    } else if (minutes > 0) {
        "${minutes}m ${(this % 60)}s"
    } else {
        "${this}s"
    }
}

fun Float.convertToMS(): String {
    val actual = this.toLong()

    return actual.convertToMS()
}

val sizeName = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

fun Long.convertToByteSize(): String {
    if (this <= 0) return "0 B"
    val i =
        kotlin.math.floor(kotlin.math.log10(this.toDouble()) / kotlin.math.log10(1024.0)).toInt()
    val p = 1024.0.pow(i.toDouble())
    val s = (this / p).roundToInt()
    return "%s %s".format(s, sizeName[i])
}

/**
 * Convert bitmap to byte array using ByteBuffer.
 */
fun Bitmap.convertToByteArray(): ByteArray {
    //minimum number of bytes that can be used to store this bitmap's pixels
    val size = this.byteCount

    //allocate new instances which will hold bitmap
    val buffer = ByteBuffer.allocate(size)
    val bytes = ByteArray(size)

    //copy the bitmap's pixels into the specified buffer
    this.copyPixelsToBuffer(buffer)

    //rewinds buffer (buffer position is set to zero and the mark is discarded)
    buffer.rewind()

    //transfer bytes from buffer into the given destination array
    buffer.get(bytes)

    //return bitmap's pixels
    return bytes
}

suspend fun File.getBounds(): Pair<Int, Int> {
    return withContext(Dispatchers.IO) {
        var imageHeight = 0//options.outHeight
        var imageWidth = 0//options.outWidth
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(this@getBounds.absolutePath, options)
            imageHeight = options.outHeight
            imageWidth = options.outWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext imageWidth to imageHeight
    }
}

suspend fun Uri.getBounds(): Pair<Int, Int> {
    return withContext(Dispatchers.IO) {
        var imageHeight = 0//options.outHeight
        var imageWidth = 0//options.outWidth
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(File(this@getBounds.path).absolutePath, options)
            imageHeight = options.outHeight
            imageWidth = options.outWidth
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext imageWidth to imageHeight
    }
}