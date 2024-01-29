package cu.z17.views.videoEditor.sections.filter

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.ClippingConfiguration
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.ChannelMixingAudioProcessor
import androidx.media3.common.audio.ChannelMixingMatrix
import androidx.media3.common.audio.SonicAudioProcessor
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BitmapOverlay
import androidx.media3.effect.Contrast
import androidx.media3.effect.DrawableOverlay
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram
import androidx.media3.effect.HslAdjustment
import androidx.media3.effect.OverlayEffect
import androidx.media3.effect.OverlaySettings
import androidx.media3.effect.Presentation
import androidx.media3.effect.RgbAdjustment
import androidx.media3.effect.RgbFilter
import androidx.media3.effect.RgbMatrix
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.effect.SingleColorLut
import androidx.media3.effect.TextOverlay
import androidx.media3.effect.TextureOverlay
import androidx.media3.exoplayer.audio.SilenceSkippingAudioProcessor
import androidx.media3.transformer.Composition
import androidx.media3.transformer.DefaultEncoderFactory
import androidx.media3.transformer.DefaultMuxer
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.google.common.collect.ImmutableList
import java.io.File
import java.io.IOException
import java.util.Arrays

@OptIn(UnstableApi::class)
object TransformerUtility {
    const val SHOULD_REMOVE_AUDIO = "should_remove_audio"
    const val SHOULD_REMOVE_VIDEO = "should_remove_video"
    const val SHOULD_FLATTEN_FOR_SLOW_MOTION = "should_flatten_for_slow_motion"
    const val FORCE_AUDIO_TRACK = "force_audio_track"
    const val AUDIO_MIME_TYPE = "audio_mime_type"
    const val VIDEO_MIME_TYPE = "video_mime_type"
    const val RESOLUTION_HEIGHT = "resolution_height"
    const val SCALE_X = "scale_x"
    const val SCALE_Y = "scale_y"
    const val ROTATE_DEGREES = "rotate_degrees"
    const val TRIM_START_MS = "trim_start_ms"
    const val TRIM_END_MS = "trim_end_ms"
    const val ENABLE_FALLBACK = "enable_fallback"
    const val ABORT_SLOW_EXPORT = "abort_slow_export"
    const val HDR_MODE = "hdr_mode"
    const val AUDIO_EFFECTS_SELECTIONS = "audio_effects_selections"
    const val VIDEO_EFFECTS_SELECTIONS = "video_effects_selections"
    const val PERIODIC_VIGNETTE_CENTER_X = "periodic_vignette_center_x"
    const val PERIODIC_VIGNETTE_CENTER_Y = "periodic_vignette_center_y"
    const val PERIODIC_VIGNETTE_INNER_RADIUS = "periodic_vignette_inner_radius"
    const val PERIODIC_VIGNETTE_OUTER_RADIUS = "periodic_vignette_outer_radius"
    const val COLOR_FILTER_SELECTION = "color_filter_selection"
    const val CONTRAST_VALUE = "contrast_value"
    const val RGB_ADJUSTMENT_RED_SCALE = "rgb_adjustment_red_scale"
    const val RGB_ADJUSTMENT_GREEN_SCALE = "rgb_adjustment_green_scale"
    const val RGB_ADJUSTMENT_BLUE_SCALE = "rgb_adjustment_blue_scale"
    const val HSL_ADJUSTMENTS_HUE = "hsl_adjustments_hue"
    const val HSL_ADJUSTMENTS_SATURATION = "hsl_adjustments_saturation"
    const val HSL_ADJUSTMENTS_LIGHTNESS = "hsl_adjustments_lightness"
    const val BITMAP_OVERLAY_URI = "bitmap_overlay_uri"
    const val BITMAP_OVERLAY_ALPHA = "bitmap_overlay_alpha"
    const val TEXT_OVERLAY_TEXT = "text_overlay_text"
    const val TEXT_OVERLAY_TEXT_COLOR = "text_overlay_text_color"
    const val TEXT_OVERLAY_ALPHA = "text_overlay_alpha"

