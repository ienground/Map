package zone.ien.map.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import zone.ien.map.ui.navigation.HomeNavigationGraph
import zone.ien.map.ui.navigation.NavigationDestination

object HomeDestination: NavigationDestination {
    override val route: String = "home"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = koinViewModel()
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
