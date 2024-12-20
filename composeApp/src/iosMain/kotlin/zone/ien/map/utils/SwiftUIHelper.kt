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
        onSelectLatLng: (MapLatLng) -> Unit,
        onMapClick: (MapPointF, MapLatLng) -> Unit
    ) : UIViewController

    fun updateCurrentLatLng(
        currentLatLng: MapLatLng
    )

    fun updateSelectedLatLng(
        selectedLatLng: MapLatLng
    )

    fun updateMarkers(
        markers: List<Triple<Int, Double, Double>>
    )

    fun updateRoutes(
        routes: List<MapLatLng>
    )

    fun updateCandidates(
        candidates: List<Candidate>
    )

    fun updateSelectedIndex(
        selectedIndex: Int
    )
}