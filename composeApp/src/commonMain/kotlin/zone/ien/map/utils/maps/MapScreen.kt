package zone.ien.map.utils.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import zone.ien.map.data.Candidate
import zone.ien.map.data.RouteResult
import zone.ien.map.utils.MapLatLng
import zone.ien.map.utils.MapPointF

@Composable
expect fun MapScreen(
    modifier: Modifier = Modifier,
    currentLatLng: MapLatLng,
    selectedLatLng: MapLatLng,
    onSelectLatLng: (MapLatLng) -> Unit,
    markers: List<Triple<Int, Double, Double>> = listOf(),
    routes: List<MapLatLng> = listOf(),
    candidates: List<Candidate>,
    selectedIndex: Int,
    onMapClick: (MapPointF, MapLatLng) -> Unit = { _, _ -> },
)

object MarkerType {
    const val SELECTED = 0
    const val START = 1
    const val DEST = 2
    const val ETC = -1
}