    // Video effect selections.
    const val DIZZY_CROP_INDEX = 0
    const val EDGE_DETECTOR_INDEX = 1
    const val COLOR_FILTERS_INDEX = 2
    const val MAP_WHITE_TO_GREEN_LUT_INDEX = 3
    const val RGB_ADJUSTMENTS_INDEX = 4
    const val HSL_ADJUSTMENT_INDEX = 5
    const val CONTRAST_INDEX = 6
    const val PERIODIC_VIGNETTE_INDEX = 7
    const val SPIN_3D_INDEX = 8
    const val ZOOM_IN_INDEX = 9
    const val OVERLAY_LOGO_AND_TIMER_INDEX = 10
    const val BITMAP_OVERLAY_INDEX = 11
    const val TEXT_OVERLAY_INDEX = 12

    // Audio effect selections.
    const val HIGH_PITCHED_INDEX = 0
    const val SAMPLE_RATE_INDEX = 1
    const val SKIP_SILENCE_INDEX = 2
    const val CHANNEL_MIXING_INDEX = 3
    const val VOLUME_SCALING_INDEX = 4

    // Color filter options.
    const val COLOR_FILTER_GRAYSCALE = 0
    const val COLOR_FILTER_INVERTED = 1
    const val COLOR_FILTER_SEPIA = 2


    private fun createMediaItem(bundle: Bundle?, uri: Uri): MediaItem {
        val mediaItemBuilder = MediaItem.Builder().setUri(uri)
        if (bundle != null) {
            val trimStartMs = bundle.getLong(
                TRIM_START_MS,  /* defaultValue= */
                C.TIME_UNSET
            )
            val trimEndMs = bundle.getLong(
                TRIM_END_MS,  /* defaultValue= */
                C.TIME_UNSET
            )
            if (trimStartMs != C.TIME_UNSET && trimEndMs != C.TIME_UNSET) {
                mediaItemBuilder.setClippingConfiguration(
                    ClippingConfiguration.Builder()
                        .setStartPositionMs(trimStartMs)
                        .setEndPositionMs(trimEndMs)
                        .build()
                )
            }
        }
        return mediaItemBuilder.build()
    }

