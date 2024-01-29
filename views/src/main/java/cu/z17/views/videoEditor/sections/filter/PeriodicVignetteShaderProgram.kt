package cu.z17.views.videoEditor.sections.filter

import android.content.Context
import android.opengl.GLES20
import androidx.annotation.OptIn
import androidx.media3.common.VideoFrameProcessingException
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.GlUtil
import androidx.media3.common.util.GlUtil.GlException
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BaseGlShaderProgram
import java.io.IOException

@OptIn(UnstableApi::class)
internal class PeriodicVignetteShaderProgram(
    context: Context?,
    useHdr: Boolean,
    centerX: Float,
    centerY: Float,
    minInnerRadius: Float,
    maxInnerRadius: Float,
    outerRadius: Float,
) :
    BaseGlShaderProgram( /* useHighPrecisionColorComponents= */useHdr,  /* texturePoolCapacity= */
        1
    ) {
    private var glProgram: GlProgram? = null
    private val minInnerRadius: Float
    private val deltaInnerRadius: Float

    /**
     * Creates a new instance.
     *
     *
     * The inner radius of the vignette effect oscillates smoothly between `minInnerRadius`
     * and `maxInnerRadius`.
     *
     *
     * The pixels between the inner radius and the `outerRadius` are darkened linearly based
     * on their distance from `innerRadius`. All pixels outside `outerRadius` are black.
     *
     *
     * The parameters are given in normalized texture coordinates from 0 to 1.
     *
     * @param context The [Context].
     * @param useHdr Whether input textures come from an HDR source. If `true`, colors will be
     * in linear RGB BT.2020. If `false`, colors will be in linear RGB BT.709.
     * @param centerX The x-coordinate of the center of the effect.
     * @param centerY The y-coordinate of the center of the effect.
     * @param minInnerRadius The lower bound of the radius that is unaffected by the effect.
     * @param maxInnerRadius The upper bound of the radius that is unaffected by the effect.
     * @param outerRadius The radius after which all pixels are black.
     * @throws VideoFrameProcessingException If a problem occurs while reading shader files.
     */
    init {
        Assertions.checkArgument(minInnerRadius <= maxInnerRadius)
        Assertions.checkArgument(maxInnerRadius <= outerRadius)
        this.minInnerRadius = minInnerRadius
        deltaInnerRadius = maxInnerRadius - minInnerRadius
        glProgram = try {
            GlProgram(context!!, VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH)
        } catch (e: IOException) {
            throw VideoFrameProcessingException(e)
        } catch (e: GlException) {
            throw VideoFrameProcessingException(e)
        }
        glProgram?.setFloatsUniform("uCenter", floatArrayOf(centerX, centerY))
        glProgram?.setFloatsUniform("uOuterRadius", floatArrayOf(outerRadius))
        // Draw the frame on the entire normalized device coordinate space, from -1 to 1, for x and y.
        glProgram?.setBufferAttribute(
            "aFramePosition",
            GlUtil.getNormalizedCoordinateBounds(),
            GlUtil.HOMOGENEOUS_COORDINATE_VECTOR_SIZE
        )
    }

    override fun configure(inputWidth: Int, inputHeight: Int): Size {
        return Size(inputWidth, inputHeight)
    }

    @Throws(VideoFrameProcessingException::class)
    override fun drawFrame(inputTexId: Int, presentationTimeUs: Long) {
        try {
            glProgram!!.use()
            glProgram!!.setSamplerTexIdUniform("uTexSampler", inputTexId,  /* texUnitIndex= */0)
            val theta = presentationTimeUs * 2 * Math.PI / DIMMING_PERIOD_US
            val innerRadius =
                minInnerRadius + deltaInnerRadius * (0.5f - 0.5f * Math.cos(theta).toFloat())
            glProgram!!.setFloatsUniform("uInnerRadius", floatArrayOf(innerRadius))
            glProgram!!.bindAttributesAndUniforms()
            // The four-vertex triangle strip forms a quad.
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,  /* first= */0,  /* count= */4)
        } catch (e: GlException) {
            throw VideoFrameProcessingException(e, presentationTimeUs)
        }
    }

    @Throws(VideoFrameProcessingException::class)
    override fun release() {
        super.release()
        try {
            glProgram!!.delete()
        } catch (e: GlException) {
            throw VideoFrameProcessingException(e)
        }
    }

    companion object {
        private const val VERTEX_SHADER_PATH = "vertex_shader_copy_es2.glsl"
        private const val FRAGMENT_SHADER_PATH = "fragment_shader_vignette_es2.glsl"
        private const val DIMMING_PERIOD_US = 5600000f
    }
}