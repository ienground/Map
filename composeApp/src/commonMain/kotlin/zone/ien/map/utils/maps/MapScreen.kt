package zone.ien.map.utils.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapScreen(
    modifier: Modifier = Modifier,
    currentLatLng: Pair<Double, Double>,
    selectedLatLng: Pair<Double, Double>?
//    markers: List<Pair<Double, Double>> = listOf()
)