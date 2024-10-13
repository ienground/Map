package zone.ien.map.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import zone.ien.map.ui.screens.home.HomeDestination
import zone.ien.map.ui.screens.home.HomeScreen
import zone.ien.map.ui.screens.home.profile.ProfileDestination
import zone.ien.map.ui.screens.home.profile.ProfileScreen
import zone.ien.map.ui.screens.home.transport.TransportDestination
import zone.ien.map.ui.screens.home.transport.TransportScreen
import zone.ien.map.ui.screens.permissions.PermissionsDestination
import zone.ien.map.ui.screens.permissions.PermissionsScreen

sealed interface UiAction {
    data object NavigateToSettings: UiAction
    data object NavigateToDeskclock: UiAction
    data object NavigateToGuide: UiAction
    data object NavigateToPermissions: UiAction
}

typealias OnAction = (UiAction) -> Unit

@Composable
fun RootNavigationGraph(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        enterTransition = { fadeIn(tween(700)) },
        exitTransition = { fadeOut(tween(700)) },
        modifier = modifier
    ) {
        composable(
            route = HomeDestination.route
        ) {
            HomeScreen(
                windowSize = windowSize,
//                onAction = { action ->
//                    when (action) {
//                        UiAction.NavigateToSettings -> {
//                            navController.navigate(SettingsDestination.route)
//                        }
//                        UiAction.NavigateToDeskclock -> {
////                            navController.navigate(DeskclockDestination.route)
//                        }
//                        UiAction.NavigateToGuide -> {
////                            navController.navigate(GuideDestination.route)
//                        }
//                        UiAction.NavigateToPermissions -> {
//                            navController.navigate(PermissionsDestination.route)
//                        }
//                    }
//                },
            )
        }
        composable(
            route = PermissionsDestination.route,
        ) {
            PermissionsScreen(
//                windowSize = windowSize,
//                navigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun HomeNavigationGraph(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = TransportDestination.route
    ) {
        composable(route = TransportDestination.route) {
            TransportScreen()
        }

        composable(route = ProfileDestination.route) {
            ProfileScreen()
        }
    }
}