package cu.z17.android.ui.main

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cu.z17.android.ui.theme.AppTheme
import cu.z17.compress.compressFormat
import cu.z17.views.button.Z17PrimaryButton
import cu.z17.views.camera.Z17CameraModule
import cu.z17.views.form.FormItemRequest
import cu.z17.views.form.FormItemType
import cu.z17.views.form.Z17Form
import cu.z17.views.permission.PermissionNeedIt
import cu.z17.views.permission.Z17PermissionCheckerAndRequester
import cu.z17.views.utils.Z17BasePictureHeaders
import cu.z17.views.utils.Z17CoilDecoders
import cu.z17.views.videoPlayer.Z17VideoModule

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        Z17CoilDecoders.createInstance { Z17CoilDecoders(applicationContext) }
        Z17BasePictureHeaders.createInstance {
            Z17BasePictureHeaders(
                headers = mapOf(
                    "Authorization" to "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MTY2OTI2NDgsInRvRHVzSWQiOiJFZGR5IiwidXNlcm5hbWUiOiI1MzUyMTg5Mzc2In0.JiBfqlptxCQRBrEMHpnDhx0J1IozLObGGO2kksQ9rPI",
                    "User-Agent" to "ToDus 2.0.24 Pictures",
                    "Content-Type" to "application/json"
                )
            )
        }

        Z17CameraModule.createInstance { Z17CameraModule(applicationContext) }.apply {
            this.defaultFormat = "jpeg".compressFormat()
        }
        Z17VideoModule.createInstance { Z17VideoModule(applicationContext) }

        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var permissionsAccepted by remember {
                        mutableStateOf(false)
                    }

                    val context = LocalContext.current

//                if (permissionsAccepted) {
//                    Camera(
//                        modifier = Modifier
//                            .fillMaxSize(),
//                        sendImages = { files, content ->
//
//                        },
//                        sendVideos = { files, content ->
//
//                        },
//                        onClose = {},
//                        onError = {},
//                        // TODO REMOVE
//                        showVideoOption = true
//                    )
//                }

                    Z17PermissionCheckerAndRequester(
                        initialPermissions = listOf(
                            PermissionNeedIt.CAMERA,
                            PermissionNeedIt.RECORD_AUDIO,
                            PermissionNeedIt.STORAGE
                        ),
                        onGranted = {
                            permissionsAccepted = true
                        },
                        packageName = context.packageName,
                        stringContent = null
                    )

                    Z17Form(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeContentPadding(),
                        initialRequest = listOf(
                            FormItemRequest(
                                id = "item1",
                                type = FormItemType.TEXT,
                                label = "Prueba tu nombre",
                                description = "Aqui tienes que poner tu nombre completo",
                                value = "",
                                nonErrorCondition = { it.isNotBlank() },
                                errorLabel = "No puede estar en blanco",
                                limit = 100
                            ),
                            FormItemRequest(
                                id = "item2",
                                type = FormItemType.NUMBER,
                                label = "Pon tu edad",
                                description = "",
                                value = "",
                                nonErrorCondition = { it.isNotBlank() },
                                errorLabel = "Introduce tu edad por favor",
                                limit = 3
                            ),
                            FormItemRequest(
                                id = "item4",
                                type = FormItemType.LARGE_TEXT,
                                label = "Biografía",
                                description = "Resume un poco de tu experiencia laboral",
                                value = "",
                                nonErrorCondition = { true },
                                errorLabel = "",
                                limit = 1000
                            ),
                            FormItemRequest(
                                id = "item5",
                                type = FormItemType.SIMPLE_SELECTION,
                                label = "Selecciona una tecnología",
                                description = "Estas son las tecnologías con plazas abiertas",
                                value = "",
                                nonErrorCondition = { it.isNotBlank() },
                                errorLabel = "Debes seleccionar una para aplicar",
                                limit = 1,
                                selectionList = listOf(
                                    "Android",
                                    "ReactJS",
                                    "Erlang"
                                )
                            ),
                            FormItemRequest(
                                id = "item3",
                                type = FormItemType.NUMBER,
                                label = "Pon tus años de experiencia",
                                description = "",
                                value = "0",
                                nonErrorCondition = { true },
                                errorLabel = "",
                                limit = 2,
                                displaySize = 0.5F
                            ),
                            FormItemRequest(
                                id = "item6",
                                type = FormItemType.MULTIPLE_SELECTION,
                                label = "Selecciona una tecnología",
                                description = "Estas son las tecnologías con plazas abiertas",
                                value = "",
                                nonErrorCondition = { it.isNotBlank() },
                                errorLabel = "Debes seleccionar una para aplicar",
                                limit = 1,
                                selectionList = listOf(
                                    "Android",
                                    "ReactJS",
                                    "Erlang"
                                )
                            ),
                        ),
                        submitBtn = { onSubmit ->
                            Z17PrimaryButton(
                                onClick = {
                                    onSubmit()
                                },
                                text = stringResource(cu.z17.views.R.string.submit),
                                maxWidth = true
                            )
                        },
                        onComplete = {}
                    )
                }
            }
        }
    }
}


@Composable
fun <T> CrossFade(
    targetState: T,
    content: @Composable (T) -> Unit,
) {
    Crossfade(
        targetState = targetState,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ), label = "crossfade"
    ) {
        content(it)
    }
}