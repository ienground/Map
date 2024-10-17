package zone.ien.map.ui.screens.home.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveTopAppBar
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import zone.ien.map.ui.AppViewModelProvider
import zone.ien.map.ui.navigation.NavigationDestination
import zone.ien.map.ui.utils.view.AdaptiveBackButton

object ProfileDestination: NavigationDestination {
    override val route: String = "profile"
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AdaptiveTopAppBar(
                title = {},
                navigationIcon = { AdaptiveBackButton { navigateBack() } },
                modifier = Modifier
            )
        }
    ) { 
        ProfileScreenBody(
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun ProfileScreenBody(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) { 
        
    }
}