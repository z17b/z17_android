package cu.z17.views.qr

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import cu.z17.views.button.Z17SecondaryDialogButton
import cu.z17.views.picture.Z17BasePicture

@Composable
fun Z17QRScanner(
    modifier: Modifier = Modifier,
    handleCorrectScan: (String) -> Unit,
    handleCancelScan: () -> Unit,
    cancelText: String = "Cancel",
    showCancel: Boolean = false,
    size: Dp = 250.dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current

        var scanFlag by remember {
            mutableStateOf(false)
        }

        var torchFlag by remember {
            mutableStateOf(false)
        }

        fun handleTorch() {
            torchFlag = !torchFlag
        }

        val beepManager = BeepManager(LocalContext.current as Activity)

        val compoundBarcodeView = remember {
            DecoratedBarcodeView(context).apply {
                val capture = CaptureManager(context as Activity, this)
                capture.initializeFromIntent(context.intent, null)
                this.setStatusText("")
                capture.decode()

                if (torchFlag) this.setTorchOn() else this.setTorchOff()

                this.decodeContinuous { result ->
                    if (scanFlag) {
                        return@decodeContinuous
                    }
                    if (torchFlag) this.setTorchOff()
                    result?.let { barCodeOrQr ->
                        beepManager.playBeepSoundAndVibrate()

                        scanFlag = true
                        handleCorrectScan(barCodeOrQr.result.toString())
                    }
                }
                this.resume()
            }
        }

        Box {
            AndroidView(
                factory = { compoundBarcodeView },
                modifier = Modifier.size(size),
                update = {
                    if (torchFlag) it.setTorchOn() else it.setTorchOff()
                }
            )

            IconButton(onClick = {
                handleTorch()
            }) {
                Z17BasePicture(
                    modifier = Modifier.size(30.dp),
                    source = if (torchFlag) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surface)
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        if (showCancel)
            Z17SecondaryDialogButton(
                text = cancelText,
                onClick = { handleCancelScan() }
            )

        DisposableEffect(Unit) {
            compoundBarcodeView.resume()
            onDispose {
                compoundBarcodeView.pause()
            }
        }
    }
}
