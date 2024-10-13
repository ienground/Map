package zone.ien.map.ui.screens.permissions

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import zone.ien.map.ui.AppViewModelProvider
import zone.ien.map.ui.navigation.NavigationDestination

object PermissionsDestination: NavigationDestination {
    override val route: String = "permissions"
}

@Composable
fun PermissionsScreen(
    modifier: Modifier = Modifier,
    viewModel: PermissionsViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    Text("permissions")
}