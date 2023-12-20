package cu.z17.views.dialogs

import androidx.lifecycle.ViewModel
import cu.z17.views.utils.Z17MutableListFlow

class Z17DialogViewModel: ViewModel() {
    val listResult = Z17MutableListFlow(emptyList<String>())

}