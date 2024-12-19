package zone.ien.map.data

import zone.ien.map.utils.MapLatLng

data class Candidate(
    val routeResult: RouteResult,
    val routes: List<MapLatLng>?
)