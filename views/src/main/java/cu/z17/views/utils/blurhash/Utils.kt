package cu.z17.views.utils.blurhash

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.withSign

internal object Utils {
    @JvmStatic
    fun sRGBToLinear(value: Long): Double {
        val v = value / 255.0
        return if (v <= 0.04045) {
            v / 12.92
        } else {
            ((v + 0.055) / 1.055).pow(2.4)
        }
    }

    @JvmStatic
    fun linearTosRGB(value: Double): Long {
        val v = 0.0.coerceAtLeast(1.0.coerceAtMost(value))
        return if (v <= 0.0031308) {
            (v * 12.92 * 255 + 0.5).toLong()
        } else {
            ((1.055 * Math.pow(v, 1 / 2.4) - 0.055) * 255 + 0.5).toLong()
        }
    }

    @JvmStatic
    fun signPow(`val`: Double, exp: Double): Double {
        return abs(`val`).pow(exp).withSign(`val`)
    }

    @JvmStatic
    fun max(values: Array<DoubleArray>, from: Int, endExclusive: Int): Double {
        var result = Double.NEGATIVE_INFINITY
        for (i in from until endExclusive) {
            for (j in values[i].indices) {
                val value = values[i][j]
                if (value > result) {
                    result = value
                }
            }
        }
        return result
    }
}