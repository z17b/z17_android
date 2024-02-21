package cu.z17.views.utils

import cu.z17.singledi.SingletonInitializer
import okhttp3.Headers

class Z17BasePictureHeaders(private val headers: Map<String, String>? = null) {

    companion object : SingletonInitializer<Z17BasePictureHeaders>(){
        fun fromMapToHeaders(map: Map<String, String>?): Headers? {
            map?.let {
                val builder = Headers.Builder()

                it.forEach { (key, value) ->
                    builder.add(key, value)
                }

                return builder.build()
            }
            return null
        }
    }

    private var _h: Headers? = null

    fun getHeaders() = this._h

    fun updateHeaders(newHeaders: Map<String, String>) {
        this._h = fromMapToHeaders(newHeaders)
    }

    fun thereAreHeaders() = this._h != null

    init {
        headers?.let {
            updateHeaders(it)
        }
    }
}