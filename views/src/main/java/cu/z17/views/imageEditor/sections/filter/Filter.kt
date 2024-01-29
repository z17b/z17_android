package cu.z17.views.imageEditor.sections.filter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cu.z17.views.picture.Z17BasePicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun Filter(
    bitmap: Bitmap,
    filteredBitmaps: List<Bitmap>,
    filterSelected: Int,
    onFiltersLoad: (List<Bitmap>) -> Unit,
) {
    Box {
        Z17BasePicture(
            modifier = Modifier
                .fillMaxSize(),
            source = if (filterSelected == -1) Color.Transparent else if (filterSelected == 0) bitmap else filteredBitmaps[filterSelected - 1],
            placeholder = MaterialTheme.colorScheme.background
        )

        LaunchedEffect(Unit) {
            if (filteredBitmaps.isEmpty()) {
                val filtered1 = getBitmapFromColorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            0.393f, 0.769f, 0.189f, 0f, 0f,
                            0.349f, 0.686f, 0.168f, 0f, 0f,
                            0.272f, 0.534f, 0.131f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f,
                        )
                    ),
                    bitmap
                )

                val filtered2 = getBitmapFromColorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            1.3f, 0f, 0f, 0f, 0f,
                            0f, 1.3f, 0f, 0f, 0f,
                            0f, 0f, 1.3f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    ),
                    bitmap
                )

                val filtered3 = getBitmapFromColorMatrix(
                    ColorMatrix().apply {
                        setSaturation(1.5F)
                    },
                    bitmap
                )

                val filtered4 = getBitmapFromColorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 100F,
                            0f, 1f, 0f, 0f, 100F,
                            0f, 0f, 1f, 0f, 100F,
                            0f, 0f, 0f, 1f, 0f
                        )
                    ),
                    bitmap
                )

                val filtered5 = getBitmapFromColorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, -100F,
                            0f, 1f, 0f, 0f, -100F,
                            0f, 0f, 1f, 0f, -100F,
                            0f, 0f, 0f, 1f, 0f
                        )
                    ),
                    bitmap
                )

                val filtered6 = getBitmapFromColorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            0.33f, 0.33f, 0.33f, 0f, 0f,
                            0.33f, 0.33f, 0.33f, 0f, 0f,
                            0.33f, 0.33f, 0.33f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    ),
                    bitmap
                )

                onFiltersLoad(listOf(filtered1, filtered6, filtered2, filtered3, filtered4, filtered5))
            }
        }
    }
}

suspend fun getBitmapFromColorMatrix(cm: ColorMatrix, sourceBitmap: Bitmap): Bitmap {
    return withContext(Dispatchers.Default) {
        val drawableBitmap: Bitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val ret =
            Bitmap.createBitmap(drawableBitmap.width, drawableBitmap.height, drawableBitmap.config)
        val canvas = Canvas(ret)
        val paint = Paint()
        paint.setColorFilter(ColorMatrixColorFilter(cm))
        canvas.drawBitmap(drawableBitmap, 0F, 0F, paint)
        return@withContext ret
    }
}