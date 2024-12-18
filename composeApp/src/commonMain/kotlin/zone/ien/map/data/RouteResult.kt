package zone.ien.map.data

import zone.ien.map.utils.MapLatLng

data class RouteResult(
    val waypoint: String = "",
    val latLng: MapLatLng = MapLatLng(0.0, 0.0),
    val distance: Int = 0,
    val duration: Int = 0,
    val taxiFare: Int = 0,
    val tollFare: Int = 0
)
