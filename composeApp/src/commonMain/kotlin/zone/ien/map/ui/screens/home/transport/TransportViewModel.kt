package zone.ien.map.ui.screens.home.transport

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunnychung.lib.multiplatform.kdatetime.KZonedDateTime
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.encodeURLParameter
import io.ktor.http.headers
import io.ktor.util.toMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import zone.ien.map.TAG
import zone.ien.map.constant.ApiKey
import zone.ien.map.constant.Pref
import zone.ien.map.data.QueryResult
import zone.ien.map.data.favorite.Favorite
import zone.ien.map.data.favorite.FavoriteRepository
import zone.ien.map.data.history.History
import zone.ien.map.data.history.HistoryRepository
import zone.ien.map.datastore.DEFAULT_DATASTORE
import zone.ien.map.utils.Dlog
import zone.ien.map.utils.LocationUtils
import zone.ien.map.utils.measure
import zone.ien.map.utils.now
import zone.ien.map.utils.toAddress

@OptIn(ExperimentalMaterial3Api::class)
class TransportViewModel(
    val favoriteRepository: FavoriteRepository,
    val historyRepository: HistoryRepository
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
            dataStore.data.collect {
                val currentLatitude = it[Pref.Key.CURRENT_LATITUDE] ?: 0.0
                val currentLongitude = it[Pref.Key.CURRENT_LONGITUDE] ?: 0.0

                updateUiState(uiState.item.copy(currentLatLng = Pair(currentLatitude, currentLongitude)))
                getAddress(currentLatitude, currentLongitude)
            }
        }
    }

    fun updateUiState(item: TransportDetails) {
        uiState = TransportUiState(item = item)
    }

    fun getCurrentLocation() {
        LocationUtils.getCurrentLocation()
    }

    fun getAddress(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.get("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=${longitude}%2C${latitude}&output=json&orders=addr%2Croadaddr") {
                    header("x-ncp-apigw-api-key-id", ApiKey.NAVER_MAP_CLIENT_KEY_ID)
                    header("x-ncp-apigw-api-key", ApiKey.NAVER_MAP_CLIENT_KEY)
                }
                val result = Json.decodeFromString<JsonObject>(response.bodyAsText())["results"]?.jsonArray
                updateUiState(uiState.item.copy(currentAddress = result?.toAddress() ?: ""))
            } catch (e: Exception) {
                Dlog.e(TAG, "error: ${e}")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun searchDestination(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                queryUiState = TransportQueryUiState(isInitialized = false, itemList = listOf())
                val response = client.get("https://openapi.naver.com/v1/search/local.json?query=${query.encodeURLParameter()}&display=10&start=5&sort=random") {
                    header("X-Naver-Client-Id", ApiKey.NAVER_CLIENT_ID)
                    header("X-Naver-Client-Secret", ApiKey.NAVER_CLIENT_SECRET)
                }
                val result = Json.decodeFromString<JsonObject>(response.bodyAsText())["items"]?.jsonArray?.map {
                    val jsonObject = it.jsonObject
                    val title = jsonObject["title"]?.jsonPrimitive?.content ?: ""
                    val category = jsonObject["category"]?.jsonPrimitive?.content ?: ""
                    val telephone = jsonObject["telephone"]?.jsonPrimitive?.content ?: ""
                    val address = jsonObject["address"]?.jsonPrimitive?.content ?: ""
                    val roadAddress = jsonObject["roadAddress"]?.jsonPrimitive?.content ?: ""
                    val latitude = jsonObject["mapy"]?.jsonPrimitive?.int?.div(10000000.0) ?: 0.0
                    val longitude = jsonObject["mapx"]?.jsonPrimitive?.int?.div(10000000.0) ?: 0.0

                    QueryResult(title = title, categories = category.split(">"), telephone = telephone, address = address, roadAddress = roadAddress, latitude = latitude, longitude = longitude)
                }

                queryUiState = TransportQueryUiState(isInitialized = true, itemList = result ?: listOf())
                updateUiState(uiState.item.copy(searchActive = true))
            } catch (e: Exception) {
                Dlog.e(TAG, "error: ${e}")
            }
        }
    }

    fun requestRoute() {
        // 저장
        CoroutineScope(Dispatchers.IO).launch {
            uiState.item.selectedQuery?.let { query ->
                val item = History(label = query.title, address = query.address, latitude = query.latitude, longitude = query.longitude, category = query.categories.joinToString(">"))
                val id = historyRepository.getByCoordinate(query.latitude, query.longitude)

                if (id == null) {
                    historyRepository.upsert(item)
                } else {
                    historyRepository.upsert(item.apply { this.id = id })
                }
            }
        }

        // 경로 요청


        // 표시
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
//    val queryResults: List<QueryResult> = listOf(),
    val sheetType: SheetType = SheetType.SEARCH,
    val sheetState: SheetValue = SheetValue.PartiallyExpanded,
    val isOrdered: Boolean = false,
    val selectedQuery: QueryResult? = null,

    val currentAddress: String = "",
    val currentLatLng: Pair<Double, Double> = Pair(0.0, 0.0),
    val layovers: List<String> = listOf(),
//    val markers: List<Pair<Double, Double>> = listOf()
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