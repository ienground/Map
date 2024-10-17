package zone.ien.map.utils.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.naver.maps.map.NaverMap
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.NaverMap

@OptIn(ExperimentalNaverMapApi::class)
@Composable
actual fun MapScreen(modifier: Modifier) {
    NaverMap(
        modifier = modifier
    )
}