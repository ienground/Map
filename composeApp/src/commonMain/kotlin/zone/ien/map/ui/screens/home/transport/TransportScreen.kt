package zone.ien.map.ui.screens.home.transport

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.History
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Navigation
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material.icons.sharp.Place
import androidx.compose.material.icons.sharp.RemoveCircle
import androidx.compose.material.icons.sharp.School
import androidx.compose.material.icons.sharp.Search
import androidx.compose.material.icons.sharp.Work
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import io.github.alexzhirkevich.cupertino.CupertinoBottomSheetDefaults
import kotlinx.coroutines.launch
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.add
import map.composeapp.generated.resources.home
import map.composeapp.generated.resources.input_query
import map.composeapp.generated.resources.long_ago
import map.composeapp.generated.resources.more
import map.composeapp.generated.resources.office
import map.composeapp.generated.resources.query_empty
import map.composeapp.generated.resources.recent
import map.composeapp.generated.resources.school
import map.composeapp.generated.resources.this_month
import map.composeapp.generated.resources.this_week
import map.composeapp.generated.resources.this_year
import map.composeapp.generated.resources.today
import map.composeapp.generated.resources.yesterday
import org.jetbrains.compose.resources.stringResource
import zone.ien.map.data.QueryResult
import zone.ien.map.data.favorite.Favorite
import zone.ien.map.ui.AppViewModelProvider
import zone.ien.map.ui.navigation.NavigationDestination
import zone.ien.map.ui.utils.view.AdaptiveBackButton
import zone.ien.map.ui.utils.view.AdaptiveText
import zone.ien.map.utils.HistoryGroup
import zone.ien.map.utils.LocationUtils
import zone.ien.map.utils.diffToString
import zone.ien.map.utils.groupByTime
import zone.ien.map.utils.maps.MapScreen
import zone.ien.map.utils.measure
import zone.ien.map.utils.safeSubList
import zone.ien.map.utils.timeInMillis

object TransportDestination: NavigationDestination {
    override val route: String = "transport"
}

