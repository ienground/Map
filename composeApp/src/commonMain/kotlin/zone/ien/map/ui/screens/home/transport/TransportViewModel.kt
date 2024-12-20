package zone.ien.map.ui.screens.home.transport

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunnychung.lib.multiplatform.kdatetime.KZonedDateTime
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import zone.ien.map.TAG
import zone.ien.map.constant.ApiKey
import zone.ien.map.constant.MapDirection
import zone.ien.map.constant.Pref
import zone.ien.map.data.Candidate
import zone.ien.map.data.QueryResult
import zone.ien.map.data.RouteResult
import zone.ien.map.data.favorite.Favorite
import zone.ien.map.data.favorite.FavoriteRepository
import zone.ien.map.data.history.History
import zone.ien.map.data.history.HistoryRepository
import zone.ien.map.datastore.DEFAULT_DATASTORE
import zone.ien.map.utils.Dlog
import zone.ien.map.utils.LocationUtils
import zone.ien.map.utils.MapLatLng
import zone.ien.map.utils.extractRepresentativeCoordinatesByCount
import zone.ien.map.utils.now
import zone.ien.map.utils.toAddress
import zone.ien.map.utils.toRoadAddress

@OptIn(ExperimentalMaterial3Api::class)
class TransportViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val historyRepository: HistoryRepository
): ViewModel(), KoinComponent {
    val historyUiStateList: StateFlow<TransportHistoryUiStateList> =
        historyRepository.getAll().map { TransportHistoryUiStateList(it.map { history ->
            history.toHistoryDetails()
        }, isInitialized = true) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TransportHistoryUiStateList()
            )

    val favoriteUiStateList: StateFlow<TransportFavoriteUiStateList> =
        favoriteRepository.getAll().map { TransportFavoriteUiStateList(it.map { favorite ->
            favorite.toFavoriteDetails()
        }, isInitialized = true) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TransportFavoriteUiStateList()
            )

    var queryUiState by mutableStateOf(TransportQueryUiState(isInitialized = true))
        private set

    var uiState by mutableStateOf(TransportUiState())
        private set

    private val client = HttpClient()
    private val dataStore: DataStore<Preferences> by inject(DEFAULT_DATASTORE)

    init {
        viewModelScope.launch {
            getCurrentLocation()
        }
    }

    fun updateUiState(item: TransportDetails) {
        uiState = TransportUiState(item = item)
    }

    fun getCurrentLocation() {
        LocationUtils.getCurrentLocation {
            if (!uiState.item.isCurrentInitialized) {
                updateUiState(uiState.item.copy(selectedLatLng = it))
            }
            updateUiState(uiState.item.copy(currentLatLng = it, isCurrentInitialized = true))
        }
    }

    fun getSelectedAddress(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.get("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=${longitude}%2C${latitude}&output=json&orders=addr%2Croadaddr") {
                    header("x-ncp-apigw-api-key-id", ApiKey.NAVER_MAP_CLIENT_KEY_ID)
                    header("x-ncp-apigw-api-key", ApiKey.NAVER_MAP_CLIENT_KEY)
                }
                val result = Json.decodeFromString<JsonObject>(response.bodyAsText())["results"]?.jsonArray
                updateUiState(uiState.item.copy(selectedRoadAddress = result?.toRoadAddress() ?: ""))
                updateUiState(uiState.item.copy(selectedAddress = result?.toAddress() ?: ""))
            } catch (e: Exception) {
                Dlog.e(TAG, "error: ${e}")
            }
        }
    }

    suspend fun getCurrentAddress() {
        try {
            val response = client.get("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=${uiState.item.currentLatLng.longitude}%2C${uiState.item.currentLatLng.latitude}&output=json&orders=addr%2Croadaddr") {
                header("x-ncp-apigw-api-key-id", ApiKey.NAVER_MAP_CLIENT_KEY_ID)
                header("x-ncp-apigw-api-key", ApiKey.NAVER_MAP_CLIENT_KEY)
            }
            val result = Json.decodeFromString<JsonObject>(response.bodyAsText())["results"]?.jsonArray
            updateUiState(uiState.item.copy(currentRoadAddress = result?.toRoadAddress() ?: "", currentAddress = result?.toAddress() ?: ""))
        } catch (e: Exception) {
            Dlog.e(TAG, "error: ${e}")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun searchDestination(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getCurrentAddress()
                queryUiState = TransportQueryUiState(isInitialized = false, itemList = listOf())

                val response = client.get("https://dapi.kakao.com/v2/local/search/keyword.JSON?query=${query}&x=${uiState.item.currentLatLng.longitude}&y=${uiState.item.currentLatLng.latitude}&sort=accuracy&page=1&size=15") {
                    header("Authorization", ApiKey.KAKAO_API_KEY)
                }

                val result = Json.decodeFromString<JsonObject>(response.bodyAsText())["documents"]?.jsonArray?.map {
                    val jsonObject = it.jsonObject
                    val title = jsonObject["place_name"]?.jsonPrimitive?.content ?: ""
                    val category = jsonObject["category_group_name"]?.jsonPrimitive?.content ?: ""
                    val address = jsonObject["address_name"]?.jsonPrimitive?.content ?: ""
                    val roadAddress = jsonObject["road_address_name"]?.jsonPrimitive?.content ?: ""
                    val latitude = jsonObject["y"]?.jsonPrimitive?.content?.toDouble() ?: 0.0
                    val longitude = jsonObject["x"]?.jsonPrimitive?.content?.toDouble() ?: 0.0

                    QueryResult(title = title, categories = category.split(">"), address = address, roadAddress = roadAddress, latitude = latitude, longitude = longitude)
                }

                queryUiState = TransportQueryUiState(isInitialized = true, itemList = result ?: listOf())
                updateUiState(uiState.item.copy(searchActive = true))
            } catch (e: Exception) {
                Dlog.e(TAG, "error: ${e}")
            }
        }
    }

    fun requestRouteInitial(goal: MapLatLng) {
        // 저장
        updateUiState(item = uiState.item.copy(isRouteLoading = true))
        CoroutineScope(Dispatchers.IO).launch {
            uiState.item.selectedQuery?.let { query ->
                val item = History(label = query.title, address = query.address, latitude = query.latitude, longitude = query.longitude, category = query.categories.joinToString(">"))
                val pre = historyRepository.getByCoordinate(query.latitude, query.longitude).first()

                if (pre == null) {
                    historyRepository.upsert(item)
                } else {
                    historyRepository.upsert(item.apply { this.id = pre.id })
                }
            }

            // 경로 요청
            val response = client.get("https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?goal=${goal.longitude}%2C${goal.latitude}&start=${uiState.item.currentLatLng.longitude}%2C${uiState.item.currentLatLng.latitude}&option=${uiState.item.trafficOption}") {
                header("x-ncp-apigw-api-key-id", ApiKey.NAVER_MAP_CLIENT_KEY_ID)
                header("x-ncp-apigw-api-key", ApiKey.NAVER_MAP_CLIENT_KEY)
            }

            val result = Json.decodeFromString<JsonObject>(response.bodyAsText())["route"]?.jsonObject?.get(uiState.item.trafficOption)?.jsonArray

            if (result?.isNotEmpty() == true) {
                val summary = result[0].jsonObject["summary"]?.jsonObject
                val path = result[0].jsonObject["path"]?.jsonArray?.map { json ->
                    json.jsonArray.let { MapLatLng(it[1].jsonPrimitive.double, it[0].jsonPrimitive.double) }
                }

                if (path != null) {
                    val represent = extractRepresentativeCoordinatesByCount(path, 3)
                    val routesInitial = arrayListOf<MapLatLng>()
                    val routes = uiState.item.routesInitial
                    routesInitial.addAll(path)
                    routes.clear()
                    routes.addAll(represent)
                    updateUiState(uiState.item.copy(routesInitial = routes))

                    if (uiState.item.layovers.isEmpty()) {
                        val distance = summary?.get("distance")?.jsonPrimitive?.int ?: 0
                        val duration = summary?.get("duration")?.jsonPrimitive?.int ?: 0
                        val tollFare = summary?.get("tollFare")?.jsonPrimitive?.int ?: 0
                        val taxiFare = summary?.get("taxiFare")?.jsonPrimitive?.int ?: 0

                        updateUiState(item = uiState.item.copy(
                            routesCandidates = mutableStateListOf(Candidate(
                                routeResult = RouteResult("", goal, distance, duration, taxiFare, tollFare),
                                routes = routesInitial
                            )),
                            selectedCandidates = 0, sheetType = SheetType.PREVIEW, isRouteLoading = false
                        ))
                    } else {
                        requestWaypoints(goal)
                    }
                }
            }
        }
    }

    fun requestWaypoints(goal: MapLatLng) {
        viewModelScope.launch {
            if (uiState.item.layovers.isNotEmpty()) {
                val candidates = mutableStateListOf<Candidate>()
                val _candidates = uiState.item.routesInitial.mapIndexed { index, latLng ->
                    val waypointResponse = client.get("https://dapi.kakao.com/v2/local/search/keyword.JSON?query=${uiState.item.layovers.first().second}&x=${latLng.longitude}&y=${latLng.latitude}&sort=accuracy&page=1&size=3") {
                        header("Authorization", ApiKey.KAKAO_API_KEY)
                    }

                    val result = Json.decodeFromString<JsonObject>(waypointResponse.bodyAsText())["documents"]?.jsonArray?.map {
                        val jsonObject = it.jsonObject
                        val title = jsonObject["place_name"]?.jsonPrimitive?.content ?: ""
                        val category = jsonObject["category_group_name"]?.jsonPrimitive?.content ?: ""
                        val address = jsonObject["address_name"]?.jsonPrimitive?.content ?: ""
                        val roadAddress = jsonObject["road_address_name"]?.jsonPrimitive?.content ?: ""
                        val latitude = jsonObject["y"]?.jsonPrimitive?.content?.toDouble() ?: 0.0
                        val longitude = jsonObject["x"]?.jsonPrimitive?.content?.toDouble() ?: 0.0

                        QueryResult(title = title, categories = category.split(">"), address = address, roadAddress = roadAddress, latitude = latitude, longitude = longitude)
                    }?.let { if (it.isNotEmpty()) it.first() else null }

                    if (result != null) {
                        val routeResponse = client.get("https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?goal=${goal.longitude}%2C${goal.latitude}&start=${uiState.item.currentLatLng.longitude}%2C${uiState.item.currentLatLng.latitude}&option=${uiState.item.trafficOption}&waypoints=${result.longitude},${result.latitude}") {
                            header("x-ncp-apigw-api-key-id", ApiKey.NAVER_MAP_CLIENT_KEY_ID)
                            header("x-ncp-apigw-api-key", ApiKey.NAVER_MAP_CLIENT_KEY)
                        }

                        val routeResult = Json.decodeFromString<JsonObject>(routeResponse.bodyAsText())["route"]?.jsonObject?.get(uiState.item.trafficOption)?.jsonArray

                        if (routeResult?.isNotEmpty() == true) {
                            val summary = routeResult[0].jsonObject["summary"]?.jsonObject
                            val path = routeResult[0].jsonObject["path"]?.jsonArray?.map { json ->
                                json.jsonArray.let { MapLatLng(it[1].jsonPrimitive.double, it[0].jsonPrimitive.double) }
                            }

                            val distance = summary?.get("distance")?.jsonPrimitive?.int ?: 0
                            val duration = summary?.get("duration")?.jsonPrimitive?.int ?: 0
                            val tollFare = summary?.get("tollFare")?.jsonPrimitive?.int ?: 0
                            val taxiFare = summary?.get("taxiFare")?.jsonPrimitive?.int ?: 0

                            Candidate(RouteResult(waypoint = result.title, latLng = MapLatLng(result.latitude, result.longitude), distance = distance, duration = duration, tollFare = tollFare, taxiFare = taxiFare), path)
                        } else null
                    } else {
                        null
                    }
                }.sortedBy { it?.routeResult?.duration }
                    .distinct().filterNotNull()

                candidates.addAll(_candidates)
                updateUiState(item = uiState.item.copy(routesCandidates = candidates, selectedCandidates = 0, sheetType = SheetType.PREVIEW, isRouteLoading = false))
            }

        }
    }

    fun updateFavorite(query: QueryResult) {
        viewModelScope.launch {
            val pre = favoriteRepository.getByCoordinateOnlyEtc(query.latitude, query.longitude).first()
            val entity = Favorite(label = query.title, address = query.address, latitude = query.latitude, longitude = query.longitude, type = -1, registerTime = KZonedDateTime.now(), lastUsedTime = KZonedDateTime.now())
            if (pre == null) {
                favoriteRepository.upsert(entity)
            } else {
                favoriteRepository.delete(pre)
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class TransportHistoryUiStateList(
    val itemList: List<HistoryDetails> = listOf(),
    val isInitialized: Boolean = false
)

data class TransportFavoriteUiStateList(
    val itemList: List<FavoriteDetails> = listOf(),
    val isInitialized: Boolean = false
)

data class TransportQueryUiState(
    val itemList: List<QueryResult> = listOf(),
    val isInitialized: Boolean = false
)

data class TransportUiState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val item: TransportDetails = TransportDetails(),
)

data class TransportDetails @OptIn(ExperimentalMaterial3Api::class) constructor(
    val query: String = "",
    val searchActive: Boolean = false,

    val sheetType: SheetType = SheetType.SEARCH,
    val sheetState: SheetValue = SheetValue.PartiallyExpanded,
    val isOrdered: Boolean = false,
    val selectedQuery: QueryResult? = null,
    val trafficOption: String = MapDirection.TrafficOption.OPTIMAL,

    val isCurrentInitialized: Boolean = false,
    val currentRoadAddress: String = "",
    val currentAddress: String = "",
    val currentLatLng: MapLatLng = MapLatLng(Pref.Default.CURRENT_LATITUDE, Pref.Default.CURRENT_LONGITUDE),

    val selectedRoadAddress: String = "",
    val selectedAddress: String = "",
    val selectedLatLng: MapLatLng = MapLatLng(Pref.Default.CURRENT_LATITUDE, Pref.Default.CURRENT_LONGITUDE),

    val layovers: SnapshotStateList<Pair<Long, String>> = mutableStateListOf(),
    val markers: SnapshotStateList<Triple<Int, Double, Double>> = mutableStateListOf(),
    val isRouteLoading: Boolean = false,

    val selectedCandidates: Int = -1,
    val routesInitial: SnapshotStateList<MapLatLng> = mutableStateListOf(),
    val routesCandidates: SnapshotStateList<Candidate> = mutableStateListOf(),
    val routesFinal: SnapshotStateList<MapLatLng> = mutableStateListOf(),
)

data class HistoryDetails(
    val id: Long = -1,
    val label: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: String = "",
    val lastUsedTime: KZonedDateTime = KZonedDateTime.now()
)

data class FavoriteDetails(
    val id: Long = -1,
    val label: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: Int = -1,
    val registerTime: KZonedDateTime = KZonedDateTime.now(),
    val lastUsedTime: KZonedDateTime = KZonedDateTime.now()
)

fun Favorite.toFavoriteDetails() = FavoriteDetails(id = id ?: 1, label = label, address = address, latitude = latitude, longitude = longitude, type = type, registerTime = registerTime, lastUsedTime = lastUsedTime)
fun FavoriteDetails.toFavorite() = Favorite(label = label, address = address, latitude = latitude, longitude = longitude, type = type, registerTime = registerTime, lastUsedTime = lastUsedTime).apply { id = this@toFavorite.id }
fun History.toHistoryDetails() = HistoryDetails(id = id ?: 1, label = label, address = address, latitude = latitude, longitude = longitude, category = category, lastUsedTime = lastUsedTime)
fun HistoryDetails.toHistory() = History(label = label, address = address, latitude = latitude, longitude = longitude, category = category, lastUsedTime = lastUsedTime).apply { id = this@toHistory.id }