package cu.z17.views.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@SuppressLint("ClickableViewAccessibility")
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@Composable
fun Z17MapView(
    modifier: Modifier = Modifier,
    startLatitude: Double = 22.261369,
    startLongitude: Double = -79.577868,
    startZoom: Double = 11.0,
    canSelectCustom: Boolean = false,
    centerOn: String = "",
    currentPoint: Z17MapMarker?,
    otherPoints: List<Z17MapMarker>,
    updateMarker: (Z17MapMarker) -> Unit
) {
    val mapView = rememberMapViewWithLifecycle(
        startLatitude = startLatitude,
        startLongitude = startLongitude,
        startZoom = startZoom
    )

    val context = LocalContext.current

    var isCentered by remember {
        mutableStateOf(false)
    }

    val markers = remember {
        ArrayList<Marker>()
    }

    val accuracyOverlays = remember {
        mutableMapOf<String, AccuracyOverlay>()
    }

    fun centerMap(mapView: MapView, location: Location) {
        val point = GeoPoint(
            location.latitude,
            location.longitude,
            location.altitude
        )

        val zoom = when {
            location.accuracy < 100 -> 18
            location.accuracy < 500 -> 16
            location.accuracy < 1000 -> 14
            else -> 13
        }

        if (mapView.zoomLevelDouble < zoom) {
            mapView.controller.animateTo(point, zoom.toDouble(), 1000)
        } else {
            mapView.controller.animateTo(point)
        }
    }

    fun paintCurrentMarker(mapView: MapView, z17MapMarker: Z17MapMarker, context: Context) {
        val point =
            GeoPoint(
                z17MapMarker.latitude,
                z17MapMarker.longitude,
                z17MapMarker.altitude
            )

        var mMarker = markers.find { it.id == z17MapMarker.id }

        if (mMarker == null) {
            mMarker = Marker(mapView)
            mMarker.id = z17MapMarker.id
            mMarker.setInfoWindow(null)
            markers.add(mMarker)

            context.let {
                val hexColor = "#2196F3"
                val currentColor = Color.parseColor(hexColor)

                val drawable =
                    ContextCompat.getDrawable(context, cu.z17.views.R.drawable.location_pointer)
                drawable?.setTint(currentColor)

                mMarker.icon = drawable
            }
            mMarker.position = point
            mMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        } else {
            mapView.overlays.remove(mMarker)
            mMarker.position = point
        }

        mMarker.setOnMarkerClickListener { _, _ ->
            z17MapMarker.location?.let {
                centerMap(mapView, z17MapMarker.location)
            }
            false
        }

        if (!isCentered && centerOn.isEmpty()) {
            z17MapMarker.location?.let {
                centerMap(mapView, z17MapMarker.location)
            }
            isCentered = true
        }

        if (accuracyOverlays[z17MapMarker.id] != null) {
            mapView.overlays.remove(accuracyOverlays[z17MapMarker.id])
        }
        val accuracyHexColor = "#1976D2"
        val accuracyColor = Color.parseColor(accuracyHexColor)

        accuracyOverlays[z17MapMarker.id] = AccuracyOverlay(
            point, z17MapMarker.accuracy ?: 0F, accuracyColor
        )

        mapView.overlays.add(accuracyOverlays[z17MapMarker.id])

        mapView.overlays.add(mMarker)
        mapView.invalidate()
    }

    fun paintOtherMarker(mapView: MapView, z17MapMarker: Z17MapMarker, context: Context) {
        val point =
            GeoPoint(
                z17MapMarker.latitude,
                z17MapMarker.longitude,
                z17MapMarker.altitude
            )

        var friendMarker = markers.find { it.id == z17MapMarker.id }

        if (friendMarker == null) {
            friendMarker = Marker(mapView)
            friendMarker.id = z17MapMarker.id
            friendMarker.setInfoWindow(null)
            markers.add(friendMarker)
        }

        context.let {
            val hexColor = "#F44336"
            val otherColor = Color.parseColor(hexColor)

            val drawable =
                ContextCompat.getDrawable(
                    context,
                    if (z17MapMarker.icon != 0) z17MapMarker.icon else cu.z17.views.R.drawable.location_pointer
                )
            drawable?.setTint(otherColor)
            friendMarker.icon = drawable

        }
        friendMarker.position = point
        friendMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        if (!isCentered && centerOn == z17MapMarker.id) {
            z17MapMarker.location?.let {
                centerMap(mapView, z17MapMarker.location)
            }
            isCentered = true
        }

        mapView.overlays.add(friendMarker)
        mapView.invalidate()
    }

    fun paintCustomMarker(mapView: MapView, z17MapMarker: Z17MapMarker, context: Context) {
        val point =
            GeoPoint(
                z17MapMarker.latitude,
                z17MapMarker.longitude,
                z17MapMarker.altitude
            )

        var customMarker = markers.find { it.id == z17MapMarker.id }

        if (customMarker == null) {
            customMarker = Marker(mapView)
            customMarker.id = z17MapMarker.id
            customMarker.setInfoWindow(null)
            markers.add(customMarker)
        }

        context.let {
            val hexColor = "#000000"
            val otherColor = Color.parseColor(hexColor)

            val drawable =
                ContextCompat.getDrawable(context, cu.z17.views.R.drawable.custom_location_pointer)
            drawable?.setTint(otherColor)
            customMarker.icon = drawable
        }
        customMarker.position = point
        customMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        if (!isCentered && centerOn == z17MapMarker.id) {
            z17MapMarker.location?.let {
                centerMap(mapView, z17MapMarker.location)
            }
            isCentered = true
        }

        mapView.overlays.add(customMarker)
        mapView.invalidate()
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView
        },
        update = { mV ->
            val mReceive = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    return false
                }

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    if (canSelectCustom) {
                        p?.let {
                            mV.controller.animateTo(p)
                            updateMarker(
                                Z17MapMarker(
                                    id = "toSend",
                                    latitude = p.latitude,
                                    longitude = p.longitude,
                                    altitude = p.altitude
                                )
                            )
                        }

                        return true
                    }
                    return false
                }
            }
            val overlayEvents = MapEventsOverlay(mReceive)
            mV.overlays.add(overlayEvents)
            mV.invalidate()

            currentPoint?.let { z17MapMarker ->
                paintCurrentMarker(mV, z17MapMarker, context)
            }

            otherPoints.forEach { z17MapMarker ->
                if (z17MapMarker.id == "toSend") {
                    paintCustomMarker(mV, z17MapMarker, context)
                } else paintOtherMarker(mV, z17MapMarker, context)
            }
        }
    )
}