enum class SheetType {
    SEARCH, DETAIL, ROUTE, HISTORY
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
        viewModel.updateUiState(viewModel.uiState.item.copy(sheetState = sheetState.bottomSheetState.currentValue))
        if (sheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
            viewModel.updateUiState(viewModel.uiState.item.copy(searchActive = false))
            if (viewModel.uiState.item.sheetType == SheetType.HISTORY) {
                viewModel.updateUiState(viewModel.uiState.item.copy(sheetType = SheetType.SEARCH))
            }
        }
    }

    val sheetHeight = animateDpAsState(
        targetValue = when (viewModel.uiState.item.sheetType) {
            SheetType.SEARCH -> if (viewModel.uiState.item.searchActive) 800.dp else 514.dp
            SheetType.DETAIL -> 100.dp
            SheetType.ROUTE -> 800.dp
            SheetType.HISTORY -> 800.dp
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
                androidx.compose.animation.AnimatedVisibility(
                    visible = viewModel.uiState.item.sheetType == SheetType.HISTORY,
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    HistorySheetContent(
                        uiState = viewModel.uiState,
                        onItemValueChanged = viewModel::updateUiState,
                        historyList = historyUiStateList
                    )
                }
            }
        },
        sheetPeekHeight = 108.dp,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
            },
            placeholder = {
                Text(stringResource(Res.string.input_query))
            },
            leadingIcon = {

            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Sharp.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = uiState.item.queryResults.isEmpty(),
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    Text(
                        text = stringResource(Res.string.query_empty),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .wrapContentHeight()
                    )
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = uiState.item.queryResults.isNotEmpty(),
                    enter = fadeIn(tween(700)),
                    exit = fadeOut(tween(700))
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(items = uiState.item.queryResults, key = { _, item -> item.title }) { index, item ->
                            SearchRow(
                                uiState = uiState,
                                query = item,
                                onClick = { onItemValueChanged(uiState.item.copy(currentLatLng = Pair(item.latitude, item.longitude), selectedQuery = item, sheetType = SheetType.DETAIL)) },
                                modifier = Modifier
                            )
                            if (index != uiState.item.queryResults.size - 1) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = !uiState.item.searchActive,
            enter = fadeIn(tween(700)),
            exit = fadeOut(tween(700))
        ) {
            Column(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                LazyRow {
                    item {
                        FavoriteRow(
                            uiState = uiState,
                            item = favoriteList.itemList.firstOrNull { it.type == Favorite.Type.HOME }
                                ?: FavoriteDetails(type = Favorite.Type.HOME),
                            onClick = {},
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    item {
                        FavoriteRow(
                            uiState = uiState,
                            item = favoriteList.itemList.firstOrNull { it.type == Favorite.Type.OFFICE }
                                ?: FavoriteDetails(type = Favorite.Type.OFFICE),
                            onClick = {},
                        )
                    }
                    item {
                        FavoriteRow(
                            uiState = uiState,
                            item = favoriteList.itemList.firstOrNull { it.type == Favorite.Type.SCHOOL }
                                ?: FavoriteDetails(type = Favorite.Type.SCHOOL),
                            onClick = {},
                        )
                    }
                    items(items = favoriteList.itemList.filter { it.type == Favorite.Type.ETC }, key = { it.id }) { item ->
                        FavoriteRow(
                            uiState = uiState,
                            item = item,
                            onClick = {},
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                    item { Spacer(modifier = Modifier.width(8.dp)) }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.recent),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                    TextButton(
                        onClick = { onItemValueChanged(uiState.item.copy(sheetType = SheetType.HISTORY)) },
                        modifier = Modifier
                    ) {
                        Text(
                            text = stringResource(Res.string.more)
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(items = historyList.itemList.sortedByDescending { it.lastUsedTime.timeInMillis() }.safeSubList(0, 3), key = { _, item -> item.id }) { index, item ->
                        val shape = if (historyList.itemList.size == 1) RoundedCornerShape(16.dp) else when (index) {
                            0 -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            historyList.itemList.size - 1, 2 -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                            else -> RectangleShape
                        }
                        HistoryRow(
                            item = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    onItemValueChanged(uiState.item.copy(selectedQuery = QueryResult(title = item.label, address = item.address, latitude = item.latitude, longitude = item.longitude), sheetType = SheetType.DETAIL)
                                    )
                                }
                        )
                        if (index != historyList.itemList.size - 1 && index != 2) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteRow(
    modifier: Modifier = Modifier,
    uiState: TransportUiState,
    item: FavoriteDetails,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        FavoriteIconButton(
            onClick = onClick,
            item = item,
            contentColor = when (item.type) {
                Favorite.Type.HOME -> MaterialTheme.colorScheme.onPrimaryContainer
                Favorite.Type.OFFICE -> MaterialTheme.colorScheme.onSecondaryContainer
                Favorite.Type.SCHOOL -> MaterialTheme.colorScheme.onTertiaryContainer
                else -> MaterialTheme.colorScheme.onErrorContainer
            },
            containerColor = when (item.type) {
                Favorite.Type.HOME -> MaterialTheme.colorScheme.primaryContainer
                Favorite.Type.OFFICE -> MaterialTheme.colorScheme.secondaryContainer
                Favorite.Type.SCHOOL -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.errorContainer
            },
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = when (item.type) {
                    Favorite.Type.HOME -> Icons.Sharp.Home
                    Favorite.Type.OFFICE -> Icons.Sharp.Work
                    Favorite.Type.SCHOOL -> Icons.Sharp.School
                    else -> Icons.Sharp.Place
                },
                contentDescription = null,
            )
        }
        Text(
            text =
                if (item.id != -1L) item.label
                else when (item.type) {
                    Favorite.Type.HOME -> stringResource(Res.string.home)
                    Favorite.Type.OFFICE -> stringResource(Res.string.office)
                    Favorite.Type.SCHOOL -> stringResource(Res.string.school)
                    else -> ""
                }
            ,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = if (item.id != -1L) LocationUtils.measure(uiState.item.currentLatLng, Pair(item.latitude, item.longitude)).diffToString() else stringResource(Res.string.add),
            fontSize = 12.sp
        )
    }
}

@Composable
fun FavoriteIconButton(
    modifier: Modifier = Modifier,
    item: FavoriteDetails,
    onClick: () -> Unit,
    contentColor: Color,
    containerColor: Color,
    content: @Composable () -> Unit
) {
    Box {
        AnimatedVisibility(
            visible = item.id == -1L,
            enter = fadeIn(tween(700)),
            exit = fadeOut(tween(700))
        ) {
            OutlinedIconButton(
                onClick = onClick,
                shape = RoundedCornerShape(16.dp),
                modifier = modifier,
                colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = contentColor),
                content = content
            )
        }
        AnimatedVisibility(
            visible = item.id != -1L,
            enter = fadeIn(tween(700)),
            exit = fadeOut(tween(700))
        ) {
            FilledTonalIconButton(
                onClick = onClick,
                shape = RoundedCornerShape(16.dp),
                modifier = modifier,
                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerColor, contentColor = contentColor),
                content = content
            )
        }
    }
}

@Composable
fun HistoryRow(
    modifier: Modifier = Modifier,
    item: HistoryDetails,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Sharp.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            Text(
                text = item.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = item.address,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalRichTextApi::class)
@Composable
fun SearchRow(
    modifier: Modifier = Modifier,
    uiState: TransportUiState,
    query: QueryResult,
    onClick: () -> Unit
) {
    val richTextState = rememberRichTextState()

    Row(
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
                RichText(
                    state = richTextState.setHtml(query.title),
                    modifier = Modifier.weight(1f)
                )
                AdaptiveText(
                    text = query.categories.last()
                )
            }
            Row {
                AdaptiveText(
                    text = query.address,
                    modifier = Modifier.weight(1f)
                )
                AdaptiveText(
                    text = LocationUtils.measure(uiState.item.currentLatLng, Pair(query.latitude, query.longitude)).diffToString()
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSheetContent(
    modifier: Modifier = Modifier,
    uiState: TransportUiState,
    onItemValueChanged: (TransportDetails) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    AdaptiveBackButton { onItemValueChanged(uiState.item.copy(sheetType = SheetType.SEARCH)) }
                },
                title = {}
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(it)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSheetContent(
    modifier: Modifier = Modifier,
    uiState: TransportUiState,
    onItemValueChanged: (TransportDetails) -> Unit,
    onRequestRoute: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    AdaptiveBackButton { onItemValueChanged(uiState.item.copy(sheetType = SheetType.DETAIL)) }
                },
                title = {}
            )
        },
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            item { Button(onClick = onRequestRoute) { Text(text = "확인") } }
            item { Text(text = "현위치") }
            item {
                Row {
                    Checkbox(checked = uiState.item.isOrdered, onCheckedChange = { onItemValueChanged(uiState.item.copy(isOrdered = it)) })
                    Text(text = "순서 중요")
                }
            }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheetContent(
    modifier: Modifier = Modifier,
    uiState: TransportUiState,
    onItemValueChanged: (TransportDetails) -> Unit,
    historyList: TransportHistoryUiStateList
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    AdaptiveBackButton { onItemValueChanged(uiState.item.copy(sheetType = SheetType.SEARCH)) }
                },
                title = { Text(text = "History") },
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            historyList.itemList.groupByTime().forEach { pair ->
                if (pair.second.isNotEmpty()) {
                    HistoryList(
                        pair = pair,
                        uiState = uiState,
                        onItemValueChanged = onItemValueChanged,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryList(
    modifier: Modifier = Modifier,
    pair: Pair<HistoryGroup, List<HistoryDetails>>,
    uiState: TransportUiState,
    onItemValueChanged: (TransportDetails) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(when (pair.first) {
                    HistoryGroup.TODAY -> Res.string.today
                    HistoryGroup.YESTERDAY -> Res.string.yesterday
                    HistoryGroup.WEEK -> Res.string.this_week
                    HistoryGroup.MONTH -> Res.string.this_month
                    HistoryGroup.YEAR -> Res.string.this_year
                    HistoryGroup.OLD -> Res.string.long_ago
                }),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(items = pair.second, key = { _, item -> item.id }) { index, item ->
                val shape = if (pair.second.size == 1) RoundedCornerShape(16.dp) else when (index) {
                    0 -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    pair.second.size - 1 -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    else -> RectangleShape
                }
                HistoryRow(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                        }
                )
                if (index != pair.second.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}