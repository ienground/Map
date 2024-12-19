package zone.ien.map.utils.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import zone.ien.map.data.Candidate
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
    markers: List<Triple<Int, Double, Double>>,
    routes: List<MapLatLng>,
    candidates: List<Candidate>,
    selectedIndex: Int,
    onMapClick: (MapPointF, MapLatLng) -> Unit,
) {
    SwiftUIFactory.shared?.let {
        LaunchedEffect(Unit) {

        }
        LaunchedEffect(selectedLatLng) {

        }

        LaunchedEffect(selectedIndex, candidates) {

        }

        UIKitViewController(
            factory = {
                it.makeController(
                    currentLatLng,
                    selectedLatLng,
                    onSelectLatLng,
                    markers,
                    routes,
                    candidates,
                    selectedIndex,
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