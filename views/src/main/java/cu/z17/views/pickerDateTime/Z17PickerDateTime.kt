package cu.z17.views.pickerDateTime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cu.z17.views.button.Z17PrimaryDialogButton
import cu.z17.views.button.Z17SecondaryDialogButton
import cu.z17.views.dialogs.Z17CustomDialog
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Z17PickerDateTime(
    show: Boolean,
    onCancel: () -> Unit,
    onSelected: (year: Int, month: Int, day: Int, hour: Int, minutes: Int) -> Unit
) {
    val tomorrow = remember {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = Date().time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.timeInMillis
    }

    val showDatePickerDialog = remember {
        mutableStateOf(true)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = tomorrow
    )

    val showTimePickerDialog = remember {
        mutableStateOf(false)
    }

    val timePickerState = rememberTimePickerState()

    if (show) {
        Z17CustomDialog(
            openDialog = showDatePickerDialog.value,
            onClose = { showDatePickerDialog.value = false }
        ) {
            Column {
                DatePicker(
                    modifier = Modifier.fillMaxWidth(),
                    state = datePickerState,
                    headline = null,
                    title = null
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Z17SecondaryDialogButton(
                        text = stringResource(id = cu.z17.views.R.string.cancel),
                        onClick = {
                            showDatePickerDialog.value = false
                            onCancel()
                        })

                    Spacer(modifier = Modifier.width(10.dp))

                    Z17PrimaryDialogButton(
                        text = stringResource(id = cu.z17.views.R.string.continue_label),
                        onClick = {
                            showDatePickerDialog.value = false
                            showTimePickerDialog.value = true
                        })
                }
            }
        }

        Z17CustomDialog(
            openDialog = showTimePickerDialog.value,
            onClose = { showTimePickerDialog.value = false }
        ) {
            Column {
                TimePicker(
                    state = timePickerState
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Z17SecondaryDialogButton(
                        text = stringResource(id = cu.z17.views.R.string.cancel),
                        onClick = {
                            showTimePickerDialog.value = false
                            onCancel()
                        })

                    Spacer(modifier = Modifier.width(10.dp))

                    Z17PrimaryDialogButton(
                        text = stringResource(id = cu.z17.views.R.string.ok),
                        onClick = {
                            showTimePickerDialog.value = false

                            val calendar = Calendar.getInstance()
                            calendar.time = Date(datePickerState.selectedDateMillis ?: tomorrow)
                            val day = calendar.get(Calendar.DAY_OF_MONTH)
                            val month = calendar.get(Calendar.MONTH) + 1
                            val year = calendar.get(Calendar.YEAR)

                            onSelected(
                                year,
                                month,
                                day,
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        })
                }
            }
        }

    }
}