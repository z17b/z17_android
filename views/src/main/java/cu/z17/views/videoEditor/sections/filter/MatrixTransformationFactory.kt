package cu.z17.views.videoEditor.sections.filter

import android.graphics.Matrix
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.effect.GlMatrixTransformation
import androidx.media3.effect.MatrixTransformation
import kotlin.math.cos
import kotlin.math.sin

@OptIn(UnstableApi::class)
internal object MatrixTransformationFactory {
    /**
     * Returns a [MatrixTransformation] that rescales the frames over the first [ ][.ZOOM_DURATION_SECONDS] seconds, such that the rectangle filled with the input frame increases
     * linearly in size from a single point to filling the full output frame.
     */
    fun createZoomInTransition(): MatrixTransformation {
        return MatrixTransformation { obj: Long -> calculateZoomInTransitionMatrix(obj) }
    }

    /**
     * Returns a [MatrixTransformation] that crops frames to a rectangle that moves on an
     * ellipse.
     */
    fun createDizzyCropEffect(): MatrixTransformation {
        return MatrixTransformation { obj: Long -> calculateDizzyCropMatrix(obj) }
    }

    /**
     * Returns a [GlMatrixTransformation] that rotates a frame in 3D around the y-axis and
     * applies perspective projection to 2D.
     */
    fun createSpin3dEffect(): GlMatrixTransformation {
        return GlMatrixTransformation(function = { presentationTimeUs ->
            calculate3dSpinMatrix(
                presentationTimeUs
            )
        })
    }

    private const val ZOOM_DURATION_SECONDS = 2f
    private const val DIZZY_CROP_ROTATION_PERIOD_US = 1500000f
    private fun calculateZoomInTransitionMatrix(presentationTimeUs: Long): Matrix {
        val transformationMatrix = Matrix()
        val scale = Math.min(1f, presentationTimeUs / (C.MICROS_PER_SECOND * ZOOM_DURATION_SECONDS))
        transformationMatrix.postScale( /* sx= */scale,  /* sy= */scale)
        return transformationMatrix
    }

    private fun calculateDizzyCropMatrix(presentationTimeUs: Long): Matrix {
        val theta = presentationTimeUs * 2 * Math.PI / DIZZY_CROP_ROTATION_PERIOD_US
        val centerX = 0.5f * cos(theta).toFloat()
        val centerY = 0.5f * sin(theta).toFloat()
        val transformationMatrix = Matrix()
        transformationMatrix.postTranslate( /* dx= */centerX,  /* dy= */centerY)
        transformationMatrix.postScale( /* sx= */2f,  /* sy= */2f)
        return transformationMatrix
    }

    private fun calculate3dSpinMatrix(presentationTimeUs: Long): FloatArray {
        val transformationMatrix = FloatArray(16)
        android.opengl.Matrix.frustumM(
            transformationMatrix,  /* offset= */
            0,  /* left= */
            -1f,  /* right= */
            1f,  /* bottom= */
            -1f,  /* top= */
            1f,  /* near= */
            3f,  /* far= */
            5f
        )
        android.opengl.Matrix.translateM(
            transformationMatrix,  /* mOffset= */0,  /* x= */0f,  /* y= */0f,  /* z= */-4f
        )
        val theta = Util.usToMs(presentationTimeUs) / 10f
        android.opengl.Matrix.rotateM(
            transformationMatrix,  /* mOffset= */0, theta,  /* x= */0f,  /* y= */1f,  /* z= */0f
        )
        return transformationMatrix
    }
}