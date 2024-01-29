package cu.z17.compress.constraint

import java.io.File

 interface Constraint {
    fun isSatisfied(imageFile: File): Boolean

    fun satisfy(imageFile: File): File
}