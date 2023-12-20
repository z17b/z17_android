package cu.z17.views.qr

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import cu.z17.views.picture.Z17BasePicture

@Composable
fun Z17QRView(modifier: Modifier = Modifier, data: String) {
    Box(modifier = modifier) {
        val viewModel: Z17QrViewModel = viewModel()

        val primaryColor = MaterialTheme.colorScheme.primary

        val state = viewModel.qrResult.collectAsStateWithLifecycle()

        val dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = SimpleColorFilter(primaryColor.toArgb()),
                keyPath = arrayOf("**")
            ),
        )

        val placeholderLottie =
            rememberLottieComposition(spec = LottieCompositionSpec.RawRes(cu.z17.views.R.raw.qr_code_scan))

        if (state.value != null)
            Z17BasePicture(
                source = state.value, modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1F)
            )
        else
            LottieAnimation(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1F),
                composition = placeholderLottie.value,
                iterations = Int.MAX_VALUE,
                reverseOnRepeat = true,
                dynamicProperties = dynamicProperties
            )

        LaunchedEffect(Unit) {
            viewModel.loadContent(data, primaryColor.toArgb())
        }
    }
}