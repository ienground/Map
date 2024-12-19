package zone.ien.map.ui.screens.home.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveTopAppBar
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.home
import map.composeapp.generated.resources.none
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import zone.ien.map.data.favorite.Favorite
import zone.ien.map.ui.navigation.NavigationDestination
import zone.ien.map.ui.navigation.ProfileNavigationGraph
import zone.ien.map.ui.screens.home.profile.list.ProfileListDestination
import zone.ien.map.ui.screens.home.transport.FavoriteDetails
import zone.ien.map.ui.screens.home.transport.TransportFavoriteUiStateList
import zone.ien.map.ui.utils.view.AdaptiveBackButton
import zone.ien.map.ui.utils.view.AdaptiveText
import zone.ien.map.ui.utils.view.AdaptiveTextShimmer

object ProfileDestination: NavigationDestination {
    override val route: String = "profile"
}

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navigateBack: () -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier,
        topBar = {
            AnimatedVisibility(
                visible = currentDestination?.route == ProfileListDestination.route,
                enter = expandVertically(tween(700)),
                exit = shrinkVertically(tween(700))
            ) {
                TopAppBar(
                    title = {},
                    navigationIcon = { AdaptiveBackButton { navigateBack() } },
                    modifier = Modifier
                )
            }
        }
    ) { 
        ProfileNavigationGraph(
            navController = navController,
            modifier = Modifier.padding(it)
        )
    }
}

