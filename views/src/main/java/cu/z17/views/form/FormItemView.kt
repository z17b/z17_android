package cu.z17.views.form

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cu.z17.views.form.UtilRegex.CANNOT_BE_EMPTY
import cu.z17.views.inputText.Z17InputText
import cu.z17.views.label.Z17Label
import cu.z17.views.picture.Z17BasePicture

@Composable
fun FormItemView(
    modifier: Modifier = Modifier,
    formItemRequest: FormItemRequest,
    isChecked: Boolean,
    updateItem: (FormItemRequest) -> Unit,
    cameraDisplaying: Pair<Int, String>?,
    getImage: () -> Unit,
    clearImage: () -> String
) {
    Box(modifier = modifier) {
        fun handleChange(newValue: String) {
            updateItem(formItemRequest.copy(value = newValue))
        }

        val showError by remember(isChecked) {
            derivedStateOf {
                if (formItemRequest.okRegex == "") return@derivedStateOf false
                if (formItemRequest.okRegex == CANNOT_BE_EMPTY) return@derivedStateOf isChecked && formItemRequest.value.isBlank()

                val okRegex = Regex(formItemRequest.okRegex)

                // esta chekeando y no se cumple la condicion del regex
                isChecked && !(okRegex.matches(formItemRequest.value))
            }
        }

        val showLabel by remember {
            derivedStateOf {
                formItemRequest.label.isNotBlank()
            }
        }

        val showDescription by remember {
            derivedStateOf {
                formItemRequest.description.isNotBlank()
            }
        }

        Column(modifier = modifier) {
            // TOP
            Column(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .padding(bottom = 5.dp)
            ) {
                if (showLabel)
                    Z17Label(
                        text = formItemRequest.label,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black)
                    )
                if (showDescription)
                    Z17Label(
                        text = formItemRequest.description,
                        style = MaterialTheme.typography.bodySmall
                    )
            }

            // BODY
            when (formItemRequest.type) {
                FormItemType.NUMBER -> {
                    Field(
                        modifier = Modifier.fillMaxWidth(formItemRequest.displaySize),
                        value = formItemRequest.value,
                        onValueChange = ::handleChange,
                        inputType = KeyboardType.Number,
                        limit = formItemRequest.limit
                    )
                }

                FormItemType.TEXT -> {
                    Field(
                        modifier = Modifier.fillMaxWidth(formItemRequest.displaySize),
                        value = formItemRequest.value,
                        onValueChange = ::handleChange,
                        inputType = KeyboardType.Text,
                        limit = formItemRequest.limit
                    )
                }

                FormItemType.IMAGE -> {
                    ImageFiled(
                        modifier = Modifier.fillMaxWidth(formItemRequest.displaySize),
                        value = formItemRequest.value,
                        onValueChange = ::handleChange,
                        onPeakRequest = getImage,
                        valueInSelection = cameraDisplaying?.second,
                        clearImage = clearImage,
                        imageForm = formItemRequest.imageForm,
                    )
                }

                FormItemType.LARGE_TEXT -> {
                    LargeField(
                        modifier = Modifier.fillMaxWidth(formItemRequest.displaySize),
                        value = formItemRequest.value,
                        onValueChange = ::handleChange,
                        inputType = KeyboardType.Text,
                        limit = formItemRequest.limit
                    )
                }

                FormItemType.LARGE_IMAGE -> {
                    LargeImageFiled(
                        modifier = Modifier.fillMaxWidth(formItemRequest.displaySize),
                        value = formItemRequest.value,
                        onValueChange = ::handleChange,
                        onPeakRequest = getImage,
                        valueInSelection = cameraDisplaying?.second,
                        clearImage = clearImage,
                        imageForm = formItemRequest.imageForm,
                    )
                }

                FormItemType.SIMPLE_SELECTION -> {
                    SingleSelection(
                        modifier = Modifier.fillMaxWidth(formItemRequest.displaySize),
                        value = formItemRequest.value,
                        onValueChange = ::handleChange,
                        selectionList = formItemRequest.selectionList
                    )
                }

                FormItemType.MULTIPLE_SELECTION -> {
                    MultipleSelection(
                        modifier = Modifier.fillMaxWidth(formItemRequest.displaySize),
                        value = formItemRequest.value,
                        onValueChange = ::handleChange,
                        selectionList = formItemRequest.selectionList
                    )
                }
            }

            // BOTTOM
            if (showError)
                Z17Label(
                    modifier = Modifier
                        .padding(start = 5.dp),
                    text = formItemRequest.errorLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )

            LaunchedEffect(Unit) {
                when (formItemRequest.type) {
                    FormItemType.SIMPLE_SELECTION -> {
                        if (formItemRequest.value == "") {
                            handleChange("0")
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
internal fun Field(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    inputType: KeyboardType,
    limit: Int
) {
    Z17InputText(
        modifier = modifier,
        value = value,
        onTextChange = onValueChange,
        inputType = inputType,
        maxLength = limit
    )
}

@Composable
internal fun LargeField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    inputType: KeyboardType,
    limit: Int
) {
    Z17InputText(
        modifier = modifier,
        value = value,
        onTextChange = onValueChange,
        inputType = inputType,
        maxLength = limit,
        minLines = 4,
        maxLines = 4
    )
}

@Composable
internal fun SingleSelection(
    modifier: Modifier = Modifier,
    selectionList: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = modifier) {
        selectionList.forEachIndexed { index, s ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = index == (value.toIntOrNull() ?: 0),
                    onClick = {
                        onValueChange(index.toString())
                    }
                )

                Spacer(Modifier.padding(horizontal = 5.dp))

                Z17Label(
                    text = s,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
internal fun MultipleSelection(
    modifier: Modifier = Modifier,
    selectionList: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = modifier) {
        fun handleAdd(index: Int) {
            val arr = value.split(",").mapNotNull { it.toIntOrNull() }
            val arrayList = ArrayList<Int>().apply { addAll(arr) }
            arrayList.add(index)

            onValueChange(arrayList.joinToString(","))
        }

        fun handleRemove(index: Int) {
            val arr = value.split(",").mapNotNull { it.toIntOrNull() }
            val arrayList = ArrayList<Int>().apply { addAll(arr) }
            arrayList.remove(index)

            onValueChange(arrayList.joinToString(","))
        }

        selectionList.forEachIndexed { index, s ->
            val isCheck by remember(value) {
                derivedStateOf {
                    value.split(",").mapNotNull { it.toIntOrNull() }.contains(index)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isCheck,
                    onCheckedChange = {
                        if (it) {
                            handleAdd(index)
                        } else {
                            handleRemove(index)
                        }
                    }
                )

                Spacer(Modifier.padding(horizontal = 5.dp))

                Z17Label(
                    text = s,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
internal fun ImageFiled(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onPeakRequest: () -> Unit,
    valueInSelection: String? = null,
    clearImage: () -> String,
    imageForm: ImageForm
) {

    Box(modifier) {
        Z17BasePicture(
            modifier = Modifier
                .size(100.dp)
                .clip(
                    shape = when (imageForm) {
                        ImageForm.CIRCLE -> CircleShape
                        ImageForm.SQUARE -> RectangleShape
                        ImageForm.ROUND_CORNERS -> RoundedCornerShape(8.dp)
                    }
                )
                .border(
                    1.dp, shape = when (imageForm) {
                        ImageForm.CIRCLE -> CircleShape
                        ImageForm.SQUARE -> RectangleShape
                        ImageForm.ROUND_CORNERS -> RoundedCornerShape(8.dp)
                    }, color = MaterialTheme.colorScheme.primary
                )
                .clickable {
                    onPeakRequest()
                },
            source = value,
            contentScale = if (value.isNotBlank()) ContentScale.Crop else ContentScale.Inside
        )

        LaunchedEffect(valueInSelection) {
            if (!valueInSelection.isNullOrBlank()) {
                onValueChange(clearImage())
            }
        }
    }
}

@Composable
internal fun LargeImageFiled(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onPeakRequest: () -> Unit,
    valueInSelection: String? = null,
    clearImage: () -> String,
    imageForm: ImageForm
) {
    Box(modifier) {
        Z17BasePicture(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .clip(
                    shape = when (imageForm) {
                        ImageForm.CIRCLE -> CircleShape
                        ImageForm.SQUARE -> RectangleShape
                        ImageForm.ROUND_CORNERS -> RoundedCornerShape(8.dp)
                    }
                )
                .border(
                    1.dp, shape = when (imageForm) {
                        ImageForm.CIRCLE -> CircleShape
                        ImageForm.SQUARE -> RectangleShape
                        ImageForm.ROUND_CORNERS -> RoundedCornerShape(8.dp)
                    }, color = MaterialTheme.colorScheme.primary
                )
                .clickable {
                    onPeakRequest()
                },
            source = value,
            contentScale = if (value.isNotBlank()) ContentScale.Crop else ContentScale.Inside
        )

        LaunchedEffect(valueInSelection) {
            if (!valueInSelection.isNullOrBlank()) {
                onValueChange(clearImage())
            }
        }
    }
}