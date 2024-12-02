package cu.z17.views.form

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cu.z17.views.button.Z17PrimaryButton
import cu.z17.views.loader.Z17SimpleCircleLoader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Z17Form(
    modifier: Modifier = Modifier,
    initialRequest: List<FormItemRequest>,
    submitBtn: @Composable (() -> Unit) -> Unit = { onSubmit ->
        Z17PrimaryButton(
            onClick = {
                onSubmit()
            },
            text = stringResource(cu.z17.views.R.string.submit),
            maxWidth = true
        )
    },
    onComplete: (List<FormItemRequest>) -> Unit
) {
    val request = remember {
        mutableStateListOf<FormItemRequest>()
    }

    val isLoading by remember {
        derivedStateOf { request.isEmpty() && initialRequest.isNotEmpty() }
    }

    fun handleChange(index: Int, newFormItemRequest: FormItemRequest) {
        request[index] = newFormItemRequest
    }

    var isChecked by remember {
        mutableStateOf(false)
    }

    fun checkForm() {
        var hasNonErrors = true

        request.forEach {
            hasNonErrors = hasNonErrors && it.nonErrorCondition(it.value)
        }

        isChecked = true

        if (hasNonErrors) onComplete(request)
    }

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Z17SimpleCircleLoader()
        }
    } else {
        val listState = rememberLazyListState()

        LazyColumn(
            modifier = modifier,
            state = listState
        ) {
            items(
                count = request.size,
                key = { request[it].id }
            ) { index ->
                FormItemView(
                    modifier = Modifier
                        .animateItemPlacement()
                        .padding(vertical = 10.dp),
                    formItemRequest = request[index],
                    isChecked = isChecked,
                    updateItem = { upd ->
                        handleChange(index, upd)
                        isChecked = false
                    }
                )
            }

            item {
                Box(modifier = Modifier.padding(vertical = 10.dp)) {
                    submitBtn.invoke {
                        checkForm()
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (request.isEmpty() && initialRequest.isNotEmpty()) request.addAll(initialRequest)
    }
}