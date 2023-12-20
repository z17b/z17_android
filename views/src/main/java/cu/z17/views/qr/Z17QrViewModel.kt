package cu.z17.views.qr

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Z17QrViewModel : ViewModel() {
    private val _qrResult = MutableStateFlow<Bitmap?>(null)
    val qrResult = _qrResult.asStateFlow()

    fun loadContent(content: String, primaryColor: Int, backgroundColor: Int = Color.WHITE) {
        viewModelScope.launch {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x, y, if (bitMatrix.get(x, y))
                            primaryColor else backgroundColor
                    )
                }
            }

            delay(1000)

            _qrResult.value = bitmap
        }
    }

}