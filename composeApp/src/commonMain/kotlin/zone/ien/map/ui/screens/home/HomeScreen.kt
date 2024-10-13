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
    val badges = remember { mutableStateListOf(0, 0, 0, 0, 0) }

    Row(
        modifier = modifier
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                HomeNavigationGraph(
                    windowSize = windowSize,
                    navController = navController,
//                    onAction = onAction
                )
            }
            AnimatedVisibility(
                visible = true,
                enter = expandVertically(tween(700)),
                exit = shrinkVertically(tween(700))
            ) {
                BottomNavigationBar(navController = navController, badges = badges)
            }
        }
    }
}

val navigationItems = listOf(
    BottomNavigationItem.Directions,
    BottomNavigationItem.Profile,
//    BottomNavigationItem.Timer,
//    BottomNavigationItem.CycledTimer,
//    BottomNavigationItem.Stopwatch
)
//
@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun BottomNavigationBar(navController: NavHostController, isPreview: Boolean = false, badges: SnapshotStateList<Int>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    AnimatedVisibility(
        visible = navigationItems.any { it.route == currentDestination?.route } || isPreview,
        enter = expandVertically(animationSpec = tween(700)),
        exit = shrinkVertically(animationSpec = tween(700))
    ) {
        AdaptiveNavigationBar {
            navigationItems.forEachIndexed { index, item ->
                NavItem(navItem = item, currentDestination = currentDestination, navController = navController, badges[index])
            }
        }
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun RowScope.NavItem(navItem: BottomNavigationItem, currentDestination: NavDestination?, navController: NavHostController, badge: Int) {
    AdaptiveNavigationBarItem(
        selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
        icon = {
            BadgedBox(badge = {
                if (badge != 0) {
                    Badge(
                        modifier = Modifier.size(if (badge <= 0) 6.dp else 16.dp)
                    ) {
                        if (badge > 0) Text(text = badge.toString(), fontSize = 12.sp)
                    }
                }
            }) {
                AdaptiveIcon(imageVector = navItem.icon, contentDescription = stringResource(navItem.title))
            }
        },
        label = {
            AdaptiveText(
                text = stringResource(navItem.title),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
//                .marqueeHorizontalFadingEdges(
//                    marqueeProvider = { Modifier.basicMarquee(iterations = Int.MAX_VALUE) }
//                )
            )
        },
        alwaysShowLabel = currentTheme != Theme.Material3,
        onClick = {
            if (currentDestination?.route != navItem.route) {
                navController.navigate(navItem.route) {
                    popUpTo(TransportDestination.route) {
                        saveState = true
                    }
                }
            }
        }
    )
}

sealed class BottomNavigationItem(val title: StringResource, val icon: ImageVector, val route: String) {
    data object Directions: BottomNavigationItem(Res.string.directions, Icons.Rounded.Alarm, TransportDestination.route)
    data object Profile: BottomNavigationItem(Res.string.profile, Icons.Rounded.Person, ProfileDestination.route)
//    data object Timer: BottomNavigationItem(Res.string.timer, MyIconPack.HourglassBottom, HomeTimerDestination.route)
//    data object CycledTimer: BottomNavigationItem(Res.string.cycle_timer, MyIconPack.HourglassRepeat, HomeCycledTimerDestination.route)
//    data object Stopwatch: BottomNavigationItem(Res.string.stopwatch, Icons.Rounded.Timer, HomeStopwatchDestination.route)
}