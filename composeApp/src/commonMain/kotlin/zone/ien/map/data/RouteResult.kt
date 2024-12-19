package zone.ien.map.data

import zone.ien.map.constant.Pref
import zone.ien.map.utils.MapLatLng

data class RouteResult(
    val waypoint: String = "",
    val latLng: MapLatLng = MapLatLng(Pref.Default.CURRENT_LATITUDE, Pref.Default.CURRENT_LONGITUDE),
    val distance: Int = 0,
    val duration: Int = 0,
    val taxiFare: Int = 0,
    val tollFare: Int = 0
)
