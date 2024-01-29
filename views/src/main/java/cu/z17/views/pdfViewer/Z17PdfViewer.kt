package cu.z17.views.pdfViewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.net.toFile
import coil.memory.MemoryCache
import cu.z17.views.utils.Z17CoilDecoders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.sqrt


@Composable
fun Z17PdfViewer(
    modifier: Modifier = Modifier,
    uri: Uri,
    zoomable: Boolean,
    horizontal: Boolean = true,
) {
    Column(modifier) {
        val rendererScope = rememberCoroutineScope()
        val mutex = remember { Mutex() }
        val renderer by produceState<PdfRenderer?>(null, uri) {
            rendererScope.launch(Dispatchers.IO) {
                val input =
                    ParcelFileDescriptor.open(uri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY)
                value = PdfRenderer(input)
            }
            awaitDispose {
                val currentRenderer = value
                rendererScope.launch(Dispatchers.IO) {
                    mutex.withLock {
                        currentRenderer?.close()
                    }
                }
            }
        }

        val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }

        var actualPage by remember {
            mutableStateOf<Pair<Int, Bitmap?>>(Pair(0, null))
        }

        BoxWithConstraints(modifier = Modifier.weight(1F)) {
            val context = LocalContext.current

            val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
            val height = (width * sqrt(2f)).toInt()

            val imageLoadingScope = rememberCoroutineScope()

            if (horizontal) {
                PagePdfViewer(
                    modifier = modifier,
                    actualPage = actualPage,
                    cacheKey = "$uri-${actualPage.first}",
                    width = width,
                    height = height,
                    context = context,
                    zoomable = zoomable
                )
            }

            DisposableEffect(actualPage.first, pageCount) {
                val bitmap: Bitmap? = Z17CoilDecoders.getInstance().imageLoader.memoryCache?.get(
                    MemoryCache.Key("$uri-${actualPage.first}")
                )?.bitmap

                if (bitmap != null) actualPage = actualPage.copy(second = bitmap)
                else if (pageCount > actualPage.first) {
                    val job = imageLoadingScope.launch(Dispatchers.IO) {
                        val destinationBitmap =
                            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                        mutex.withLock {
                            if (!coroutineContext.isActive) return@launch
                            try {
                                renderer?.let {
                                    it.openPage(actualPage.first).use { page ->
                                        page.render(
                                            destinationBitmap,
                                            null,
                                            null,
                                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                //Just catch and return in case the renderer is being closed
                                return@launch
                            }
                        }
                        actualPage = actualPage.copy(second = destinationBitmap)
                    }

                    onDispose {
                        job.cancel()
                    }
                }

                onDispose {}
            }
        }

        PdfControls(
            actualPageIndex = actualPage.first,
            pageCount = pageCount,
            onPageChange = {
                actualPage = it to null
            }
        )
    }
}