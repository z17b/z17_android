package cu.z17.views.utils

import cu.z17.singledi.SingletonInitializer
import okhttp3.Headers

class Z17BasePictureHeaders(private val headers: Headers? = null) {

    companion object : SingletonInitializer<Z17BasePictureHeaders>()

    private var _h: Headers? = headers

    fun getHeaders() = this._h

    fun updateHeaders(newHeaders: Headers) {
        this._h = newHeaders
    }

    fun thereAreHeaders() = this._h != null

}