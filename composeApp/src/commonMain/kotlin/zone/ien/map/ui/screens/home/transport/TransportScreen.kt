package zone.ien.map.ui.screens.home.transport

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Navigation
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material.icons.sharp.RemoveCircle
import androidx.compose.material.icons.sharp.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.alexzhirkevich.cupertino.CupertinoBottomSheetDefaults
import io.github.alexzhirkevich.cupertino.CupertinoBottomSheetScaffold
import io.github.alexzhirkevich.cupertino.CupertinoSearchTextField
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import kotlinx.coroutines.launch
import zone.ien.map.TAG
import zone.ien.map.ui.AppViewModelProvider
import zone.ien.map.ui.navigation.NavigationDestination
import zone.ien.map.ui.screens.home.HomeScreenBody
import zone.ien.map.ui.utils.view.AdaptiveText
import zone.ien.map.utils.Dlog
import zone.ien.map.utils.maps.MapScreen

object TransportDestination: NavigationDestination {
    override val route: String = "transport"
}

enum class SheetType {
    SEARCH, DETAIL, ROUTE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(
    modifier: Modifier = Modifier,
    navigateToProfile: () -> Unit,
    viewModel: TransportViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val historyUiStateList by viewModel.historyUiStateList.collectAsState()
    val favoriteUiStateList by viewModel.favoriteUiStateList.collectAsState()
    val sheetState = rememberBottomSheetScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.getCurrentLocation()
    }

    LaunchedEffect(sheetState.bottomSheetState.currentValue) {
        if (sheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
            viewModel.updateUiState(viewModel.uiState.item.copy(searchActive = false))
        } else if (sheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
            viewModel.updateUiState(viewModel.uiState.item.copy(searchActive = true))
        }
        Dlog.d(TAG, "bottom sheet state: ${sheetState.bottomSheetState.currentValue}")
    }

