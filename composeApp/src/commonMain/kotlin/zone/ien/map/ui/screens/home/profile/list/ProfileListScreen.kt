package zone.ien.map.ui.screens.home.profile.list

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.sharp.Place
import androidx.compose.material.icons.sharp.School
import androidx.compose.material.icons.sharp.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.home
import map.composeapp.generated.resources.none
import map.composeapp.generated.resources.set_home
import map.composeapp.generated.resources.set_office
import map.composeapp.generated.resources.set_school
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import zone.ien.map.data.favorite.Favorite
import zone.ien.map.ui.navigation.NavigationDestination
import zone.ien.map.ui.screens.home.transport.TransportFavoriteUiStateList
import zone.ien.map.ui.utils.view.AdaptiveText
import zone.ien.map.ui.utils.view.AdaptiveTextShimmer

object ProfileListDestination: NavigationDestination {
    override val route: String = "list"
}

@Composable
fun ProfileListScreen(
    modifier: Modifier = Modifier,
    navigateToEdit: (Int) -> Unit,
    viewModel: ProfileListViewModel = koinViewModel(),
) {
    val favoriteUiStateList by viewModel.favoriteUiStateList.collectAsState()

    ProfileListScreenBody(
        favoriteUiStateList = favoriteUiStateList,
        navigateToEdit = navigateToEdit,
        modifier = modifier
    )
}

@Composable
fun ProfileListScreenBody(
    modifier: Modifier = Modifier,
    favoriteUiStateList: TransportFavoriteUiStateList,
    navigateToEdit: (Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        FavoriteCard(
            favoriteUiStateList = favoriteUiStateList,
            type = Favorite.Type.HOME,
            onClick = navigateToEdit
        )
        FavoriteCard(
            favoriteUiStateList = favoriteUiStateList,
            type = Favorite.Type.OFFICE,
            onClick = navigateToEdit
        )
        FavoriteCard(
            favoriteUiStateList = favoriteUiStateList,
            type = Favorite.Type.SCHOOL,
            onClick = navigateToEdit
        )
    }
}

@Composable
fun FavoriteCard(
    modifier: Modifier = Modifier,
    favoriteUiStateList: TransportFavoriteUiStateList,
    type: Int,
    onClick: (Int) -> Unit
) {
    val text: String
    val icon: ImageVector
    val containerColor: Color
    val contentColor: Color
    val buttonContainerColor: Color
    val buttonContentColor: Color

    when (type) {
        Favorite.Type.HOME -> {
            text = stringResource(Res.string.set_home)
            icon = Icons.Sharp.Home
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            buttonContainerColor = MaterialTheme.colorScheme.primary
            buttonContentColor = MaterialTheme.colorScheme.onPrimary
        }
        Favorite.Type.OFFICE -> {
            text = stringResource(Res.string.set_office)
            icon = Icons.Sharp.Work
            containerColor = MaterialTheme.colorScheme.secondaryContainer
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            buttonContainerColor = MaterialTheme.colorScheme.secondary
            buttonContentColor = MaterialTheme.colorScheme.onSecondary
        }
        Favorite.Type.SCHOOL -> {
            text = stringResource(Res.string.set_school)
            icon = Icons.Sharp.School
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            buttonContainerColor = MaterialTheme.colorScheme.tertiary
            buttonContentColor = MaterialTheme.colorScheme.onTertiary
        }
        else -> {
            text = ""
            icon = Icons.Sharp.Place
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            buttonContainerColor = MaterialTheme.colorScheme.primary
            buttonContentColor = MaterialTheme.colorScheme.onPrimary
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(36.dp))
                    AdaptiveText(
                        text = text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                FilledIconButton(
                    onClick = { onClick(type) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = buttonContainerColor, contentColor = buttonContentColor)
                ) { Icon(imageVector = Icons.Sharp.Edit, contentDescription = null) }
            }
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !favoriteUiStateList.isInitialized,
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    AdaptiveTextShimmer(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(60.dp)
                    )
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = favoriteUiStateList.isInitialized,
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    AdaptiveText(
                        text = favoriteUiStateList.itemList.firstOrNull { it.type == type }?.label ?: stringResource(
                            Res.string.none),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !favoriteUiStateList.isInitialized,
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    AdaptiveTextShimmer(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = favoriteUiStateList.isInitialized,
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    AdaptiveText(
                        text = favoriteUiStateList.itemList.firstOrNull { it.type == type }?.address ?: stringResource(
                            Res.string.none)
                    )
                }
            }
        }
    }
}