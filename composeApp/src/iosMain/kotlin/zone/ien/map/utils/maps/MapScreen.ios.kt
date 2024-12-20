package zone.ien.map.utils.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import zone.ien.map.TAG
import zone.ien.map.data.Candidate
import zone.ien.map.utils.Dlog
import zone.ien.map.utils.MapLatLng
import zone.ien.map.utils.MapPointF
import zone.ien.map.utils.SwiftUIFactory

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapScreen(
    modifier: Modifier,
    currentLatLng: MapLatLng,
    selectedLatLng: MapLatLng,
    onSelectLatLng: (MapLatLng) -> Unit,
    markers: SnapshotStateList<Triple<Int, Double, Double>>,
    routes: SnapshotStateList<MapLatLng>,
    candidates: SnapshotStateList<Candidate>,
    selectedIndex: Int,
    onMapClick: (MapPointF, MapLatLng) -> Unit,
) {
    SwiftUIFactory.shared?.let {

        it.updateMarkers(markers)

        LaunchedEffect(currentLatLng) { it.updateCurrentLatLng(currentLatLng) }
        LaunchedEffect(selectedLatLng) {
            it.updateSelectedLatLng(selectedLatLng)
        }
        LaunchedEffect(markers) { it.updateMarkers(markers); Dlog.d(TAG, "add marker: ${markers}") }
        LaunchedEffect(routes) { it.updateRoutes(routes) }
        LaunchedEffect(candidates) { it.updateCandidates(candidates) }
        LaunchedEffect(selectedIndex) {
            it.updateSelectedIndex(selectedIndex)
        }

        LaunchedEffect(selectedIndex, candidates) {

        }

        UIKitViewController(
            factory = {
                it.makeController(
                    onSelectLatLng,
                    onMapClick
                )
            },
            modifier = modifier,
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
        )
    }
}