package cu.z17.views.button

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cu.z17.views.label.Z17Label
import cu.z17.views.picture.Z17BasePicture

@Composable
fun Z17BaseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(15.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4F)
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(15.dp),
    focusManager: FocusManager = LocalFocusManager.current,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = {
            focusManager.clearFocus()
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun Z17BaseDialogButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(15.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4F)
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(
        top = 10.dp, bottom = 10.dp, end = 15.dp, start = 15.dp
    ),
    focusManager: FocusManager = LocalFocusManager.current,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = {
            focusManager.clearFocus()
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        content = content
    )
}


@Composable
fun Z17PrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    leading: @Composable () -> Unit = {},
    maxWidth: Boolean = false,
    enabled: Boolean = true,
) {
    Z17BaseButton(
        onClick = onClick,
        modifier = if (maxWidth) modifier.fillMaxWidth() else modifier,
        enabled = enabled,
    ) {
        leading()

        Z17Label(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
        )
    }
}

@Composable
fun Z17SecondaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    leading: @Composable () -> Unit = {},
    maxWidth: Boolean = false,
    enabled: Boolean = true,
) {
    Z17BaseButton(
        onClick = onClick,
        enabled = enabled,
        modifier = if (maxWidth) modifier.fillMaxWidth() else modifier,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onSurface),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        leading()

        Z17Label(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun Z17PrimaryDialogButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    leading: @Composable () -> Unit = {},
    maxWidth: Boolean = false,
    simple: Boolean = false,
) {
    if (simple) {
        Z17Label(
            modifier = modifier.clickable { onClick() },
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    } else
        Z17BaseDialogButton(
            onClick = onClick, modifier = if (maxWidth) modifier.fillMaxWidth() else modifier
        ) {
            leading()

            Z17Label(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
            )
        }
}

@Composable
fun Z17SecondaryDialogButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    leading: @Composable () -> Unit = {},
    maxWidth: Boolean = false,
    simple: Boolean = false,
) {
    if (simple) {
        Z17Label(
            modifier = modifier.clickable { onClick() },
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    } else
        Z17BaseDialogButton(
            onClick = onClick,
            modifier = if (maxWidth) modifier.fillMaxWidth()
            else modifier,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onSurface),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            leading()

            Z17Label(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
}

@Composable
fun Z17SnackbarButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    leading: @Composable () -> Unit = {},
    maxWidth: Boolean = false,
) {
    Z17BaseDialogButton(
        onClick = onClick, modifier = if (maxWidth) modifier.fillMaxWidth() else modifier
    ) {
        leading()

        Z17Label(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

enum class ButtonState { Pressed, Idle }

fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(
        if (buttonState == ButtonState.Pressed) 0.90f else 1f,
        label = "anb"
    )
    val alphaC by animateFloatAsState(
        if (buttonState == ButtonState.Pressed) 0.8f else 1f,
        label = "anb1"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            alpha = alphaC
        }
        .clickable(interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { })
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Modifier.invertedBounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(
        if (buttonState == ButtonState.Pressed) 1.4f else 1f,
        label = "anb"
    )
    val alphaC by animateFloatAsState(
        if (buttonState == ButtonState.Pressed) 1.2F else 1f,
        label = "anb1"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            alpha = alphaC
        }
        .clickable(interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { })
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

@Composable
fun Z17TitleDescBtn(image: Any, title: String, content: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Z17BasePicture(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                source = image,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Z17Label(
                text = title, style = MaterialTheme.typography.bodyLarge
            )
            Z17Label(text = content, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}
