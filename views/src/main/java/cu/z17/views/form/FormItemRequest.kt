package cu.z17.views.form

import androidx.compose.runtime.Immutable

@Immutable
data class FormItemRequest(
    val id: String,
    val type: FormItemType,
    val label: String,
    val description: String = "",
    val value: String,
    val nonErrorCondition: (String) -> Boolean = { true },
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