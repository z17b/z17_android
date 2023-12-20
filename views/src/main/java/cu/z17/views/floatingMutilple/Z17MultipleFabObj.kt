package cu.z17.views.floatingMutilple

import androidx.compose.ui.graphics.Color

class Z17MultipleFabObj(
    val id: String,
    val label: String,
    val icon: Any,
    val buttonColor: Color? = null,
    val iconColor: Color? = null,
    val action: () -> Unit
)