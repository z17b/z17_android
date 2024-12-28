package cu.z17.views.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.layout.consumeWindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Z17BaseScaffold(
    topBar: @Composable (scrollBehavior: TopAppBarScrollBehavior) -> Unit = {},
    noToolbar: Boolean,
    isNestedActive: Boolean,
    showFloatingActionBtn: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    scaffoldState: ScaffoldState,
    bottomBar: @Composable (() -> Unit)? = null,
    content: @Composable (modifier: Modifier) -> Unit,
) {
    Box {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

        if (noToolbar)
            Scaffold(
                scaffoldState = scaffoldState,
                modifier = if (isNestedActive) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier,
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = showFloatingActionBtn,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        floatingActionButton()
                    }
                },
                drawerBackgroundColor = MaterialTheme.colorScheme.background,
                drawerContent = drawerContent,
                backgroundColor = backgroundColor,
                drawerGesturesEnabled = true,
            ) {
                content(
                    Modifier.padding(it)
                        .consumeWindowInsets(it)
                )
            }
        else
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    topBar(
                        scrollBehavior
                    )
                },
                modifier = if (isNestedActive) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier,
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = showFloatingActionBtn,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        floatingActionButton()
                    }
                },
                drawerBackgroundColor = MaterialTheme.colorScheme.background,
                drawerContent = drawerContent,
                backgroundColor = backgroundColor,
                drawerGesturesEnabled = true,
                bottomBar = bottomBar ?: {}
            ) {
                content(
                    Modifier.padding(it)
                        .consumeWindowInsets(it)
                )
            }
    }
}