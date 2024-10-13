package zone.ien.map.ui.utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun SetBackHandler(onBack: () -> Unit) {
    BackHandler { onBack.invoke() }
}