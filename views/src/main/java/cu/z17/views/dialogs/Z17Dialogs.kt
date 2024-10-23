package cu.z17.views.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cu.z17.views.button.Z17PrimaryDialogButton
import cu.z17.views.button.Z17SecondaryDialogButton
import cu.z17.views.label.Z17Label

@Composable
fun Z17DialogWithActions(
    openDialog: Boolean,
    onClose: () -> Unit,
    yesAction: () -> Unit,
    noAction: () -> Unit = {},
    title: String,
    description: String,
    yes: String = stringResource(id = android.R.string.ok),
    no: String = stringResource(id = android.R.string.cancel)
) {
    if (openDialog)
        AlertDialog(
            modifier = Modifier.safeContentPadding(),
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                onClose()
            },
            title = {
                Z17Label(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Z17Label(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Z17PrimaryDialogButton(
                    text = yes,
                    onClick = {
                        yesAction()
                        onClose()
                    })
            },
            dismissButton = {
                Z17SecondaryDialogButton(
                    text = no,
                    onClick = {
                        onClose()
                        noAction()
                    })
            }
        )
}

@Composable
fun Z17DialogWithActionsCustom(
    openDialog: Boolean,
    onClose: () -> Unit,
    yesAction: () -> Unit,
    noAction: () -> Unit = {},
    title: String,
    description: String,
    yes: String = stringResource(id = android.R.string.ok),
    no: String = stringResource(id = android.R.string.cancel)
) {
    if (openDialog)
        AlertDialog(
            modifier = Modifier.safeContentPadding(),
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                onClose()
            },
            title = {
                Z17Label(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Z17Label(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Z17PrimaryDialogButton(
                    text = yes,
                    onClick = {
                        yesAction()
                        onClose()
                    })
            },
            dismissButton = {
                Z17SecondaryDialogButton(
                    text = no,
                    onClick = {
                        noAction()
                        onClose()
                    })
            }
        )
}

@Composable
fun Z17CustomDialog(
    openDialog: Boolean,
    onClose: () -> Unit,
    content: @Composable (close: () -> Unit) -> Unit
) {
    if (openDialog)
        Dialog(
            onDismissRequest = {
                onClose()
            }
        ) {
            Box(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(15.dp)
                    )
            ) {
                content {
                    onClose()
                }
            }
        }
}