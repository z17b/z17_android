package cu.z17.android.ui.main

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cu.z17.android.ui.theme.AppTheme
import cu.z17.compress.compressFormat
import cu.z17.views.camera.Z17CameraModule
import cu.z17.views.label.Z17ClickableLabel
import cu.z17.views.label.Z17ClickableLabelClickType
import cu.z17.views.qr.Z17QRView
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
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                        .safeContentPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var permissionsAccepted by remember {
                        mutableStateOf(false)
                    }

                    val context = LocalContext.current

                    if (permissionsAccepted) {
                        Camera(
                            modifier = Modifier
                                .fillMaxSize(),
                            sendImages = { files, content ->

                            },
                            sendVideos = { files, content ->

                            },
                            onClose = {},
                            onError = {},
                            // TODO REMOVE
                            showVideoOption = true
                        )
                    }

                    Z17QRView(
                        data = "asdasd",
                        modifier = Modifier
                            .size(200.dp)
                    )

                    Z17ClickableLabel(
                        text = "Lorem ipsum dolor sit amet, consectetur adipiscing \n\n asda /elit. 9218-555-1234 sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. 1234-5678-9012 enim ad minim veniam, quis nostrud exercitation ullamco 8888-7777-6666 laboris nisi.\n" +
                                "\n" +
                                "Curabitur 2025-05-13 pretium 9999 -0000-1234-ABCD (Nota: Este último no es válido según el regex, se incluye como contraste). Suspendisse 5555-3333 potenti. 6789-01-23-45-67 et justo vitae, ultricies 4815-26-37-48-59.",
                        onClick = { type, value ->
                            when (type) {
                                Z17ClickableLabelClickType.LINK -> {
                                    println("${MessageAction.LINK_CLICK} $value")
                                }

                                Z17ClickableLabelClickType.NUMBER -> {
                                    println("${MessageAction.NUMBER_CLICK} $value")
                                }

                                Z17ClickableLabelClickType.TAG -> {
                                    println("${MessageAction.TAG_CLICK} $value")
                                }

                                Z17ClickableLabelClickType.MENTION -> {
                                    println("${MessageAction.MENTION_CLICK} $value")
                                }

                                Z17ClickableLabelClickType.COMMAND -> {
                                    println("${MessageAction.COMMAND_CLICK} $value")
                                }

                                else -> {
                                    println("${MessageAction.REGULAR_CLICK} $value")
                                }
                            }
                        },
                        onLongClick = {

                        }
                    )

//                    Z17PermissionCheckerAndRequester(
//                        initialPermissions = listOf(
//                            PermissionNeedIt.CAMERA,
//                            PermissionNeedIt.RECORD_AUDIO,
//                            PermissionNeedIt.STORAGE
//                        ),
//                        onGranted = {
//                            permissionsAccepted = true
//                        },
//                        packageName = context.packageName,
//                        stringContent = null
//                    )

//                    Z17Form(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .safeContentPadding(),
//                        initialRequest = listOf(
//                            FormItemRequest(
//                                id = "item7",
//                                type = FormItemType.IMAGE,
//                                label = "Escoge una imagen",
//                                description = "Utiliza una imagen para reconocerte.",
//                                value = "",
//                                okRegex = CAN_BE_EMPTY,
//                                errorLabel = "",
//                            ),
//                            FormItemRequest(
//                                id = "item8",
//                                type = FormItemType.LARGE_IMAGE,
//                                label = "Escoge una imagen",
//                                description = "Utiliza una imagen para fondo.",
//                                value = "",
//                                okRegex = CAN_BE_EMPTY,
//                                errorLabel = "",
//                            ),
//                            FormItemRequest(
//                                id = "item1",
//                                type = FormItemType.TEXT,
//                                label = "Prueba tu nombre",
//                                description = "Aqui tienes que poner tu nombre completo",
//                                value = "",
//                                okRegex = CANNOT_BE_EMPTY,
//                                errorLabel = "No puede estar en blanco",
//                                limit = 100
//                            ),
//                            FormItemRequest(
//                                id = "item2",
//                                type = FormItemType.NUMBER,
//                                label = "Pon tu edad",
//                                description = "",
//                                value = "",
//                                okRegex = CANNOT_BE_EMPTY,
//                                errorLabel = "Introduce tu edad por favor",
//                                limit = 3
//                            ),
//                            FormItemRequest(
//                                id = "item4",
//                                type = FormItemType.LARGE_TEXT,
//                                label = "Biografía",
//                                description = "Resume un poco de tu experiencia laboral",
//                                value = "",
//                                okRegex = CAN_BE_EMPTY,
//                                errorLabel = "",
//                                limit = 1000
//                            ),
//                            FormItemRequest(
//                                id = "item5",
//                                type = FormItemType.SIMPLE_SELECTION,
//                                label = "Selecciona una tecnología",
//                                description = "Estas son las tecnologías con plazas abiertas",
//                                value = "",
//                                okRegex = CANNOT_BE_EMPTY,
//                                errorLabel = "Debes seleccionar una para aplicar",
//                                limit = 1,
//                                selectionList = listOf(
//                                    "Android",
//                                    "ReactJS",
//                                    "Erlang"
//                                )
//                            ),
//                            FormItemRequest(
//                                id = "item3",
//                                type = FormItemType.NUMBER,
//                                label = "Pon tus años de experiencia",
//                                description = "",
//                                value = "0",
//                                okRegex = CAN_BE_EMPTY,
//                                errorLabel = "",
//                                limit = 2,
//                                displaySize = 0.5F
//                            ),
//                            FormItemRequest(
//                                id = "item6",
//                                type = FormItemType.MULTIPLE_SELECTION,
//                                label = "Selecciona una tecnología",
//                                description = "Estas son las tecnologías con plazas abiertas",
//                                value = "",
//                                okRegex = CANNOT_BE_EMPTY,
//                                errorLabel = "Debes seleccionar una para aplicar",
//                                limit = 1,
//                                selectionList = listOf(
//                                    "Android",
//                                    "ReactJS",
//                                    "Erlang"
//                                )
//                            ),
//                        ),
//                        submitBtn = { onSubmit ->
//                            Z17PrimaryButton(
//                                onClick = {
//                                    onSubmit()
//                                },
//                                text = stringResource(cu.z17.views.R.string.submit),
//                                maxWidth = true
//                            )
//                        },
//                        onComplete = {
//                            println("!!!! ${it}")
//                        },
//                        onRequestRealPath = {
//                            val result = FileUtils.getRealPath(context, Uri.parse(it))
//                            result ?: ""
//                        }
//                    )

//                    var selectedI by remember { mutableIntStateOf(1) }
//                    Z17Spinner2(
//                        options = listOf(1, 2, 3, 4, 5, 6),
//                        handleSelection = {
//                            selectedI = it
//                        },
//                        selectedOption = selectedI,
//                        getTitle = { it.toString() }
//                    )
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