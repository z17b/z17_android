package cu.z17.views.spinner

data class Z17NameIdItem(
    var stringId: String = "", var intId: Int = -1, var text: String
)

abstract class Z17NameItem {
    abstract fun getTitle(): String
}