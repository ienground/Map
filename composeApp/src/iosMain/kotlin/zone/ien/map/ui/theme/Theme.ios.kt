package zone.ien.map.ui.theme

import androidx.compose.runtime.Composable
import io.github.alexzhirkevich.cupertino.adaptive.Theme
import io.github.alexzhirkevich.cupertino.theme.darkColorScheme
import io.github.alexzhirkevich.cupertino.theme.lightColorScheme

actual val AdaptiveTheme: Theme = Theme.Cupertino

@Composable
actual fun dynamicMaterialColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): androidx.compose.material3.ColorScheme {
    return when {
        darkTheme -> darkScheme
        else -> lightScheme
    }
}

@Composable
actual fun dynamicCupertinoColorScheme(
    darkTheme: Boolean,
): io.github.alexzhirkevich.cupertino.theme.ColorScheme {
    return if (darkTheme) darkColorScheme(accent = darkScheme.primary)
    else lightColorScheme(accent = lightScheme.primary)
}