@Composable
fun rememberMapViewWithLifecycle(
    startLatitude: Double,
    startLongitude: Double,
    startZoom: Double
): MapView {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            id = cu.z17.views.R.id.map
            this.setTileSource(
                XYTileSource(
                    "OSMPublicTransport",
                    0,
                    20,
                    256,
                    ".png",
                    arrayOf("https://map.todus.cu/styles/basic-preview/"),
                    "© OpenStreetMap contributors"
                )
            )

            this.controller.setCenter(GeoPoint(startLatitude, startLongitude, 10.0))
            this.controller.setZoom(startZoom)
            this.maxZoomLevel = 20.0
            this.minZoomLevel = 2.0
            this.overlayManager.tilesOverlay.loadingBackgroundColor = Color.TRANSPARENT
            this.overlayManager.tilesOverlay.loadingLineColor = Color.TRANSPARENT
            this.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            this.setScrollableAreaLimitDouble(BoundingBox(85.0, 180.0, -85.0, -180.0))
            this.isHorizontalMapRepetitionEnabled = false
            this.isVerticalMapRepetitionEnabled = false
            this.setScrollableAreaLimitLatitude(
                MapView.getTileSystem().maxLatitude,
                MapView.getTileSystem().minLatitude, 0
            )
            this.setMultiTouchControls(true)
        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
    }