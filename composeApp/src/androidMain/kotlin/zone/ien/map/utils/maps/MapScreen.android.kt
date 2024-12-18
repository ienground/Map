package zone.ien.map.utils.maps

import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.compose.ArrowheadPathOverlay
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapType
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerDefaults
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.naver.maps.map.overlay.OverlayImage
import zone.ien.map.R
import zone.ien.map.TAG
import zone.ien.map.utils.Dlog
import zone.ien.map.utils.MapLatLng
import zone.ien.map.utils.MapPointF

@OptIn(ExperimentalNaverMapApi::class)
@Composable
actual fun MapScreen(
    modifier: Modifier,
    currentLatLng: MapLatLng,
    selectedLatLng: MapLatLng,
    onSelectLatLng: (MapLatLng) -> Unit,
    markers: List<Triple<Int, Double, Double>>,
    routes: List<MapLatLng>,
    onMapClick: (MapPointF, MapLatLng) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(currentLatLng.latitude, currentLatLng.longitude), 16.0)
    }

    LaunchedEffect(selectedLatLng) {
        cameraPositionState.animate(CameraUpdate.toCameraPosition(CameraPosition(LatLng(selectedLatLng.latitude, selectedLatLng.longitude), cameraPositionState.position.zoom)))
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            onSelectLatLng(MapLatLng(cameraPositionState.position.target.latitude, cameraPositionState.position.target.longitude))
        }
    }

    NaverMap(
//        locationSource = rememberFusedLocationSource(),
//        properties = MapProperties(
//            locationTrackingMode = LocationTrackingMode.Follow
//        ),
        cameraPositionState = cameraPositionState,
        onMapClick = { point, latLng -> onMapClick(MapPointF(point.x, point.y), MapLatLng(latLng.latitude, latLng.longitude)) },
        onMapDoubleTab = { point, latLng -> Dlog.d(TAG, "onMapDoubleTab"); true },
        onMapLongClick = { point, latLng -> Dlog.d(TAG, "onMapLongClick"); true },
        onMapLoaded = { Dlog.d(TAG, "onMapLoaded") },
        onMapTwoFingerTap = { point, latLng -> Dlog.d(TAG, "onMapTwoFingerTap"); true},
        onOptionChange = { Dlog.d(TAG, "onOptionChange") },
        onLocationChange = { Dlog.d(TAG, "onLocationChange ${it}") },
        onSymbolClick = { Dlog.d(TAG, "onSymbolClick ${it}"); true },
        onIndoorSelectionChange = { Dlog.d(TAG, "onIndoorSelectionChange ${it}") },
        modifier = modifier
    ) {
        Marker(
            icon = OverlayImage.fromResource(R.drawable.ic_current_anchor),
            state = MarkerState(position = LatLng(currentLatLng.latitude, currentLatLng.longitude))
        )
        markers.forEach {
            Marker(
                state = MarkerState(position = LatLng(it.second, it.third))
            )
        }
        if (routes.isNotEmpty()) {
            ArrowheadPathOverlay(
                coords = routes.map { LatLng(it.latitude, it.longitude) },
            )
        }


    }
}