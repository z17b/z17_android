package cu.z17.views.utils

import java.util.*
import kotlin.math.abs

class ColorGenerator private constructor(private val mColors: List<Int>) {
    private val mRandom: Random = Random(System.currentTimeMillis())

    val randomColor: Int
        get() = mColors[mRandom.nextInt(mColors.size)]

    fun getColor(key: Any): Int {
        return mColors[abs(key.hashCode()) % mColors.size]
    }

    companion object {
        var DEFAULT: ColorGenerator
        var MATERIAL: ColorGenerator

        init {
            DEFAULT = create(
                listOf(
                    -0x1b39d2,
                    -0x98408c,
                    -0xa65d42,
                    -0xdf6c33,
                    -0x529d59,
                    -0x7fa87f
                )
            )
            MATERIAL = create(
                listOf(
                    -0x1a8c8d,
                    -0x1b39d2,
                    -0xff6d93,
                    -0x0066cc,
                    -0xb22f1f,
                    -0xb24954,
                    -0x7e387c,
                    -0x512a7f,
                    -0x2b1ea9,
                    -0x5e7781,
                    -0x6f5b52
                )
            )
        }

        fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }
    }
}
