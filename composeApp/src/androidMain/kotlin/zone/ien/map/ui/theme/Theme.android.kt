package zone.ien.map.ui.theme

import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.alexzhirkevich.cupertino.adaptive.Theme

actual val AdaptiveTheme: Theme = Theme.Material3

@Composable
actual fun dynamicMaterialColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): androidx.compose.material3.ColorScheme {
    return when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }
}

@Composable
actual fun dynamicCupertinoColorScheme(
    darkTheme: Boolean,
): io.github.alexzhirkevich.cupertino.theme.ColorScheme {
    return if (darkTheme) io.github.alexzhirkevich.cupertino.theme.darkColorScheme(accent = darkScheme.primary)
    else io.github.alexzhirkevich.cupertino.theme.lightColorScheme(accent = lightScheme.primary)
}