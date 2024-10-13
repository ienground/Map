package zone.ien.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveScaffold
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveTheme
import io.github.alexzhirkevich.cupertino.adaptive.CupertinoThemeSpec
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import io.github.alexzhirkevich.cupertino.adaptive.MaterialThemeSpec
import io.github.alexzhirkevich.cupertino.adaptive.Shapes
import io.github.alexzhirkevich.cupertino.adaptive.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import zone.ien.map.ui.navigation.RootNavigationGraph
import zone.ien.map.ui.theme.dynamicCupertinoColorScheme
import zone.ien.map.ui.theme.dynamicMaterialColorScheme
import zone.ien.map.ui.theme.getTypography

const val TAG = "MapTAG"

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
@Preview
fun App() {
    GeneratedAdaptiveTheme(
        target = zone.ien.map.ui.theme.AdaptiveTheme,
    ) {
        AdaptiveScaffold(
            contentWindowInsets = WindowInsets(0.dp)
        ) {
            val navController = rememberNavController()
            val windowSize = calculateWindowSizeClass()
            Column(
                modifier = Modifier.padding(it)
            ) {
                RootNavigationGraph(
                    windowSize = windowSize,
                    navController = navController
                )
            }
        }
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun GeneratedAdaptiveTheme(
    target: Theme,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = true,
    shapes: Shapes = Shapes(),
    content: @Composable () -> Unit
) {
    AdaptiveTheme(
        target = target,
        material = MaterialThemeSpec.Default(
            colorScheme = dynamicMaterialColorScheme(darkTheme = useDarkTheme, dynamicColor = useDynamicColor),
            shapes = androidx.compose.material3.Shapes(
                extraSmall = shapes.extraSmall,
                small = shapes.small,
                medium = shapes.medium,
                large = shapes.large,
                extraLarge = shapes.extraLarge
            ),
            typography = getTypography()
        ),
        cupertino = CupertinoThemeSpec.Default(
            colorScheme = dynamicCupertinoColorScheme(darkTheme = useDarkTheme),
            shapes = io.github.alexzhirkevich.cupertino.theme.Shapes(
                extraSmall = shapes.extraSmall,
                small = shapes.small,
                medium = shapes.medium,
                large = shapes.large,
                extraLarge = shapes.extraLarge
            )
        ),
        content = content
    )
}