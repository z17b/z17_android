package cu.z17.views.permission

import androidx.compose.runtime.Immutable

@Immutable
data class PermissionsRequesterState(
    val permissionsResult: PermissionsResult = PermissionsResult.request(),
    val open: Boolean = false,
    val permissions: List<PermissionNeedIt> = emptyList(),
)