package cu.z17.views.map

import android.location.Location

class Z17MapMarker(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val location: Location? = null,
    val accuracy: Float? = null,
    val icon: Int = cu.z17.views.R.drawable.location_pointer
)