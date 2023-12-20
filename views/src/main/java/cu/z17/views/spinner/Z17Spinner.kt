package cu.z17.views.spinner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Z17Spinner(
    modifier: Modifier = Modifier,
    options: List<Z17NameIdItem>,
    handleSelection: (String) -> Unit,
    selectedOptionText: MutableState<String>,
    style: TextStyle = MaterialTheme.typography.titleSmall
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }) {

        Z17Label(
            text = selectedOptionText.value,
            style = style.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }) {
            options.forEachIndexed { index, selectionOption ->
                val shape =
                    if (options.size == 1) RoundedCornerShape(15.dp) else
                        when (index) {
                            0 -> RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                            options.size - 1 -> RoundedCornerShape(
                                bottomStart = 15.dp,
                                bottomEnd = 15.dp
                            )

                            else -> RectangleShape
                        }
                DropdownMenuItem(
                    modifier = Modifier
                        .defaultMinSize(1.dp)
                        .background(color = MaterialTheme.colorScheme.background)
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = shape
                        ),
                    onClick = {
                        selectedOptionText.value = selectionOption.text
                        expanded = false
                        handleSelection.invoke(selectionOption.stringId)
                    }, text = {
                        Z17Label(
                            text = selectionOption.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Z17Spinner2(
    modifier: Modifier = Modifier,
    options: List<T>,
    handleSelection: (T) -> Unit,
    selectedOption: T,
    getTitle: (T) -> String,
    style: TextStyle = MaterialTheme.typography.titleSmall
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }) {

        Z17Label(
            text = getTitle(selectedOption),
            style = style.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }) {
            options.forEachIndexed { index, selectionOption ->
                val shape =
                    if (options.size == 1) RoundedCornerShape(15.dp) else
                        when (index) {
                            0 -> RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                            options.size - 1 -> RoundedCornerShape(
                                bottomStart = 15.dp,
                                bottomEnd = 15.dp
                            )

                            else -> RectangleShape
                        }
                DropdownMenuItem(
                    modifier = Modifier
                        .defaultMinSize(1.dp)
                        .background(color = MaterialTheme.colorScheme.background)
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = shape
                        ),
                    onClick = {
                        expanded = false
                        handleSelection(selectionOption)
                    }, text = {
                        Z17Label(
                            text = getTitle(selectionOption),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    })
            }
        }
    }
}