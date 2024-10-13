package zone.ien.map.ui.utils.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import zone.ien.map.ui.utils.AdaptiveDimen

val listVerticalSpacing
    @Composable
    get() = AdaptiveDimen(
        material = 16.dp,
        cupertino = 0.dp
    )

val listHorizontalMargin
    @Composable
    get() = AdaptiveDimen(
        material = 16.dp,
        cupertino = 0.dp
    )