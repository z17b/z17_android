package cu.z17.android.ui.main

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import cu.z17.android.ui.theme.AppTheme
import cu.z17.views.inputText.Z17InputText
import cu.z17.views.label.Z17Label
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.scaffold.Z17BaseScaffold
import cu.z17.views.tab.Z17Tab
import cu.z17.views.textToggle.Z17TextToggle
import cu.z17.views.topBar.Z17TopBar
import cu.z17.views.utils.Z17BasePictureHeaders
import cu.z17.views.utils.Z17CoilDecoders

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContent {
            AppTheme {
                val sizeH = LocalConfiguration.current.screenHeightDp

                Z17CoilDecoders.createInstance { Z17CoilDecoders(applicationContext) }
                Z17BasePictureHeaders.createInstance {
                    Z17BasePictureHeaders(
                        headers = mapOf(
                            "Authorization" to "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDg4MjQwODksInRvRHVzSWQiOiJFZGR5IiwidXNlcm5hbWUiOiI1MzUyMTg5Mzc2IiwidmVyc2lvbiI6IjMwMDI0In0.YkpHKwJ6leAqmJc9Tkqfr5BDjciIKwNaJwXKPV26uGQ",
                            "User-Agent" to "ToDus 2.0.24 Pictures",
                            "Content-Type" to "application/json"
                        )
                    )
                }

                Z17BaseScaffold(
                    scaffoldState = rememberScaffoldState(),
                    isNestedActive = false,
                    noToolbar = false,
                    topBar = {
                        Z17TopBar(
                            title = {
                                Z17Label(
                                    text = "asdjanskdn",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            },
                            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
                                rememberTopAppBarState()
                            ),
                            showActions = false,
                            navHostController = rememberNavController(),
                            navigationClick = {

                            },
                            actions = {
                                IconButton(
                                    onClick = {

                                    }) {
                                    Z17BasePicture(
                                        modifier = Modifier
                                            .size(24.dp),
                                        source = Icons.Outlined.Save,
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                                    )
                                }
                            }
                        )
                    },
                    showFloatingActionBtn = false,
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {

                            },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Z17BasePicture(
                                source = Icons.Outlined.Check,
                                modifier = Modifier.size(25.dp),
                                description = "complete action btn",
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.background)
                            )
                        }
                    }
                ) { modifier ->
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // IMAGE
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((sizeH / 2).dp)
                        ) {
                            Z17BasePicture(
                                modifier = Modifier.fillMaxSize(),
                                source = "https://s3.todus.cu/todus/profile/f43d3fbabb78eb6d7b6975a7f646d8d7017db6b15439954ae48170eab1aa3812",
                                contentScale = ContentScale.Crop,
                                placeholder = MaterialTheme.colorScheme.secondary
                            )

                            CrossFade(targetState = false) { state ->
                                if (state) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = 0.5F
                                                )
                                            )
                                            .clickable {

                                            }
                                    ) {
                                        Z17BasePicture(
                                            modifier = Modifier
                                                .align(alignment = Alignment.Center)
                                                .size(24.dp),
                                            source = Icons.Outlined.Edit,
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
                                        )
                                    }
                                }
                            }
                        }

                        // NAME
                        CrossFade(targetState = false) { state ->
                            if (state) {
                                Z17InputText(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    value = "asdasda",
                                    onTextChange = {
                                    },
                                    labelText = "asdjasd",
                                    maxLines = 1,
                                    maxLength = 150,
                                    style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                                )
                            } else {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                        .defaultMinSize(minHeight = 74.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Z17Label(
                                        text = "asdasdasd",
                                        style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        val tabs =
                            listOf(
                                "asdas",
                                "wqeqd"
                            )

                        TabRow(
                            selectedTabIndex = 1,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Z17Tab(
                                title = tabs[0],
                                selected = 1 == 0,
                                onClick = {
                                }
                            )

                            Z17Tab(
                                title = "${tabs[1]} ($1)",
                                selected = false,
                                onClick = {
                                }
                            )
                        }

                        var tabIndex by remember { mutableIntStateOf(0) }

                        // INFORMATION AND CONFIGURATION
                        Column(modifier = Modifier.height(IntrinsicSize.Max)) {
                            when (tabIndex) {
                                0 -> {
                                    // Information section
                                    CrossFade(targetState = false) { state ->
                                        if (state) {
                                            Z17InputText(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(20.dp),
                                                value = "asdfasfasfasfasfasfas",
                                                onTextChange = {
                                                },
                                                labelText = "asdasdasdasd",
                                                minLines = 3,
                                                maxLines = 4,
                                                maxLength = 150
                                            )
                                        } else {
                                            RichTitleDescBtn(
                                                image = Icons.Outlined.Info,
                                                content = "asdasdasd",
                                                title = "asdasdasd"
                                            ) {}
                                        }
                                    }

                                    TitleDescBtn(
                                        image = Icons.Outlined.Lock,
                                        title = "asdasdasd",
                                        content = "asdasd",
                                    ) {

                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            horizontal = 20.dp,
                                            vertical = 5.dp
                                        ),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )

                                    Z17TextToggle(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        state = false,
                                        onChange = {
                                        },
                                        leading = {
                                            Z17BasePicture(
                                                modifier = Modifier
                                                    .padding(20.dp)
                                                    .size(24.dp),
                                                source = Icons.Outlined.NotificationsNone,
                                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                                            )
                                        },
                                        label = "asdasdasd"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun <T> CrossFade(
    targetState: T,
    content: @Composable (T) -> Unit,
) {
    Crossfade(
        targetState = targetState,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ), label = "crossfade"
    ) {
        content(it)
    }
}