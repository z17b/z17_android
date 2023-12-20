package cu.z17.views.permission

import android.Manifest
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import cu.z17.views.R
import cu.z17.views.dialogs.Z17CustomDialog
import cu.z17.views.dialogs.Z17DialogWithActions
import cu.z17.views.label.Z17Label
import cu.z17.views.label.Z17MarkdownLabel
import cu.z17.views.picture.Z17BasePicture
import cu.z17.views.textToggle.Z17TextToggle

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Z17PermissionCheckerAndRequester(
    modifier: Modifier = Modifier,
    initialPermissionsResult: PermissionsResult = PermissionsResult.request(),
    initialPermissions: List<PermissionNeedIt> = emptyList(),
    onGranted: () -> Unit,
    onClose: () -> Unit = {},
    packageName: String,
    stringContent: String? = ""
) {
    val permissionState = remember {
        mutableStateOf(
            PermissionsRequesterState(
                permissionsResult = initialPermissionsResult,
                permissions = initialPermissions
            )
        )
    }

    if (stringContent != null && permissionState.value.permissionsResult.state != 2) {
        Z17MarkdownLabel(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2F),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(20.dp),
            text = stringContent.ifEmpty { stringResource(id = R.string.required_permissions_were_not_granted) },
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            onClick = {
                permissionState.value = permissionState.value.copy(open = true)
            }
        )
    }

    // CONTACT
    val contactPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_CONTACTS
        )
    )

    // STORAGE
    val storagePermissionState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else listOf()
    )

    val mediaPermissionsState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    )

    val storageForApi30OrAbove = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else true

    // LOCATION
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    // READ SMS
    val readSmsPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
        )
    )

    // CALL
    val callPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CALL_PHONE
        )
    )

    // RECORD
    val recordPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.RECORD_AUDIO
        )
    )

    // CAMERA
    val cameraPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA
        )
    )

    // NOTIFICATIONS
    val notificationsPermissionsState =
        rememberMultiplePermissionsState(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else listOf()
        )

    LaunchedEffect(Unit) {
        var result = true

        permissionState.value.permissions.forEach {
            // CONTACT
            if (it == PermissionNeedIt.CONTACT && !contactPermissionsState.allPermissionsGranted) result =
                false

            // STORAGE
            if (it == PermissionNeedIt.STORAGE) {
                if (!storagePermissionState.allPermissionsGranted || !mediaPermissionsState.allPermissionsGranted || !storageForApi30OrAbove) {
                    result = false
                }
            }

            // LOCATION
            if (it == PermissionNeedIt.LOCATION && !locationPermissionsState.allPermissionsGranted) result =
                false

            // SMS
            if (it == PermissionNeedIt.READ_SMS && !readSmsPermissionsState.allPermissionsGranted) result =
                false

            // CALL
            if (it == PermissionNeedIt.CALLS && !callPermissionsState.allPermissionsGranted) result =
                false

            // RECORD
            if (it == PermissionNeedIt.RECORD_AUDIO && !recordPermissionsState.allPermissionsGranted) result =
                false

            // CAMERA
            if (it == PermissionNeedIt.CAMERA && !cameraPermissionsState.allPermissionsGranted) result =
                false

            // NOTIFICATIONS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (it == PermissionNeedIt.NOTIFICATIONS && !notificationsPermissionsState.allPermissionsGranted) result =
                    false
            }
        }

        if (result) {
            onGranted()
            permissionState.value =
                permissionState.value.copy(permissionsResult = PermissionsResult.granted())
        } else {
            permissionState.value =
                permissionState.value.copy(permissionsResult = PermissionsResult.notGranted())
        }

        if (!result)
            permissionState.value =
                permissionState.value.copy(open = true)
    }

    if (permissionState.value.open)
        Z17PermissionRequester(
            permissionState = permissionState.value,
            onClose = {
                permissionState.value = permissionState.value.copy(open = false)
                onClose()
            },
            onResult = {
                permissionState.value = permissionState.value.copy(permissionsResult = it)

                if (it.state == 2) {
                    permissionState.value = permissionState.value.copy(open = false)
                    onGranted()
                }
            },
            packageName = packageName
        )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Z17PermissionChecker(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    permissionState: PermissionsRequesterState,
    onResult: (PermissionsResult) -> Unit,
    content: @Composable (Modifier) -> Unit = {
        if (permissionState.permissionsResult.state == 4) {
            Z17MarkdownLabel(
                modifier = it
                    .fillMaxWidth()
                    .padding(20.dp)
                    .background(
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2F),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp),
                text = stringResource(id = R.string.required_permissions_were_not_granted),
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                onClick = onClick
            )
        }
    }
) {
    content(modifier)

    // CONTACT
    val contactPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_CONTACTS
        )
    )

    // STORAGE
    val storagePermissionState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else listOf()
    )

    val mediaPermissionsState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    )

    val storageForApi30OrAbove = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else true

    // LOCATION
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    // READ SMS
    val readSmsPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
        )
    )

    // CALL
    val callPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CALL_PHONE
        )
    )

    // RECORD
    val recordPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.RECORD_AUDIO
        )
    )

    // CAMERA
    val cameraPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA
        )
    )

    // NOTIFICATIONS
    val notificationsPermissionsState =
        rememberMultiplePermissionsState(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else listOf()
        )

    LaunchedEffect(Unit) {
        var result = true

        permissionState.permissions.forEach {
            // CONTACT
            if (it == PermissionNeedIt.CONTACT && !contactPermissionsState.allPermissionsGranted) result =
                false

            // STORAGE
            if (it == PermissionNeedIt.STORAGE) {
                if (!storagePermissionState.allPermissionsGranted || !mediaPermissionsState.allPermissionsGranted || !storageForApi30OrAbove) {
                    result = false
                }
            }

            // LOCATION
            if (it == PermissionNeedIt.LOCATION && !locationPermissionsState.allPermissionsGranted) result =
                false

            // SMS
            if (it == PermissionNeedIt.READ_SMS && !readSmsPermissionsState.allPermissionsGranted) result =
                false

            // CALL
            if (it == PermissionNeedIt.CALLS && !callPermissionsState.allPermissionsGranted) result =
                false

            // RECORD
            if (it == PermissionNeedIt.RECORD_AUDIO && !recordPermissionsState.allPermissionsGranted) result =
                false

            // CAMERA
            if (it == PermissionNeedIt.CAMERA && !cameraPermissionsState.allPermissionsGranted) result =
                false

            // NOTIFICATIONS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (it == PermissionNeedIt.NOTIFICATIONS && !notificationsPermissionsState.allPermissionsGranted) result =
                    false
            }
        }

        if (result)
            onResult(PermissionsResult.granted())
        else onResult(PermissionsResult.notGranted())
    }
}

