package zone.ien.map.utils.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapType
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import zone.ien.map.TAG
import zone.ien.map.utils.Dlog

@OptIn(ExperimentalNaverMapApi::class)
@Composable
actual fun MapScreen(
    modifier: Modifier,
    currentLatLng: Pair<Double, Double>,
    selectedLatLng: Pair<Double, Double>?
//    markers: List<Pair<Double, Double>>
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(currentLatLng.first, currentLatLng.second), 16.0)
    }

    LaunchedEffect(currentLatLng) {
        cameraPositionState.animate(CameraUpdate.toCameraPosition(CameraPosition(LatLng(currentLatLng.first, currentLatLng.second), 16.0)))
    }

    NaverMap(
        properties = MapProperties(),
        cameraPositionState = cameraPositionState,
        modifier = modifier
    ) {
        selectedLatLng?.let {
            Marker(
                state = MarkerState(position = LatLng(it.first, it.second))
            )
        }
    }
}