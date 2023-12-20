package cu.z17.views.permission

data class PermissionsResult(val state: Int?) {
    companion object {
        fun granted(): PermissionsResult {
            return PermissionsResult(2)
        }

        fun notGranted(): PermissionsResult {
            return PermissionsResult(4)
        }

        fun loading(): PermissionsResult {
            return PermissionsResult(1)
        }

        fun request(): PermissionsResult {
            return PermissionsResult(0)
        }

        fun notRequest(): PermissionsResult {
            return PermissionsResult(3)
        }
    }
}

