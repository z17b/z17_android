package cu.z17.views.webView

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


@SuppressLint("ClickableViewAccessibility")
@Composable
fun Z17WebView(
    modifier: Modifier = Modifier,
    url: String
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                // Use file system
                this.getSettings().domStorageEnabled = true
                this.getSettings().allowContentAccess = true
                this.getSettings().allowFileAccess = true

                // disabling scrolling
                this.isVerticalScrollBarEnabled = false
                this.isHorizontalScrollBarEnabled = false
                this.setOnTouchListener { _, event -> event.action == MotionEvent.ACTION_MOVE }

                webChromeClient = customWebChromeClient()
                loadUrl(url)
            }
        }
    )
}

private fun customWebChromeClient(): WebChromeClient {
    val webChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            openFileChooser(filePathCallback)
            return true
        }
    }
    return webChromeClient
}

private fun openFileChooser(filePathCallback: ValueCallback<Array<Uri>>) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "*/*"
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
}