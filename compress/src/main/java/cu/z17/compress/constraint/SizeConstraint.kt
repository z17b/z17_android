package cu.z17.compress.constraint

import cu.z17.compress.loadBitmap
import cu.z17.compress.overWrite
import java.io.File

class SizeConstraint(
        private val maxFileSize: Long,
        private val stepSize: Int,
        private val maxIteration: Int,
        private val minQuality: Int = 20
) : Constraint {
    private var iteration: Int = 0

    override fun isSatisfied(imageFile: File): Boolean {
        return imageFile.length() <= maxFileSize || iteration >= maxIteration
    }

    override fun satisfy(imageFile: File): File {
        iteration++
        val quality = (100 - iteration * stepSize).takeIf { it >= minQuality } ?: minQuality
        return overWrite(imageFile, loadBitmap(imageFile), quality = quality)
    }
}

fun Compression.size(maxFileSize: Long, stepSize: Int, maxIteration: Int) {
    constraint(SizeConstraint(maxFileSize, stepSize, maxIteration))
}