    private fun createTransformer(bundle: Bundle?, context: Context): Transformer {
        val transformerBuilder = Transformer.Builder(context)
        if (bundle != null) {
            val audioMimeType = bundle.getString(AUDIO_MIME_TYPE)
            if (audioMimeType != null) {
                transformerBuilder.setAudioMimeType(audioMimeType)
            }
            val videoMimeType = bundle.getString(VIDEO_MIME_TYPE)
            if (videoMimeType != null) {
                transformerBuilder.setVideoMimeType(videoMimeType)
            }
            transformerBuilder.setEncoderFactory(
                DefaultEncoderFactory.Builder(context)
                    .setEnableFallback(bundle.getBoolean(ENABLE_FALLBACK))
                    .build()
            )
            if (!bundle.getBoolean(ABORT_SLOW_EXPORT)) {
                transformerBuilder.setMuxerFactory(
                    DefaultMuxer.Factory(C.TIME_UNSET)
                )
            }
        }
        return transformerBuilder
            .addListener(
                object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        // todo
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException,
                    ) {

                    }
                })
            .build()
    }

    private fun createComposition(
        mediaItem: MediaItem,
        bundle: Bundle?,
        context: Context,
    ): Composition {
        val editedMediaItemBuilder = EditedMediaItem.Builder(mediaItem)
        // For image inputs. Automatically ignored if input is audio/video.
        editedMediaItemBuilder.setDurationUs(5000000).setFrameRate(30)
        if (bundle != null) {
            val audioProcessors = createAudioProcessorsFromBundle(bundle)
            val videoEffects = createVideoEffectsFromBundle(bundle, context)
            editedMediaItemBuilder
                .setRemoveAudio(bundle.getBoolean(SHOULD_REMOVE_AUDIO))
                .setRemoveVideo(bundle.getBoolean(SHOULD_REMOVE_VIDEO))
                .setFlattenForSlowMotion(
                    bundle.getBoolean(SHOULD_FLATTEN_FOR_SLOW_MOTION)
                )
                .setEffects(Effects(audioProcessors, videoEffects))
        }
        val compositionBuilder =
            Composition.Builder(EditedMediaItemSequence(editedMediaItemBuilder.build()))
        if (bundle != null) {
            compositionBuilder
                .setHdrMode(bundle.getInt(HDR_MODE))
                .experimentalSetForceAudioTrack(
                    bundle.getBoolean(FORCE_AUDIO_TRACK)
                )
        }
        return compositionBuilder.build()
    }

    private fun createAudioProcessorsFromBundle(bundle: Bundle): ImmutableList<AudioProcessor> {
        val selectedAudioEffects =
            bundle.getBooleanArray(AUDIO_EFFECTS_SELECTIONS)
                ?: return ImmutableList.of()
        val processors = ImmutableList.Builder<AudioProcessor>()
        if (selectedAudioEffects[HIGH_PITCHED_INDEX]
            || selectedAudioEffects[SAMPLE_RATE_INDEX]
        ) {
            val sonicAudioProcessor = SonicAudioProcessor()
            if (selectedAudioEffects[HIGH_PITCHED_INDEX]) {
                sonicAudioProcessor.setPitch(2f)
            }
            if (selectedAudioEffects[SAMPLE_RATE_INDEX]) {
                sonicAudioProcessor.setOutputSampleRateHz(48000)
            }
            processors.add(sonicAudioProcessor)
        }
        if (selectedAudioEffects[SKIP_SILENCE_INDEX]) {
            val silenceSkippingAudioProcessor = SilenceSkippingAudioProcessor()
            silenceSkippingAudioProcessor.setEnabled(true)
            processors.add(silenceSkippingAudioProcessor)
        }
        val mixToMono = selectedAudioEffects[CHANNEL_MIXING_INDEX]
        val scaleVolumeToHalf = selectedAudioEffects[VOLUME_SCALING_INDEX]
        if (mixToMono || scaleVolumeToHalf) {
            val mixingAudioProcessor = ChannelMixingAudioProcessor()
            for (inputChannelCount in 1..6) {
                val matrix: ChannelMixingMatrix = if (mixToMono) {
                    val mixingCoefficients = FloatArray(inputChannelCount)
                    // Each channel is equally weighted in the mix to mono.
                    Arrays.fill(mixingCoefficients, 1f / inputChannelCount)
                    ChannelMixingMatrix(
                        inputChannelCount,  /* outputChannelCount= */1, mixingCoefficients
                    )
                } else {
                    // Identity matrix.
                    ChannelMixingMatrix.create(
                        inputChannelCount,  /* outputChannelCount= */inputChannelCount
                    )
                }

                // Apply the volume adjustment.
                mixingAudioProcessor.putChannelMixingMatrix(
                    if (scaleVolumeToHalf) matrix.scaleBy(0.5f) else matrix
                )
            }
            processors.add(mixingAudioProcessor)
        }
        return processors.build()
    }

    private fun createVideoEffectsFromBundle(
        bundle: Bundle,
        context: Context,
    ): ImmutableList<Effect> {
        val selectedEffects = bundle.getBooleanArray(VIDEO_EFFECTS_SELECTIONS)
            ?: return ImmutableList.of()
        val effects = ImmutableList.Builder<Effect>()
        if (selectedEffects[DIZZY_CROP_INDEX]) {
            effects.add(MatrixTransformationFactory.createDizzyCropEffect())
        }
        if (selectedEffects[EDGE_DETECTOR_INDEX]) {
            try {
                val clazz = Class.forName("androidx.media3.demo.transformer.MediaPipeShaderProgram")
                val constructor = clazz.getConstructor(
                    Context::class.java,
                    Boolean::class.javaPrimitiveType,
                    String::class.java,
                    Boolean::class.javaPrimitiveType,
                    String::class.java,
                    String::class.java
                )
                effects.add(
                    GlEffect { c: Context?, useHdr: Boolean ->
                        try {
                            return@GlEffect (constructor.newInstance(
                                c,
                                useHdr,
                                "edge_detector_mediapipe_graph.binarypb",
                                true,
                                "input_video",
                                "output_video"
                            ) as GlShaderProgram)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            throw RuntimeException("Failed to load MediaPipeShaderProgram", e)
                        }
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (selectedEffects[COLOR_FILTERS_INDEX]) {
            when (bundle.getInt(COLOR_FILTER_SELECTION)) {
                COLOR_FILTER_GRAYSCALE -> effects.add(RgbFilter.createGrayscaleFilter())
                COLOR_FILTER_INVERTED -> effects.add(RgbFilter.createInvertedFilter())
                COLOR_FILTER_SEPIA -> {
                    // W3C Sepia RGBA matrix with sRGB as a target color space:
                    // https://www.w3.org/TR/filter-effects-1/#sepiaEquivalent
                    // The matrix is defined for the sRGB color space and the Transformer library
                    // uses a linear RGB color space internally. Meaning this is only for demonstration
                    // purposes and it does not display a correct sepia frame.
                    val sepiaMatrix = floatArrayOf(
                        0.393f,
                        0.349f,
                        0.272f,
                        0f,
                        0.769f,
                        0.686f,
                        0.534f,
                        0f,
                        0.189f,
                        0.168f,
                        0.131f,
                        0f,
                        0f,
                        0f,
                        0f,
                        1f
                    )
                    effects.add(RgbMatrix { _: Long, _: Boolean -> sepiaMatrix })
                }

                else -> throw IllegalStateException(
                    "Unexpected color filter "
                            + bundle.getInt(COLOR_FILTER_SELECTION)
                )
            }
        }
        if (selectedEffects[MAP_WHITE_TO_GREEN_LUT_INDEX]) {
            val length = 3
            val mapWhiteToGreenLut = Array(length) { Array(length) { IntArray(length) } }
            val scale = 255 / (length - 1)
            for (r in 0 until length) {
                for (g in 0 until length) {
                    for (b in 0 until length) {
                        mapWhiteToGreenLut[r][g][b] = Color.rgb( /* red= */r * scale,  /* green= */
                            g * scale,  /* blue= */
                            b * scale
                        )
                    }
                }
            }
            mapWhiteToGreenLut[length - 1][length - 1][length - 1] = Color.GREEN
            effects.add(SingleColorLut.createFromCube(mapWhiteToGreenLut))
        }
        if (selectedEffects[RGB_ADJUSTMENTS_INDEX]) {
            effects.add(
                RgbAdjustment.Builder()
                    .setRedScale(bundle.getFloat(RGB_ADJUSTMENT_RED_SCALE))
                    .setGreenScale(bundle.getFloat(RGB_ADJUSTMENT_GREEN_SCALE))
                    .setBlueScale(bundle.getFloat(RGB_ADJUSTMENT_BLUE_SCALE))
                    .build()
            )
        }
        if (selectedEffects[HSL_ADJUSTMENT_INDEX]) {
            effects.add(
                HslAdjustment.Builder()
                    .adjustHue(bundle.getFloat(HSL_ADJUSTMENTS_HUE))
                    .adjustSaturation(bundle.getFloat(HSL_ADJUSTMENTS_SATURATION))
                    .adjustLightness(bundle.getFloat(HSL_ADJUSTMENTS_LIGHTNESS))
                    .build()
            )
        }
        if (selectedEffects[CONTRAST_INDEX]) {
            effects.add(Contrast(bundle.getFloat(CONTRAST_VALUE)))
        }
        if (selectedEffects[PERIODIC_VIGNETTE_INDEX]) {
            effects.add(
                GlEffect { c: Context?, useHdr: Boolean ->
                    PeriodicVignetteShaderProgram(
                        c,
                        useHdr,
                        bundle.getFloat(PERIODIC_VIGNETTE_CENTER_X),
                        bundle.getFloat(PERIODIC_VIGNETTE_CENTER_Y),  /* minInnerRadius= */
                        bundle.getFloat(
                            PERIODIC_VIGNETTE_INNER_RADIUS
                        ),  /* maxInnerRadius= */
                        bundle.getFloat(
                            PERIODIC_VIGNETTE_OUTER_RADIUS
                        ),
                        bundle.getFloat(PERIODIC_VIGNETTE_OUTER_RADIUS)
                    )
                }
            )
        }
        if (selectedEffects[SPIN_3D_INDEX]) {
            effects.add(MatrixTransformationFactory.createSpin3dEffect())
        }
        if (selectedEffects[ZOOM_IN_INDEX]) {
            effects.add(MatrixTransformationFactory.createZoomInTransition())
        }
        val overlayEffect = createOverlayEffectFromBundle(bundle, selectedEffects, context)
        if (overlayEffect != null) {
            effects.add(overlayEffect)
        }
        val scaleX = bundle.getFloat(SCALE_X,  /* defaultValue= */1f)
        val scaleY = bundle.getFloat(SCALE_Y,  /* defaultValue= */1f)
        val rotateDegrees =
            bundle.getFloat(ROTATE_DEGREES,  /* defaultValue= */0f)
        if (scaleX != 1f || scaleY != 1f || rotateDegrees != 0f) {
            effects.add(
                ScaleAndRotateTransformation.Builder()
                    .setScale(scaleX, scaleY)
                    .setRotationDegrees(rotateDegrees)
                    .build()
            )
        }
        val resolutionHeight = bundle.getInt(
            RESOLUTION_HEIGHT,  /* defaultValue= */
            C.LENGTH_UNSET
        )
        if (resolutionHeight != C.LENGTH_UNSET) {
            effects.add(Presentation.createForHeight(resolutionHeight))
        }
        return effects.build()
    }

    private fun createOverlayEffectFromBundle(
        bundle: Bundle,
        selectedEffects: BooleanArray,
        context: Context,
    ): OverlayEffect? {
        val overlaysBuilder = ImmutableList.Builder<TextureOverlay>()
        if (selectedEffects[OVERLAY_LOGO_AND_TIMER_INDEX]) {
            val logoSettings =
                OverlaySettings.Builder() // Place the logo in the bottom left corner of the screen with some padding from the
                    // edges.
                    .setOverlayFrameAnchor( /* x= */1f,  /* y= */1f)
                    .setBackgroundFrameAnchor( /* x= */-0.95f,  /* y= */-0.95f)
                    .build()
            val logo: Drawable = try {
                context.packageManager.getApplicationIcon(context.packageName)
            } catch (e: PackageManager.NameNotFoundException) {
                throw IllegalStateException(e)
            }
            logo.setBounds( /* left= */
                0,  /* top= */0, logo.intrinsicWidth, logo.intrinsicHeight
            )
            val logoOverlay: TextureOverlay =
                DrawableOverlay.createStaticDrawableOverlay(logo, logoSettings)
            val timerOverlay: TextureOverlay = TimerOverlay()
            overlaysBuilder.add(logoOverlay, timerOverlay)
        }
        if (selectedEffects[BITMAP_OVERLAY_INDEX]) {
            val overlaySettings = OverlaySettings.Builder()
                .setAlphaScale(
                    bundle.getFloat(
                        BITMAP_OVERLAY_ALPHA,  /* defaultValue= */1f
                    )
                )
                .build()
            val bitmapOverlay = BitmapOverlay.createStaticBitmapOverlay(
                context,
                Uri.parse(Assertions.checkNotNull<String>(bundle.getString(BITMAP_OVERLAY_URI))),
                overlaySettings
            )
            overlaysBuilder.add(bitmapOverlay)
        }
        if (selectedEffects[TEXT_OVERLAY_INDEX]) {
            val overlaySettings = OverlaySettings.Builder()
                .setAlphaScale(
                    bundle.getFloat(
                        TEXT_OVERLAY_ALPHA,  /* defaultValue= */
                        1f
                    )
                )
                .build()
            val overlayText = SpannableString(
                Assertions.checkNotNull<String>(bundle.getString(TEXT_OVERLAY_TEXT))
            )
            overlayText.setSpan(
                ForegroundColorSpan(bundle.getInt(TEXT_OVERLAY_TEXT_COLOR)),  /* start= */
                0,
                overlayText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val textOverlay = TextOverlay.createStaticTextOverlay(overlayText, overlaySettings)
            overlaysBuilder.add(textOverlay)
        }
        val overlays = overlaysBuilder.build()
        return if (overlays.isEmpty()) null else OverlayEffect(overlays)
    }
}