@Composable
fun Z17PermissionRequester(
    permissionState: PermissionsRequesterState,
    onClose: () -> Unit,
    onResult: (PermissionsResult) -> Unit,
    packageName: String
) {
    Z17CustomDialog(
        openDialog = permissionState.open,
        onClose = onClose
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = {
                    onClose()
                },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(alignment = Alignment.End)
            ) {
                Icon(
                    Icons.Outlined.Close,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = ""
                )
            }

            Z17PermissionContainer(
                actualResult = permissionState.permissionsResult,
                permissions = permissionState.permissions,
                onResult = {
                    onResult(it)
                },
                packageName = packageName
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Z17PermissionContainer(
    modifier: Modifier = Modifier,
    actualResult: PermissionsResult,
    permissions: List<PermissionNeedIt> = emptyList(),
    onResult: (PermissionsResult) -> Unit,
    packageName: String
) {
    val context = LocalContext.current

    // CONTACT
    val contactPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_CONTACTS
        )
    )

    // STORAGE
    val storagePermissionState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else listOf()
    )

    val mediaPermissionsState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    )

    val storageForApi30OrAbove = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else true

    // LOCATION
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    // READ SMS
    val readSmsPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
        )
    )

    // CALL
    val callPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CALL_PHONE
        )
    )

    // RECORD
    val recordPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.RECORD_AUDIO
        )
    )

    // CAMERA
    val cameraPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA
        )
    )

    // NOTIFICATIONS
    val notificationsPermissionsState =
        rememberMultiplePermissionsState(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else listOf()
        )

    // Rationale dialog
    var showRationaleDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Z17Label(text = stringResource(id = R.string.required_permissions))

        Spacer(modifier = Modifier.height(30.dp))

        // CONTACTS
        if (permissions.isEmpty() || permissions.contains(PermissionNeedIt.CONTACT))
            BadgedBox(badge = {
                if (permissions.contains(PermissionNeedIt.CONTACT))
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color.Red)
                    }
            }) {
                Z17TextToggle(
                    state = contactPermissionsState.allPermissionsGranted,
                    onChange = {
                        if (it) {
                            if (!contactPermissionsState.shouldShowRationale)
                                contactPermissionsState.launchMultiplePermissionRequest()
                            else showRationaleDialog = true
                        }
                    },
                    label = stringResource(
                        id = R.string.contacts
                    ),
                    leading = {
                        Z17BasePicture(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            source = Icons.Outlined.Contacts,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                )
            }

        // STORAGE
        if (permissions.isEmpty() || permissions.contains(PermissionNeedIt.STORAGE))
            BadgedBox(badge = {
                if (permissions.contains(PermissionNeedIt.STORAGE))
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color.Red)
                    }
            }) {
                Z17TextToggle(
                    state = storagePermissionState.allPermissionsGranted && mediaPermissionsState.allPermissionsGranted && storageForApi30OrAbove,
                    onChange = {
                        if (it) {
                            if (!storagePermissionState.shouldShowRationale)
                                storagePermissionState.launchMultiplePermissionRequest()
                            else showRationaleDialog = true

                            if (!mediaPermissionsState.shouldShowRationale)
                                mediaPermissionsState.launchMultiplePermissionRequest()
                            else showRationaleDialog = true

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                if (!Environment.isExternalStorageManager()) {
                                    val intent = Intent()
                                    intent.action =
                                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                                    val uri = Uri.fromParts("package", context.packageName, null)
                                    intent.data = uri
                                    ContextCompat.startActivity(context, intent, null)
                                }
                            }
                        }
                    },
                    label = stringResource(
                        id = R.string.storage
                    ),
                    leading = {
                        Z17BasePicture(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            source = Icons.Outlined.Folder,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                )
            }

        // LOCATION
        if (permissions.isEmpty() || permissions.contains(PermissionNeedIt.LOCATION))
            BadgedBox(badge = {
                if (permissions.contains(PermissionNeedIt.LOCATION))
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color.Red)
                    }
            }) {
                Z17TextToggle(
                    state = locationPermissionsState.allPermissionsGranted,
                    onChange = {
                        if (it) {
                            if (!locationPermissionsState.shouldShowRationale)
                                locationPermissionsState.launchMultiplePermissionRequest()
                            else showRationaleDialog = true
                        }
                    },
                    label = stringResource(
                        id = R.string.location
                    ),
                    leading = {
                        Z17BasePicture(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            source = Icons.Outlined.LocationOn,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                )
            }

        // READ SMS
        if (permissions.isEmpty() || permissions.contains(PermissionNeedIt.READ_SMS))
            BadgedBox(badge = {
                if (permissions.contains(PermissionNeedIt.READ_SMS))
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color.Red)
                    }
            }) {
                Z17TextToggle(
                    state = readSmsPermissionsState.allPermissionsGranted,
                    onChange = {
                        if (it) {
                            if (!readSmsPermissionsState.shouldShowRationale)
                                readSmsPermissionsState.launchMultiplePermissionRequest()
                            else showRationaleDialog = true
                        }
                    },
                    label = stringResource(
                        id = R.string.read_sms
                    ),
                    leading = {
                        Z17BasePicture(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            source = Icons.Outlined.Sms,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                )
            }

        // CALL
        if (permissions.isEmpty() || permissions.contains(PermissionNeedIt.CALLS))
            BadgedBox(badge = {
                if (permissions.contains(PermissionNeedIt.CALLS))
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color.Red)
                    }
            }) {
                Z17TextToggle(
                    state = callPermissionsState.allPermissionsGranted,
                    onChange = {
                        if (it) {
                            if (!callPermissionsState.shouldShowRationale)
                                callPermissionsState.launchMultiplePermissionRequest()
                            else showRationaleDialog = true
                        }
                    },
                    label = stringResource(
                        id = R.string.calls
                    ),
                    leading = {
                        Z17BasePicture(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            source = Icons.Outlined.Call,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                )
            }

        // RECORD
        if (permissions.isEmpty() || permissions.contains(PermissionNeedIt.RECORD_AUDIO))
            BadgedBox(badge = {
                if (permissions.contains(PermissionNeedIt.RECORD_AUDIO))
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color.Red)
                    }
            }) {
                Z17TextToggle(
                    state = recordPermissionsState.allPermissionsGranted,
                    onChange = {
                        if (it) {
                            if (!recordPermissionsState.shouldShowRationale)
                                recordPermissionsState.launchMultiplePermissionRequest()
                            else showRationaleDialog = true
                        }
                    },
                    label = stringResource(
                        id = R.string.record
                    ),
                    leading = {
                        Z17BasePicture(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            source = Icons.Outlined.Mic,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                )
            }

        // CAMERA
        if (permissions.isEmpty() || permissions.contains(PermissionNeedIt.CAMERA))
            BadgedBox(badge = {
                if (permissions.contains(PermissionNeedIt.CAMERA))
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color.Red)
                    }
            }) {
                Z17TextToggle(
                    state = cameraPermissionsState.allPermissionsGranted,
                    onChange = {
                        if (it) {
                            if (!cameraPermissionsState.shouldShowRationale)
                                cameraPermissionsState.launchMultiplePermissionRequest()
                            else showRationaleDialog = true
                        }
                    },
                    label = stringResource(
                        id = R.string.camera
                    ),
                    leading = {
                        Z17BasePicture(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            source = Icons.Outlined.CameraAlt,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                )
            }

        // NOTIFICATIONS
        if (permissions.isEmpty() || permissions.contains(PermissionNeedIt.NOTIFICATIONS))
            BadgedBox(badge = {
                if (permissions.contains(PermissionNeedIt.NOTIFICATIONS))
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color.Red)
                    }
            }) {
                Z17TextToggle(
                    state = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationsPermissionsState.allPermissionsGranted
                    } else true,
                    onChange = {
                        if (it) {
                            if (!notificationsPermissionsState.shouldShowRationale)
                                notificationsPermissionsState.launchMultiplePermissionRequest()
                            else showRationaleDialog = true
                        }
                    },
                    label = stringResource(
                        id = R.string.notifications
                    ),
                    leading = {
                        Z17BasePicture(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            source = Icons.Outlined.Notifications,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                )
            }

        Z17DialogWithActions(
            openDialog = showRationaleDialog,
            onClose = {
                showRationaleDialog = false
            },
            yesAction = {
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                with(intent) {
                    data = Uri.fromParts("package", packageName, null)
                    addCategory(CATEGORY_DEFAULT)
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                    addFlags(FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                }

                context.startActivity(intent)
            },
            title = stringResource(id = R.string.dialog_title),
            description = stringResource(id = R.string.dialog_desc)
        )
    }

    LaunchedEffect(
        storagePermissionState.allPermissionsGranted,
        storageForApi30OrAbove,
        mediaPermissionsState.allPermissionsGranted,
        contactPermissionsState.allPermissionsGranted,
        locationPermissionsState.allPermissionsGranted,
        readSmsPermissionsState.allPermissionsGranted,
        callPermissionsState.allPermissionsGranted,
        recordPermissionsState.allPermissionsGranted,
        cameraPermissionsState.allPermissionsGranted
    ) {
        var result = true

        permissions.forEach {
            // CONTACT
            if (it == PermissionNeedIt.CONTACT && !contactPermissionsState.allPermissionsGranted) result =
                false

            // STORAGE
            if (it == PermissionNeedIt.STORAGE) {
                if (!storagePermissionState.allPermissionsGranted || !mediaPermissionsState.allPermissionsGranted || !storageForApi30OrAbove) {
                    result = false
                }
            }

            // LOCATION
            if (it == PermissionNeedIt.LOCATION && !locationPermissionsState.allPermissionsGranted) result =
                false

            // SMS
            if (it == PermissionNeedIt.READ_SMS && !readSmsPermissionsState.allPermissionsGranted) result =
                false

            // CALL
            if (it == PermissionNeedIt.CALLS && !callPermissionsState.allPermissionsGranted) result =
                false

            // RECORD
            if (it == PermissionNeedIt.RECORD_AUDIO && !recordPermissionsState.allPermissionsGranted) result =
                false

            // CAMERA
            if (it == PermissionNeedIt.CAMERA && !cameraPermissionsState.allPermissionsGranted) result =
                false

            // NOTIFICATIONS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (it == PermissionNeedIt.NOTIFICATIONS && !notificationsPermissionsState.allPermissionsGranted) result =
                    false
            }

        }

        Log.i("PERMISSIONS", "===================================")

        Log.i(
            "PERMISSION => STORAGE: ",
            "${storagePermissionState.allPermissionsGranted && storageForApi30OrAbove && mediaPermissionsState.allPermissionsGranted}"
        )
        Log.i("PERMISSION => CONTACT: ", "${contactPermissionsState.allPermissionsGranted}")
        Log.i("PERMISSION => LOC: ", "${locationPermissionsState.allPermissionsGranted}")
        Log.i("PERMISSION => SMS: ", "${readSmsPermissionsState.allPermissionsGranted}")
        Log.i("PERMISSION => CALL: ", "${callPermissionsState.allPermissionsGranted}")
        Log.i("PERMISSION => REC: ", "${recordPermissionsState.allPermissionsGranted}")
        Log.i("PERMISSION => CAM: ", "${cameraPermissionsState.allPermissionsGranted}")
        Log.i(
            "PERMISSION => NOT: ", "${
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationsPermissionsState.allPermissionsGranted
                } else true
            }"
        )

        Log.i(
            "RATIONALE => STORAGE: ",
            "${storagePermissionState.shouldShowRationale && mediaPermissionsState.shouldShowRationale}"
        )
        Log.i("RATIONALE => CONTACT: ", "${contactPermissionsState.shouldShowRationale}")
        Log.i("RATIONALE => LOC: ", "${locationPermissionsState.shouldShowRationale}")
        Log.i("RATIONALE => SMS: ", "${readSmsPermissionsState.shouldShowRationale}")
        Log.i("RATIONALE => CALL: ", "${callPermissionsState.shouldShowRationale}")
        Log.i("RATIONALE => REC: ", "${recordPermissionsState.shouldShowRationale}")
        Log.i("RATIONALE => CAM: ", "${cameraPermissionsState.shouldShowRationale}")
        Log.i(
            "RATIONALE => NOT: ", "${
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationsPermissionsState.shouldShowRationale
                } else true
            }"
        )

        Log.i("PERMISSION Result: ", "$result")

        if (actualResult.state != 3)
            if (result)
                onResult(PermissionsResult.granted())
            else onResult(PermissionsResult.notGranted())

        Log.i("PERMISSIONS", "===================================")
    }
}

enum class PermissionNeedIt {
    CONTACT,
    STORAGE,
    CAMERA,
    LOCATION,
    READ_SMS,
    CALLS,
    RECORD_AUDIO,
    NOTIFICATIONS
}