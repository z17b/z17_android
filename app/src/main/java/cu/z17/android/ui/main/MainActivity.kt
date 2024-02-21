package cu.z17.android.ui.main

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import cu.z17.android.ui.theme.AppTheme
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.utils.Z17BasePictureHeaders
import cu.z17.views.utils.Z17CoilDecoders

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContent {
            AppTheme {
                val sizeH = LocalConfiguration.current.screenHeightDp

                Z17CoilDecoders.createInstance { Z17CoilDecoders(applicationContext) }
                Z17BasePictureHeaders.createInstance { Z17BasePictureHeaders(
                    mapOf(
                        "Content-Type" to "application/json",
                        "User-Agent" to "ToDus 3.0.0 Pictures",
                        "Authorization" to "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDg1MzY5MzEsInRvRHVzSWQiOiJFZGR5IiwidXNlcm5hbWUiOiI1MzUyMTg5Mzc2IiwidmVyc2lvbiI6IjMwMDI0In0.kCXA2CzI8N4SSmu5QJdY0pTPa6rp39ZOnR32eVHP8m0"
                    )
                ) }

                Box(Modifier) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((sizeH / 2).dp)
                    ) {
                        Z17BasePicture(
                            modifier = Modifier.fillMaxSize(),
                            source = "https://s3.todus.cu/todus/profile/7340b66949a41f7c38a49a21ef62e829b122fba942226d7b4c160d35c860ab42",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}