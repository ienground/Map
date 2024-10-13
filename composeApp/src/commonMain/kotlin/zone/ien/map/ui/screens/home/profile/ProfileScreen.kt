package zone.ien.map.ui.screens.home.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import zone.ien.map.ui.AppViewModelProvider
import zone.ien.map.ui.navigation.NavigationDestination

object ProfileDestination: NavigationDestination {
    override val route: String = "profile"
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.factory)
) {

}

@Composable
fun ProfileScreenBody(
    modifier: Modifier = Modifier
) {

}