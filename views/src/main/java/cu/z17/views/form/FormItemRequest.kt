package cu.z17.views.form

import androidx.compose.runtime.Immutable
import cu.z17.views.form.UtilRegex.CAN_BE_EMPTY

@Immutable
data class FormItemRequest(
    val id: String,
    val type: FormItemType,
    val label: String,
    val description: String = "",
    val value: String,
    val okRegex: String = CAN_BE_EMPTY,
    val errorLabel: String = "",
    val limit: Int = Int.MAX_VALUE,
    val displaySize: Float = 1F,
    val selectionList: List<String> = emptyList(),
    val imageForm: ImageForm = ImageForm.ROUND_CORNERS
)

enum class ImageForm {
    CIRCLE,
    SQUARE,
    ROUND_CORNERS
}

object UtilRegex {
    const val CAN_BE_EMPTY = ""
    const val CANNOT_BE_EMPTY = "cannot_be_empty"
}