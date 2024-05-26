package cu.z17.views.inputText

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label

@Composable
fun Z17InputText(
    modifier: Modifier = Modifier,
    value: String,
    maxLines: Int = 1,
    minLines: Int = 1,
    labelText: String = "",
    error: String = "",
    onTextChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    maxLength: Int = 100,
    inputType: KeyboardType = KeyboardType.Text,
    errorColor: Color = Color(0xFFc62828),
    focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.onBackground,
    unableColor: Color = MaterialTheme.colorScheme.onSurface,
    labelTextColor: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    readOnly: Boolean = false,
    shape: Shape = RoundedCornerShape(15.dp),
    enabled: Boolean = true,
) {

    OutlinedTextField(
        value = value,
        enabled = enabled,
        onValueChange = { if (it.length <= maxLength) onTextChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = inputType),
        label = if (labelText.isEmpty()) null else {
            {
                Z17Label(
                    text = labelText,
                    style = style,
                    color = labelTextColor
                )
            }
        },
        isError = error.isNotEmpty(),
        supportingText = if (error.isNotEmpty()) {
            {
                Z17Label(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = errorColor
                )
            }
        } else null,
        maxLines = maxLines,
        minLines = minLines,
        readOnly = readOnly,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier,
        shape = shape,
        textStyle = style,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledBorderColor = unableColor,
            errorCursorColor = errorColor,
            errorLabelColor = errorColor,
            errorLeadingIconColor = errorColor,
            errorSupportingTextColor = errorColor,
            errorTrailingIconColor = errorColor
        ),
        visualTransformation = if (inputType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@Composable
fun Z17PasswordText(
    modifier: Modifier = Modifier,
    value: String,
    maxLines: Int = 1,
    minLines: Int = 1,
    labelText: String = "",
    error: String = "",
    onTextChange: (String) -> Unit,
    maxLength: Int = 100,
    inputType: KeyboardType = KeyboardType.Password,
    errorColor: Color = Color(0xFFc62828),
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    readOnly: Boolean = false,
    shape: Shape = RoundedCornerShape(15.dp),
    enabled: Boolean = true,
) {

    var isShowIn by remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = value,
        enabled = enabled,
        onValueChange = { if (it.length <= maxLength) onTextChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = inputType),
        label = if (labelText.isEmpty()) null else {
            {
                Z17Label(
                    text = labelText,
                    style = style,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        isError = error.isNotEmpty(),
        supportingText = if (error.isNotEmpty()) {
            {
                Z17Label(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = errorColor
                )
            }
        } else null,
        maxLines = maxLines,
        minLines = minLines,
        readOnly = readOnly,
        leadingIcon = {
            Icon(
                Icons.Outlined.Key,
                contentDescription = "",
                modifier = Modifier
                    .size(25.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        trailingIcon = {
            Icon(
                Icons.Outlined.RemoveRedEye,
                contentDescription = "",
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        isShowIn = !isShowIn
                    },
                tint = if (isShowIn) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5F
                )
            )
        },
        modifier = modifier,
        shape = shape,
        textStyle = style,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            errorCursorColor = errorColor,
            errorLabelColor = errorColor,
            errorLeadingIconColor = errorColor,
            errorSupportingTextColor = errorColor,
            errorTrailingIconColor = errorColor
        ),
        visualTransformation = if (!isShowIn) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@Composable
fun Z17InputTextFlat(
    modifier: Modifier = Modifier,
    value: String,
    maxLines: Int = 1,
    minLines: Int = 1,
    labelText: String = "",
    error: String = "",
    onTextChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    maxLength: Int = 100,
    inputType: KeyboardType = KeyboardType.Text,
    errorColor: Color = Color(0xFFc62828),
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    readOnly: Boolean = false,
    shape: Shape = RoundedCornerShape(15.dp),
    enabled: Boolean = true,
) {
    TextField(
        value = value,
        onValueChange = { if (it.length <= maxLength) onTextChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = inputType),
        placeholder = if (labelText.isEmpty()) null else {
            {
                Z17Label(
                    text = labelText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.background
                )
            }
        },
        isError = error.isNotEmpty(),
        supportingText = if (error.isNotEmpty()) {
            {
                Z17Label(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = errorColor
                )
            }
        } else null,
        maxLines = maxLines,
        minLines = minLines,
        readOnly = readOnly,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier,
        textStyle = style,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            errorCursorColor = errorColor,
            errorLabelColor = errorColor,
            errorLeadingIconColor = errorColor,
            errorSupportingTextColor = errorColor,
            errorTrailingIconColor = errorColor
        ),
        visualTransformation = if (inputType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None)
}