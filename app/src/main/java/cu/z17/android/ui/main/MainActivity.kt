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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cu.z17.android.ui.theme.AppTheme
import cu.z17.compress.compressFormat
import cu.z17.views.camera.Z17CameraModule
import cu.z17.views.permission.PermissionNeedIt
import cu.z17.views.permission.Z17PermissionCheckerAndRequester
import cu.z17.views.utils.Z17BasePictureHeaders
import cu.z17.views.utils.Z17CoilDecoders
import cu.z17.views.videoPlayer.Z17VideoModule

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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