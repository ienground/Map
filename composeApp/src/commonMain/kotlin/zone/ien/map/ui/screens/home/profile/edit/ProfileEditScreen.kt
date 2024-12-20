package zone.ien.map.ui.screens.home.profile.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Place
import androidx.compose.material.icons.sharp.School
import androidx.compose.material.icons.sharp.Search
import androidx.compose.material.icons.sharp.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.query_empty
import map.composeapp.generated.resources.set_home
import map.composeapp.generated.resources.set_office
import map.composeapp.generated.resources.set_school
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import zone.ien.map.TAG
import zone.ien.map.data.QueryResult
import zone.ien.map.data.favorite.Favorite
import zone.ien.map.ui.navigation.NavigationDestination
import zone.ien.map.ui.screens.home.transport.SearchRow
import zone.ien.map.ui.screens.home.transport.SearchRowShimmer
import zone.ien.map.ui.screens.home.transport.SheetType
import zone.ien.map.ui.screens.home.transport.TransportUiState
import zone.ien.map.ui.utils.view.AdaptiveBackButton
import zone.ien.map.ui.utils.view.AdaptiveText
import zone.ien.map.utils.Dlog
import zone.ien.map.utils.LocationUtils
import zone.ien.map.utils.MapLatLng
import zone.ien.map.utils.diffToString
import zone.ien.map.utils.maps.MapScreen
import zone.ien.map.utils.maps.MarkerType
import zone.ien.map.utils.measure

object ProfileEditDestination: NavigationDestination {
    override val route: String = "edit"
    const val itemTypeArg = "itemType"
    val routeWithArgs = "$route?${itemTypeArg}={$itemTypeArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel = koinViewModel(),
    navigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            val text: String
            val icon: ImageVector
            val containerColor: Color
            val contentColor: Color

            when (viewModel.uiState.item.type) {
                Favorite.Type.HOME -> {
                    text = stringResource(Res.string.set_home)
                    icon = Icons.Sharp.Home
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                }
                Favorite.Type.OFFICE -> {
                    text = stringResource(Res.string.set_office)
                    icon = Icons.Sharp.Work
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                }
                Favorite.Type.SCHOOL -> {
                    text = stringResource(Res.string.set_school)
                    icon = Icons.Sharp.School
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                }
                else -> {
                    text = ""
                    icon = Icons.Sharp.Place
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                }
            }

            ExtendedFloatingActionButton(
                text = { AdaptiveText(text = text) },
                icon = { Icon(imageVector = icon, contentDescription = null) },
                onClick = {
                    coroutineScope.launch {
                        viewModel.save()
                        navigateBack()
                    }
                },
                containerColor = containerColor,
                contentColor = contentColor
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier.fillMaxSize()
    ) {
        ProfileEditScreenBody(
            uiState = viewModel.uiState,
            onItemValueChanged = viewModel::updateUiState,
            navigateBack = navigateBack,
            onSearch = viewModel::searchDestination,
            modifier = Modifier.padding(it)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreenBody(
    modifier: Modifier = Modifier,
    uiState: ProfileEditUiState,
    onItemValueChanged: (ProfileEditDetails) -> Unit,
    navigateBack: () -> Unit,
    onSearch: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
    ) {
        DockedSearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = uiState.item.query,
                    onQueryChange = { onItemValueChanged(uiState.item.copy(query = it)) },
                    onSearch = onSearch,
                    expanded = uiState.item.searchBarExpanded,
                    onExpandedChange = { onItemValueChanged(uiState.item.copy(searchBarExpanded = it)) },
                    leadingIcon = {
                        AnimatedVisibility(
                            visible = uiState.item.searchBarExpanded,
                            enter = fadeIn(tween(700)),
                            exit = fadeOut(tween(700))
                        ) {
                            IconButton(
                                onClick = { onItemValueChanged(uiState.item.copy(searchBarExpanded = false)) },
                            ) {
                                Icon(
                                    imageVector = Icons.Sharp.Close,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = !uiState.item.searchBarExpanded,
                            enter = fadeIn(tween(700)),
                            exit = fadeOut(tween(700))
                        ) {
                            AdaptiveBackButton(navigateBack)
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { onSearch(uiState.item.query) },
                        ) {
                            Icon(
                                imageVector = Icons.Sharp.Search,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            expanded = uiState.item.searchBarExpanded,
            onExpandedChange = { onItemValueChanged(uiState.item.copy(searchBarExpanded = it)) },
            shape = SearchBarDefaults.dockedShape,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !uiState.isQueryInitialized,
                        enter = fadeIn(tween(700)),
                        exit = fadeOut(tween(700))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(5) { index ->
                                SearchRowShimmer(modifier = Modifier.shimmer())
                                if (index != 4) {
                                    HorizontalDivider(modifier = Modifier.shimmer())
                                }
                            }
                        }
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = uiState.item.queryResults.isEmpty() && uiState.isQueryInitialized,
                        enter = fadeIn(tween(700)),
                        exit = fadeOut(tween(700))
                    ) {
                        AdaptiveText(
                            text = stringResource(Res.string.query_empty),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .wrapContentHeight()
                        )
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = uiState.item.queryResults.isNotEmpty() && uiState.isQueryInitialized,
                        enter = fadeIn(tween(700)),
                        exit = fadeOut(tween(700))
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(
                                items = uiState.item.queryResults,
                                key = { index, _ -> index }) { index, item ->
                                SearchRow(
                                    uiState = uiState,
                                    query = item,
                                    onClick = {
                                        onItemValueChanged(
                                            uiState.item.copy(
                                                selectedQuery = item,
                                                selectedLatLng = MapLatLng(
                                                    item.latitude,
                                                    item.longitude
                                                ),
                                                searchBarExpanded = false
                                            )
                                        )
                                    },
                                    modifier = Modifier.animateItem()
                                )
                                if (index != uiState.item.queryResults.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.animateItem()
                                    )
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = !uiState.isItemInitialized,
                enter = fadeIn(tween(700)),
                exit = fadeOut(tween(700))
            ) {
                CircularProgressIndicator()
            }
            AnimatedVisibility(
                visible = uiState.isItemInitialized,
                enter = fadeIn(tween(700)),
                exit = fadeOut(tween(700))
            ) {
                MapScreen(
                    currentLatLng = uiState.item.currentLatLng,
                    selectedLatLng = uiState.item.selectedLatLng,
                    onSelectLatLng = { onItemValueChanged(uiState.item.copy(selectedLatLng = it)) },
                    candidates = mutableStateListOf(),
                    selectedIndex = -1,
                    markers = uiState.item.selectedLatLng.let { mutableStateListOf(Triple(MarkerType.SELECTED, it.latitude, it.longitude)) }
                )
            }
        }
    }

}

@OptIn(ExperimentalRichTextApi::class)
@Composable
fun SearchRow(
    modifier: Modifier = Modifier,
    uiState: ProfileEditUiState,
    query: QueryResult,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Icon(imageVector = Icons.Sharp.LocationOn, contentDescription = null)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row {
                AdaptiveText(
                    text = query.title,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                AdaptiveText(
                    text = query.categories.last()
                )
            }
            Row {
                AdaptiveText(
                    text = query.address,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                AdaptiveText(
                    text = LocationUtils.measure(uiState.item.currentLatLng, MapLatLng(query.latitude, query.longitude)).diffToString()
                )
            }

        }
    }
}