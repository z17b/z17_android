package cu.z17.views.topBar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Z17TopBar(
    title: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    showActions: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    showNavigation: Boolean = true,
    navHostController: NavHostController,
    navigationClick: () -> Unit = { navHostController.navigateUp() },
    navigationIcon: ImageVector = Icons.Outlined.ArrowBackIos,
    navigationIconColor: Color = MaterialTheme.colorScheme.onBackground,
    containerColor: Color = MaterialTheme.colorScheme.background,
    elevation: Dp = 1.dp
) {
    TopAppBar(
        modifier = Modifier.shadow(elevation = elevation),
        title = title,
        actions = if (showActions) {
            actions
        } else {
            {}
        },
        navigationIcon = {
            if (showNavigation) {
                IconButton(
                    onClick = navigationClick,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                ) {
                    Icon(
                        navigationIcon,
                        tint = navigationIconColor,
                        contentDescription = ""
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor
        ),
        scrollBehavior = scrollBehavior
    )
}