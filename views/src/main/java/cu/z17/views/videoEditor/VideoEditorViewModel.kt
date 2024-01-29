package cu.z17.views.videoEditor

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.z17.views.imageEditor.Z17EditorState
import cu.z17.views.utils.VideoUtils
import cu.z17.views.utils.Z17MutableListFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date


class VideoEditorViewModel : ViewModel() {
    private val _currentState = MutableStateFlow(Z17EditorState.LOADING)
    val currentState = _currentState.asStateFlow()

    fun requestState(z17EditorState: Z17EditorState) {
        _currentState.value = z17EditorState
    }

    val thumbnails = Z17MutableListFlow<Bitmap>()

    fun generateThumbnailList(videoPath: String, duration: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            val secs = (duration / 1000L)
            val pieces = (secs / 2).toInt()

            repeat(
                times = if (pieces > 6) {
                    pieces
                } else 6
            ) {
                val newThumbnail = VideoUtils.getThumbnailOnTime(
                    path = videoPath,
                    time = it * 1000L
                )

                newThumbnail?.let {
                    thumbnails.add(it)
                }
            }

            onComplete()
        }
    }

    fun saveVideo(
        source: Uri,
        context: Context,
        videoPathToSave: String,
        startMilliseconds: Long,
        endMilliseconds: Long,
        onComplete: (Long) -> Unit,
        onError: () -> Unit,
    ) {
        viewModelScope.launch {
            requestState(Z17EditorState.LOADING)

            val temp = videoPathToSave + Date().time

            withContext(Dispatchers.IO) {
                File(temp).let {
                    if (it.exists())
                        it.delete()
                }
            }

            val result = VideoUtils.saveVideo(
                source = source,
                context = context,
                videoPathToSave = temp,
                startMilliseconds = startMilliseconds,
                endMilliseconds = endMilliseconds
            ).first()

            withContext(Dispatchers.IO) {
                File(temp).copyTo(File(videoPathToSave), true)
            }

            withContext(Dispatchers.IO) {
                File(temp).let {
                    if (it.exists())
                        it.delete()
                }
            }

            if (result) {
                val newDuration =
                    calculateNewDuration(context, source, endMilliseconds - startMilliseconds)
                onComplete(newDuration)
            } else onError()
        }
    }

    private suspend fun calculateNewDuration(context: Context, uri: Uri, default: Long): Long {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, uri)

                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInMillisec = time!!.toLong()

                retriever.release()

                timeInMillisec
            } catch (e: Exception) {
                e.printStackTrace()

                default
            }
        }
    }

    fun cancelChanges(currentSource: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                requestState(Z17EditorState.LOADING)

                try {
                    File(currentSource).let {
                        if (it.exists())
                            it.delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                onComplete()
            }
        }
    }
}