package zone.ien.map.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveNavigationBar
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveNavigationBarItem
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import io.github.alexzhirkevich.cupertino.adaptive.Theme
import io.github.alexzhirkevich.cupertino.adaptive.currentTheme
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.directions
import map.composeapp.generated.resources.profile
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import zone.ien.map.ui.AppViewModelProvider
import zone.ien.map.ui.navigation.HomeNavigationGraph
import zone.ien.map.ui.navigation.NavigationDestination
import zone.ien.map.ui.screens.home.profile.ProfileDestination
import zone.ien.map.ui.screens.home.transport.TransportDestination
import zone.ien.map.ui.utils.view.AdaptiveIcon
import zone.ien.map.ui.utils.view.AdaptiveText

object HomeDestination: NavigationDestination {
    override val route: String = "home"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    HomeScreenBody(
        navController = navController,
        windowSize = windowSize,
        modifier = modifier
    )
}

@Composable
fun HomeScreenBody(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    navController: NavHostController
) {
    Box(
        modifier = modifier
    ) {
        HomeNavigationGraph(
            windowSize = windowSize,
            navController = navController,
//                    onAction = onAction
        )
    }
}
