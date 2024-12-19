package zone.ien.map.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController
import zone.ien.map.data.Candidate
import zone.ien.map.data.RouteResult

interface SwiftUIFactory {

    companion object {
        var shared: SwiftUIFactory? = null
    }

    fun makeController(
        currentLatLng: MapLatLng,
        selectedLatLng: MapLatLng,
        onSelectLatLng: (MapLatLng) -> Unit,
        markers: List<Triple<Int, Double, Double>>,
        routes: List<MapLatLng>,
        candidates: List<Candidate>,
        selectedIndex: Int,
        onMapClick: (MapPointF, MapLatLng) -> Unit
    ) : UIViewController
}