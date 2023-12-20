package cu.z17.views.scaffold

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Z17BaseScaffold(
    topBar: @Composable (scrollBehavior: TopAppBarScrollBehavior) -> Unit = {},
    noToolbar: Boolean,
    isNestedActive: Boolean,
    floatingActionButton: @Composable () -> Unit = {},
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    scaffoldState: ScaffoldState,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (modifier: Modifier) -> Unit
) {
    Box {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

        if (!noToolbar)
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    topBar(
                        scrollBehavior
                    )
                },
                modifier = if (isNestedActive) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier,
                floatingActionButton = floatingActionButton,
                drawerBackgroundColor = MaterialTheme.colorScheme.background,
                drawerContent = drawerContent,
                backgroundColor = backgroundColor,
                drawerGesturesEnabled = true,
                bottomBar = bottomBar
            ) {
                content(Modifier.padding(it))
            }
        else
            Scaffold(
                scaffoldState = scaffoldState,
                modifier = if (isNestedActive) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier,
                floatingActionButton = floatingActionButton,
                drawerBackgroundColor = MaterialTheme.colorScheme.background,
                drawerContent = drawerContent,
                backgroundColor = backgroundColor,
                drawerGesturesEnabled = true,
            ) {
                content(Modifier.padding(it))
            }
    }
}