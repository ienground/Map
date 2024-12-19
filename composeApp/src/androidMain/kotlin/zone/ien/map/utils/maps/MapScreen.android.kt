package zone.ien.map.utils.maps

import android.graphics.PointF
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import com.naver.maps.map.overlay.ArrowheadPathOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import map.composeapp.generated.resources.start_point
import org.jetbrains.compose.resources.stringResource
import zone.ien.map.R
import zone.ien.map.TAG
import zone.ien.map.data.RouteResult
import zone.ien.map.utils.Dlog
import zone.ien.map.utils.MapLatLng
import zone.ien.map.utils.MapPointF
import map.composeapp.generated.resources.Res
import zone.ien.map.data.Candidate

@OptIn(ExperimentalNaverMapApi::class)
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

    LaunchedEffect(selectedIndex, candidates) {
        candidates.getOrNull(selectedIndex)?.let {
            it.routes.let { list ->
                var minLatitude = Double.MAX_VALUE
                var minLongitude = Double.MAX_VALUE
                var maxLatitude = Double.MIN_VALUE
                var maxLongitude = Double.MIN_VALUE

                list?.forEach { latLng ->

                    if (minLatitude > latLng.latitude) minLatitude = latLng.latitude
                    if (maxLatitude < latLng.latitude) maxLatitude = latLng.latitude
                    if (minLongitude > latLng.longitude) minLongitude = latLng.longitude
                    if (maxLongitude < latLng.longitude) maxLongitude = latLng.longitude
                }

                cameraPositionState.animate(CameraUpdate.fitBounds(LatLngBounds(LatLng(minLatitude, minLongitude), LatLng(maxLatitude, maxLongitude)), 100))
            }
        }
    }

    NaverMap(
        cameraPositionState = cameraPositionState,
        onMapClick = { point, latLng -> onMapClick(MapPointF(point.x, point.y), MapLatLng(latLng.latitude, latLng.longitude)) },
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
        if (candidates.isNotEmpty() && selectedIndex >= 0 && candidates.lastIndex >= selectedIndex) {
            candidates[selectedIndex]?.let { candidate ->
                candidate.routes?.let { list ->
                    ArrowheadPathOverlay(
                        color = MaterialTheme.colorScheme.tertiary,
                        outlineWidth = 0.dp,
                        coords = list.map { LatLng(it.latitude, it.longitude) },
                    )
                    Marker(
                        icon = MarkerIcons.BLUE,
                        captionText = stringResource(Res.string.start_point),
                        state = MarkerState(position = LatLng(list.first().latitude, list.first().longitude))
                    )
                }
                Marker(
                    icon = MarkerIcons.RED,
                    captionText = candidate.routeResult.waypoint,
                    state = MarkerState(position = LatLng(candidate.routeResult.latLng.latitude, candidate.routeResult.latLng.longitude))
                )
            }
        }
    }
}