package cu.z17.views.videoEditor.sections.filter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.OverlaySettings
import androidx.media3.effect.TextOverlay
import java.util.Locale


@OptIn(UnstableApi::class)
internal class TimerOverlay : TextOverlay() {
    private val overlaySettings: OverlaySettings =
        OverlaySettings.Builder() // Place the timer in the bottom left corner of the screen with some padding from the
            // edges.
            .setOverlayFrameAnchor( /* x= */1f,  /* y= */1f)
            .setBackgroundFrameAnchor( /* x= */-0.7f,  /* y= */-0.95f)
            .build()

    override fun getText(presentationTimeUs: Long): SpannableString {
        val text = SpannableString(
            String.format(
                Locale.US,
                "%.02f",
                presentationTimeUs / C.MICROS_PER_SECOND.toFloat()
            )
        )
        text.setSpan(
            ForegroundColorSpan(Color.WHITE),  /* start= */
            0,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return text
    }

    override fun getOverlaySettings(presentationTimeUs: Long): OverlaySettings {
        return overlaySettings
    }
}