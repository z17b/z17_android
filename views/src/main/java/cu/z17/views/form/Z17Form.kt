package cu.z17.views.form

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cu.z17.views.R
import cu.z17.views.button.Z17PrimaryButton
import cu.z17.views.loader.Z17SimpleCircleLoader
import cu.z17.views.permission.PermissionNeedIt
import cu.z17.views.permission.Z17PermissionCheckerAndRequester
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Z17Form(
    modifier: Modifier = Modifier,
    initialRequest: List<FormItemRequest>,
    submitBtn: @Composable (() -> Unit) -> Unit = { onSubmit ->
        Z17PrimaryButton(
            onClick = {
                onSubmit()
            },
            text = stringResource(R.string.submit),
            maxWidth = true
        )
    },
    onComplete: (List<FormItemRequest>) -> Unit,
    onRequestRealPath: (String) -> String = { it }
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val request = remember {
        mutableStateListOf<FormItemRequest>()
    }

    var permissionsGranted by remember {
        mutableStateOf(false)
    }

    val isLoading by remember {
        derivedStateOf { request.isEmpty() && initialRequest.isNotEmpty() }
    }

    fun handleChange(index: Int, newFormItemRequest: FormItemRequest) {
        request[index] = newFormItemRequest
    }

    var isChecked by remember {
        mutableStateOf(false)
    }

    fun checkForm() {
        var hasNonErrors = true

        request.forEach {
            hasNonErrors = hasNonErrors && it.nonErrorCondition(it.value)
        }

        isChecked = true

        if (hasNonErrors) onComplete(request)
    }

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Z17SimpleCircleLoader()
        }
    } else {
        Box(modifier) {
            val listState = rememberLazyListState()

            var cameraDisplaying by remember {
                mutableStateOf<Triple<Int, Int, String>?>(null)
            }

            fun getImage(index: Int) {
                coroutineScope.launch {
                    cameraDisplaying = null
                    delay(100)
                    cameraDisplaying = Triple(index, 1, "")
                }
            }

            fun clearImage(): String {
                val value = cameraDisplaying?.third ?: ""

                cameraDisplaying = null

                return value
            }

            LazyColumn(
                modifier = modifier,
                state = listState
            ) {
                items(
                    count = request.size,
                    key = { request[it].id }
                ) { index ->
                    FormItemView(
                        modifier = Modifier
                            .animateItemPlacement()
                            .padding(vertical = 10.dp),
                        formItemRequest = request[index],
                        isChecked = isChecked,
                        updateItem = { upd ->
                            handleChange(index, upd)
                            isChecked = false
                        },
                        cameraDisplaying = if (cameraDisplaying?.first == index && cameraDisplaying?.third != null)
                            cameraDisplaying!!.first to cameraDisplaying!!.third
                        else
                            null,
                        getImage = {
                            getImage(index)
                        },
                        clearImage = ::clearImage
                    )
                }

                item {
                    Box(modifier = Modifier.padding(vertical = 10.dp)) {
                        submitBtn.invoke {
                            checkForm()
                        }
                    }
                }
            }

            if (cameraDisplaying != null) {
                if (permissionsGranted)
                    Z17FormCamera(
                        modifier = Modifier
                            .fillMaxSize(),
                        onClose = {
                            cameraDisplaying = null
                        },
                        type = cameraDisplaying?.second ?: 1,
                        rootPath = File(context.cacheDir, "/form_photo/").path,
                        initialValue = cameraDisplaying?.third ?: "",
                        onRequestRealPath = onRequestRealPath
                    )

                Z17PermissionCheckerAndRequester(
                    initialPermissions = listOf(
                        PermissionNeedIt.STORAGE,
                        PermissionNeedIt.CAMERA,
                        PermissionNeedIt.RECORD_AUDIO
                    ),
                    onGranted = {
                        permissionsGranted = true
                    },
                    packageName = context.packageName,
                    stringContent = null
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        if (request.isEmpty() && initialRequest.isNotEmpty()) request.addAll(initialRequest)
    }
}