    val sheetHeight = animateDpAsState(
        targetValue = when (viewModel.uiState.item.sheetType) {
            SheetType.SEARCH -> 500.dp
            SheetType.DETAIL -> 100.dp
            SheetType.ROUTE -> 800.dp
        },
    )

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sheetHeight.value)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = viewModel.uiState.item.sheetType == SheetType.SEARCH,
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    SearchSheetContent(
                        uiState = viewModel.uiState,
                        onItemValueChanged = viewModel::updateUiState,
                        onSearchDestination = viewModel::searchDestination,
                        sheetState = sheetState,
                        historyList = historyUiStateList,
                        favoriteList = favoriteUiStateList
                    )
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = viewModel.uiState.item.sheetType == SheetType.DETAIL,
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    DetailSheetContent(
                        uiState = viewModel.uiState,
                        onItemValueChanged = viewModel::updateUiState
                    )
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = viewModel.uiState.item.sheetType == SheetType.ROUTE,
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    RouteSheetContent(
                        uiState = viewModel.uiState,
                        onItemValueChanged = viewModel::updateUiState,
                        onRequestRoute = viewModel::requestRoute
                    )
                }
            }
        },
        sheetPeekHeight = 100.dp,
        sheetDragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    CupertinoBottomSheetDefaults.DragHandle()
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
        modifier = modifier
    ) {
        TransportScreenBody(
            uiState = viewModel.uiState,
            navigateToProfile = navigateToProfile,
            modifier = Modifier.padding(it)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreenBody(
    modifier: Modifier = Modifier,
    uiState: TransportUiState,
    navigateToProfile: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        MapScreen(
            currentLatLng = uiState.item.currentLatLng,
            selectedLatLng = uiState.item.selectedQuery?.let { Pair(it.latitude, it.longitude) },
//            markers = uiState.item.markers,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        ) {
            FilledIconButton(onClick = navigateToProfile) { Icon(imageVector = Icons.Sharp.Person, contentDescription = null) }
            FilledIconButton(onClick = {}) { Icon(imageVector = Icons.Sharp.LocationOn, contentDescription = null) }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheetContent(
    modifier: Modifier = Modifier,
    uiState: TransportUiState,
    onItemValueChanged: (TransportDetails) -> Unit,
    onSearchDestination: (String) -> Unit,
    sheetState: BottomSheetScaffoldState,
    historyList: TransportHistoryUiStateList,
    favoriteList: TransportFavoriteUiStateList
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        DockedSearchBar(
            query = uiState.item.query,
            onQueryChange = { onItemValueChanged(uiState.item.copy(query = it)) },
            onSearch = onSearchDestination,
            active = uiState.item.searchActive,
            onActiveChange = {
                onItemValueChanged(uiState.item.copy(searchActive = it))
                coroutineScope.launch {
                    sheetState.bottomSheetState.expand()
                }
                Dlog.d(TAG, "onActiveChange ${it}")
            },
            leadingIcon = {

            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Sharp.Search,
                    contentDescription = null
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(items = uiState.item.queryResults, key = { it.title }) {
                    Text(
                        text = it.title,
                        modifier = Modifier.clickable {
                            Dlog.d(TAG, "moved to ${it.latitude} ${it.longitude} from ${it.title}")
                            onItemValueChanged(uiState.item.copy(currentLatLng = Pair(it.latitude, it.longitude), selectedQuery = it, sheetType = SheetType.DETAIL))
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
        AdaptiveText(
            text = ".."
        )
        Column {
            Row {
                Text("집")
                Text("회사")
                Text("학교")
            }
            Text("기타 n개")
        }
        AdaptiveText(
            text = "Recent"
        )
        LazyColumn(

        ) {
            items(items = historyList.itemList, key = { it.id }) {
                Text(
                    text = "${it}",
                    modifier = Modifier.clickable {

                    }
                )
            }
        }
    }
}

@Composable
fun DetailSheetContent(
    modifier: Modifier = Modifier,
    uiState: TransportUiState,
    onItemValueChanged: (TransportDetails) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = uiState.item.selectedQuery?.title ?: ""
        )
        FilledTonalButton(
            onClick = {
                onItemValueChanged(uiState.item.copy(sheetType = SheetType.ROUTE, searchActive = false))
            }
        ) {
            Icon(
                imageVector = Icons.Sharp.Navigation,
                contentDescription = null
            )
        }
        FilledTonalButton(
            onClick = {
                onItemValueChanged(uiState.item.copy(sheetType = SheetType.SEARCH, selectedQuery = null, searchActive = false))
            }
        ) {
            Icon(
                imageVector = Icons.Sharp.Close,
                contentDescription = null
            )
        }
    }
}

@Composable
fun RouteSheetContent(
    modifier: Modifier = Modifier,
    uiState: TransportUiState,
    onItemValueChanged: (TransportDetails) -> Unit,
    onRequestRoute: () -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { Button(onClick = onRequestRoute) { Text(text = "확인") } }
        item { Text(text = "현위치") }
        itemsIndexed(items = uiState.item.layovers, key = { index, item -> index }) { index, value ->
            Row {
                TextField(
                    value = value,
                    onValueChange = {
                        val layovers = uiState.item.layovers.toMutableList()
                        layovers[index] = it
                        onItemValueChanged(uiState.item.copy(layovers = layovers))
                    }
                )
                IconButton(
                    onClick = {
                        val layovers = uiState.item.layovers.toMutableList()
                        layovers.removeAt(index)
                        onItemValueChanged(uiState.item.copy(layovers = layovers))
                    }
                ) {
                    Icon(imageVector = Icons.Sharp.RemoveCircle, contentDescription = null)
                }
            }
        }
        item {
            Row {
                Text(text = uiState.item.selectedQuery?.title ?: "")
                IconButton(
                    onClick = {
                        val layovers = uiState.item.layovers.toMutableList()
                        layovers.add("")
                        onItemValueChanged(uiState.item.copy(layovers = layovers))
                    }
                ) {
                    Icon(imageVector = Icons.Sharp.Add, contentDescription = null)
                }
            }
        }
    }
}