package zone.ien.map.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
private val defaultTypography = Typography()

@Composable
fun getTypography(): Typography {
    val pretendard = getPretendard()
    val typography =
        Typography(
            displayLarge = defaultTypography.displayLarge.copy(fontFamily = pretendard),
            displayMedium = defaultTypography.displayMedium.copy(fontFamily = pretendard),
            displaySmall = defaultTypography.displaySmall.copy(fontFamily = pretendard),
            headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = pretendard),
            headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = pretendard),
            headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = pretendard),
            titleLarge = defaultTypography.titleLarge.copy(fontFamily = pretendard),
            titleMedium = defaultTypography.titleMedium.copy(fontFamily = pretendard),
            titleSmall = defaultTypography.titleSmall.copy(fontFamily = pretendard),
            bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = pretendard, fontSize = 14.sp),
            bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = pretendard, fontSize = 12.sp),
            bodySmall = defaultTypography.bodySmall.copy(fontFamily = pretendard, fontSize = 11.sp),
            labelLarge = defaultTypography.labelLarge.copy(fontFamily = pretendard),
            labelMedium = defaultTypography.labelMedium.copy(fontFamily = pretendard),
            labelSmall = defaultTypography.labelSmall.copy(fontFamily = pretendard),
        )

    return typography
}
