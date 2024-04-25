package cu.z17.android.ui.main

import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cu.z17.android.ui.main.MediaUtil.getDurationFromFile
import cu.z17.views.R
import cu.z17.views.camera.ResultType
import cu.z17.views.camera.Z17Camera
import cu.z17.views.imageEditor.Z17ImageEditor
import cu.z17.views.inputText.Z17InputText
import cu.z17.views.label.Z17Label
import cu.z17.views.label.Z17MarkdownLabel
import cu.z17.views.permission.PermissionNeedIt
import cu.z17.views.permission.PermissionsRequesterState
import cu.z17.views.permission.Z17PermissionChecker
import cu.z17.views.permission.Z17PermissionRequester
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.picturesAlbum.data.AlbumConfiguration
import cu.z17.views.picturesAlbum.data.AlbumOption
import cu.z17.views.picturesAlbum.ui.Z17FullSizeAlbum
import cu.z17.views.picturesAlbum.ui.Z17RowAlbum
import cu.z17.views.videoEditor.VideoEditorConfigurations
import cu.z17.views.videoEditor.Z17VideoEditor
import cu.z17.views.videosAlbum.data.VideoAlbumConfiguration
import cu.z17.views.videosAlbum.ui.Z17FullSizeVideoAlbum
import cu.z17.views.videosAlbum.ui.Z17RowVideoAlbum
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Camera(
    modifier: Modifier = Modifier,
    showVideoOption: Boolean = true,
    enableImageFoot: Boolean = true,
    enableDeleteImage: Boolean = false,
    maxVideoDuration: Long = 5_000,
    sendVideos: (
        files: List<File>,
        content: String,
    ) -> Unit,
    sendImages: (
        files: List<File>,
        content: String,
    ) -> Unit,
    onDelete: () -> Unit = {},
    onClose: () -> Unit,
    onError: () -> Unit,
) {
    Box(
        modifier = modifier
            .clickable(enabled = false) {}
            .background(color = MaterialTheme.colorScheme.background)) {
        var isVideo by remember {
            mutableStateOf(false)
        }

        val keyboardController = LocalSoftwareKeyboardController.current

        val permissionsState = remember {
            mutableStateOf(
                PermissionsRequesterState(
                    open = false,
                    permissions = listOf(
                        PermissionNeedIt.STORAGE,
                        PermissionNeedIt.CAMERA,
                        PermissionNeedIt.RECORD_AUDIO
                    )
                )
            )
        }

        val context = LocalContext.current

        Z17PermissionChecker(
            permissionState = permissionsState.value,
            onResult = { result ->
                permissionsState.value = permissionsState.value.copy(
                    permissionsResult = result,
                    open = result.state != 2
                )
            },
            onClick = {
                permissionsState.value = permissionsState.value.copy(
                    open = true
                )
            },
            content = {
                if (permissionsState.value.permissionsResult.state == 4) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.background)
                    ) {

                        IconButton(
                            onClick = {
                                onClose()
                            },
                            modifier = Modifier
                                .padding(20.dp)
                                .align(alignment = Alignment.TopStart)
                        ) {
                            Icon(
                                Icons.Outlined.Close,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = ""
                            )
                        }

                        Z17MarkdownLabel(
                            modifier = Modifier
                                .align(alignment = Alignment.Center)
                                .fillMaxWidth()
                                .padding(20.dp)
                                .background(
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2F),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(20.dp),
                            text = stringResource(id = R.string.required_permissions_were_not_granted),
                            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                            onClick = {
                                permissionsState.value = permissionsState.value.copy(
                                    open = true
                                )
                            }
                        )
                    }
                }
            }
        )

        Z17PermissionRequester(
            permissionState = permissionsState.value,
            onResult = { result ->
                permissionsState.value = permissionsState.value.copy(
                    permissionsResult = result
                )

                if (result.state == 2) {
                    permissionsState.value = permissionsState.value.copy(
                        open = false
                    )
                }
            },
            onClose = {
                permissionsState.value = permissionsState.value.copy(
                    open = false
                )
            },
            packageName = context.packageName
        )

        if (permissionsState.value.permissionsResult.state == 2)
            CameraPager(
                modifier = Modifier.fillMaxSize(),
                showVideoOption = showVideoOption,
                enableImageFoot = enableImageFoot,
                enableDeleteImage = enableDeleteImage,
                maxVideoDuration = maxVideoDuration,
                sendImages = sendImages,
                sendVideos = sendVideos,
                onClose = onClose,
                onError = onError,
                isVideo = isVideo,
                onDelete = onDelete,
                changeIsVideo = {
                    isVideo = it
                }
            )

        LaunchedEffect(Unit) {
            keyboardController?.hide()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraPager(
    modifier: Modifier = Modifier,
    showVideoOption: Boolean,
    enableImageFoot: Boolean,
    enableDeleteImage: Boolean = false,
    maxVideoDuration: Long = Long.MAX_VALUE,
    isVideo: Boolean,
    sendVideos: (
        files: List<File>,
        content: String,
    ) -> Unit,
    sendImages: (
        files: List<File>,
        content: String,
    ) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    onError: () -> Unit,
    changeIsVideo: (Boolean) -> Unit,
) {
    Box(modifier) {
        val context = LocalContext.current

        val pagerState = rememberPagerState(
            pageCount = { 3 })

        var imagePath by rememberSaveable {
            mutableStateOf("")
        }

        var videoPathAndDuration by rememberSaveable {
            mutableStateOf("" to 0L)
        }

        val coroutineScope = rememberCoroutineScope()

        fun newImageName(): String {
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
            return "${formatter.format(Date())}.png"
        }

        fun newVideoName(): String {
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
            return "${formatter.format(Date())}.mp4"
        }

        val imagePathToSave = remember {
            val dir = File(File(context.filesDir, "test"), "IMAGES")
            if (!dir.exists()) dir.mkdirs()

            File(dir, newImageName()).path
        }

        var initialRotation by rememberSaveable {
            mutableFloatStateOf(0F)
        }

        val videoPathToSave = remember {
            val dir = File(File(context.filesDir, "test"), "VIDEOS")
            if (!dir.exists()) dir.mkdirs()

            File(dir, newImageName()).path
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            if (page == 0) {
                CameraViewPage(
                    showVideoOption = showVideoOption,
                    imagePathToSave = imagePathToSave,
                    enableDeleteImage = enableDeleteImage,
                    videoPathToSave = videoPathToSave,
                    onResult = { path, resultType, duration, rotation->
                        initialRotation = rotation
                        when (resultType) {
                            ResultType.VIDEO -> {
                                videoPathAndDuration = path to duration
                                coroutineScope.launch {
                                    pagerState.scrollToPage(2)
                                }
                            }

                            ResultType.PHOTO -> {
                                imagePath = path
                                coroutineScope.launch {
                                    pagerState.scrollToPage(1)
                                }
                            }

                            ResultType.DELETE -> {
                                onDelete()
                            }
                        }
                    },
                    onClose = onClose,
                    onError = onError,
                    isVideo = isVideo,
                    changeIsVideo = changeIsVideo
                )
            }

            if (page == 1) {
                ImageEditorViewPage(
                    enableImageFoot = enableImageFoot,
                    imagePath = imagePath,
                    imagePathToSave = imagePathToSave,
                    initialRotation = initialRotation,
                    onResult = { path, stringContent ->
                        sendImages(listOf(File(path)), stringContent)
                    },
                    retry = {
                        imagePath = ""
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    onError = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                )
            }

            if (page == 2) {
                VideoEditorViewPage(
                    enableImageFoot = enableImageFoot,
                    videoPath = videoPathAndDuration.first,
                    videoPathToSave = videoPathToSave,
                    duration = videoPathAndDuration.second,
                    maxVideoDuration = maxVideoDuration,
                    onResult = { path, stringContent ->
                        sendVideos(listOf(File(path)), stringContent)
                    },
                    retry = {
                        videoPathAndDuration = "" to 0L
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    onError = onError
                )
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            if (pagerState.currentPage == 1 && imagePath.isEmpty())
                onError()

            if (pagerState.currentPage == 2 && videoPathAndDuration.first.isEmpty())
                onError()
        }

        BackHandler {
            if (!pagerState.isScrollInProgress && pagerState.currentPage == 0) onClose()
            if (!pagerState.isScrollInProgress && pagerState.currentPage == 1) {
                imagePath = ""
                videoPathAndDuration = "" to 0L
                coroutineScope.launch {
                    pagerState.scrollToPage(0)
                }
            }
            if (!pagerState.isScrollInProgress && pagerState.currentPage == 2) {
                imagePath = ""
                videoPathAndDuration = "" to 0L
                coroutineScope.launch {
                    pagerState.scrollToPage(0)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraViewPage(
    showVideoOption: Boolean,
    enableDeleteImage: Boolean = false,
    imagePathToSave: String,
    videoPathToSave: String,
    onResult: (String, ResultType, Long, Float) -> Unit,
    onClose: () -> Unit,
    onError: () -> Unit,
    isVideo: Boolean,
    changeIsVideo: (Boolean) -> Unit,
) {

    val context = LocalContext.current

    val bottomSheetState = rememberBottomSheetScaffoldState()

    val screenHeight = LocalConfiguration.current.screenHeightDp

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = {
                it?.let { uri ->
                    onResult(
                        MediaUtil.getPath(
                            context, uri
                        )!!,
                        ResultType.PHOTO,
                        0L,
                        0F
                    )
                }
            })

    val videoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = {
                it?.let { uri ->
                    val path = MediaUtil.getPath(
                        context, uri
                    )!!
                    val fileDuration = getDurationFromFile(context, File(path))
                    onResult(
                        path,
                        ResultType.VIDEO,
                        fileDuration * 1000,
                        0F
                    )
                }
            })

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContainerColor = Color.Transparent,
        sheetContentColor = Color.Transparent,
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
        sheetTonalElevation = 0.dp,
        sheetShadowElevation = 0.dp,
        sheetPeekHeight = 140.dp,
        sheetShape = RectangleShape,
        sheetDragHandle = null,
        sheetContent = {
            val show = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                Environment.isExternalStorageManager()
            else true

            if (show) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isVideo)
                        Z17RowVideoAlbum(
                            albumConfiguration = VideoAlbumConfiguration(
                                multipleVideosAllowed = false
                            ),
                            onVideoSelected = {
                                onResult(
                                    MediaUtil.getPath(
                                        context, it.first().uri
                                    )!!, ResultType.VIDEO,
                                    it.first().duration!!,
                                    0F
                                )
                            },
                            optionsList = if (enableDeleteImage) listOf(
                                AlbumOption(preview = Icons.Outlined.DeleteOutline) {
                                    onResult(
                                        "",
                                        ResultType.DELETE,
                                        0L,
                                        0F
                                    )
                                },
                                AlbumOption(preview = Icons.Outlined.Folder) {
                                    videoPickerLauncher.launch("video/*")
                                }
                            ) else listOf(
                                AlbumOption(preview = Icons.Outlined.Folder) {
                                    videoPickerLauncher.launch("video/*")
                                }
                            )
                        )
                    else
                        Z17RowAlbum(
                            albumConfiguration = AlbumConfiguration(
                                multipleImagesAllowed = false
                            ),
                            onPhotoSelected = {
                                onResult(
                                    MediaUtil.getPath(
                                        context, it.first().uri
                                    )!!,
                                    ResultType.PHOTO,
                                    0L,
                                    0F
                                )
                            },
                            optionsList = if (enableDeleteImage) listOf(
                                AlbumOption(preview = Icons.Outlined.DeleteOutline) {
                                    onResult(
                                        "",
                                        ResultType.DELETE,
                                        0L,
                                        0F
                                    )
                                },
                                AlbumOption(preview = Icons.Outlined.Folder) {
                                    imagePickerLauncher.launch("image/*")
                                }
                            ) else listOf(
                                AlbumOption(preview = Icons.Outlined.Folder) {
                                    imagePickerLauncher.launch("image/*")
                                }
                            )
                        )
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(screenHeight.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isVideo)
                        Z17FullSizeVideoAlbum(
                            Modifier
                                .fillMaxSize(),
                            albumConfiguration = VideoAlbumConfiguration(multipleVideosAllowed = false),
                            onVideoSelected = {
                                onResult(
                                    MediaUtil.getPath(
                                        context, it.first().uri
                                    )!!,
                                    ResultType.VIDEO,
                                    it.first().duration!!,
                                    0F
                                )
                            }
                        )
                    else
                        Z17FullSizeAlbum(
                            Modifier
                                .fillMaxSize(),
                            albumConfiguration = AlbumConfiguration(multipleImagesAllowed = false),
                            onPhotoSelected = {
                                onResult(
                                    MediaUtil.getPath(
                                        context, it.first().uri
                                    )!!,
                                    ResultType.PHOTO,
                                    0L,
                                    0F
                                )
                            }
                        )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 120.dp)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Z17Camera(
                modifier = Modifier
                    .weight(1F),
                isVideo = isVideo,
                imagePathToSave = imagePathToSave,
                videoPathToSave = videoPathToSave,
                onClose = onClose,
                onResult = onResult,
                onError = onError
            )

            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                if (showVideoOption)
                    AnimatedVisibility(visible = isVideo) {
                        Z17Label(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.background)
                                .padding(10.dp),
                            text = "Camera",
                            color = MaterialTheme.colorScheme.background
                        )
                    }


                if (showVideoOption)
                    Z17Label(
                        modifier = Modifier
                            .clickable {
                                changeIsVideo(true)
                            }
                            .border(
                                if (!isVideo) 0.dp else 1.dp,
                                color = if (isVideo) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                                shape = CircleShape
                            )
                            .background(color = MaterialTheme.colorScheme.background)
                            .padding(10.dp),
                        text = "Video",
                        color = MaterialTheme.colorScheme.onBackground
                    )

                Z17Label(
                    modifier = Modifier
                        .clickable {
                            changeIsVideo(false)
                        }
                        .border(
                            if (isVideo) 0.dp else 1.dp,
                            color = if (!isVideo) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                            shape = CircleShape
                        )
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(10.dp),
                    text = "Camera",
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (showVideoOption)
                    AnimatedVisibility(visible = !isVideo) {
                        Z17Label(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.background)
                                .padding(10.dp),
                            text = "Video",
                            color = MaterialTheme.colorScheme.background
                        )
                    }
            }
        }
    }
}

@Composable
fun ImageEditorViewPage(
    enableImageFoot: Boolean,
    imagePath: String,
    imagePathToSave: String,
    initialRotation: Float,
    onResult: (String, String) -> Unit, // path and string footer content
    retry: () -> Unit,
    onError: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        var stringContent by rememberSaveable {
            mutableStateOf("")
        }

        var isReady by rememberSaveable {
            mutableStateOf(false)
        }

        var wasEdited by remember {
            mutableStateOf(false)
        }

        Z17ImageEditor(
            modifier = Modifier.weight(1F),
            initialRotation = initialRotation,
            source = Uri.fromFile(File(imagePath)),
            imagePathToSave = imagePathToSave,
            onViewState = {
                isReady = it
            },
            onError = onError,
            onEdited = {
                wasEdited = it
            }
        )

        if (isReady)
            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(bottom = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (enableImageFoot)
                    Z17InputText(
                        modifier = Modifier.weight(1F),
                        enabled = enableImageFoot,
                        value = stringContent,
                        labelText = "Image footer",
                        onTextChange = {
                            stringContent = it
                        },
                        shape = CircleShape,
                        maxLength = 500
                    )

                Spacer(modifier = Modifier.width(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                        .padding(10.dp)
                        .clickable {
                            onResult(
                                if (wasEdited) imagePathToSave else imagePath,
                                stringContent
                            )
                        }
                ) {
                    Z17BasePicture(
                        modifier = Modifier.size(24.dp),
                        source = Icons.Outlined.Check,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Z17Label(
                        text = "Ready",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
    }
}

@Composable
fun VideoEditorViewPage(
    enableImageFoot: Boolean,
    videoPath: String,
    videoPathToSave: String,
    duration: Long,
    maxVideoDuration: Long,
    onResult: (String, String) -> Unit,
    retry: () -> Unit,
    onError: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        var stringContent by rememberSaveable {
            mutableStateOf("")
        }

        var isReady by rememberSaveable {
            mutableStateOf(false)
        }

        var wasEdited by remember {
            mutableStateOf(false)
        }

        Z17VideoEditor(
            modifier = Modifier.weight(1F),
            source = videoPath,
            duration = duration,
            videoPathToSave = videoPathToSave,
            onViewState = {
                isReady = it
            },
            onError = onError,
            onEdited = {
                wasEdited = it
            },
            configs = VideoEditorConfigurations(
                allowCrop = (duration / 1000L) >= 5,
                allowFilters = false,
                forceCrop = maxVideoDuration
            )
        )

        if (isReady)
            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(bottom = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (enableImageFoot)
                    Z17InputText(
                        modifier = Modifier.weight(1F),
                        enabled = enableImageFoot,
                        value = stringContent,
                        labelText = "Image footer",
                        onTextChange = {
                            stringContent = it
                        },
                        shape = CircleShape,
                        maxLength = 500
                    )

                Spacer(modifier = Modifier.width(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                        .padding(10.dp)
                        .clickable {
                            onResult(
                                if (wasEdited) videoPathToSave else videoPath,
                                stringContent
                            )
                        }
                ) {
                    Z17BasePicture(
                        modifier = Modifier.size(24.dp),
                        source = Icons.Outlined.Check,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Z17Label(
                        text = "Ready",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
    }
}