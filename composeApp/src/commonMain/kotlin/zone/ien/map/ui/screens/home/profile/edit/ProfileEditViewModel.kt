package zone.ien.map.ui.screens.home.profile.edit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import zone.ien.map.TAG
import zone.ien.map.constant.ApiKey
import zone.ien.map.constant.Pref
import zone.ien.map.data.QueryResult
import zone.ien.map.data.favorite.Favorite
import zone.ien.map.data.favorite.FavoriteRepository
import zone.ien.map.ui.screens.home.transport.TransportQueryUiState
import zone.ien.map.utils.Dlog
import zone.ien.map.utils.LocationUtils
import zone.ien.map.utils.MapLatLng

class ProfileEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val favoriteRepository: FavoriteRepository
): ViewModel() {
    private val itemType: Int = checkNotNull(savedStateHandle[ProfileEditDestination.itemTypeArg])

    var uiState: ProfileEditUiState by mutableStateOf(ProfileEditUiState())
        private set

    private val client = HttpClient()

    init {
        viewModelScope.launch {
            val entity = favoriteRepository.getByType(itemType).first() ?: Favorite(label = "", address = "", latitude = Pref.Default.CURRENT_LATITUDE, longitude = Pref.Default.CURRENT_LONGITUDE, type = itemType)
            uiState = ProfileEditUiState(item = ProfileEditDetails(type = itemType, id = entity.id ?: -1, currentLatLng = MapLatLng(entity.latitude, entity.longitude), query = entity.label, selectedLatLng = MapLatLng(entity.latitude, entity.longitude)), isItemInitialized = true, isQueryInitialized = uiState.isQueryInitialized, isCurrentInitialized = uiState.isCurrentInitialized)
            getCurrentLocation()
        }
    }

    fun updateUiState(item: ProfileEditDetails) {
        uiState = ProfileEditUiState(item = item, isQueryInitialized = uiState.isQueryInitialized, isItemInitialized = uiState.isItemInitialized, isCurrentInitialized = uiState.isCurrentInitialized)
    }

    fun getCurrentLocation() {
        LocationUtils.getCurrentLocation {
            uiState = ProfileEditUiState(item = uiState.item.copy(currentLatLng = it), isCurrentInitialized = true, isQueryInitialized = uiState.isQueryInitialized, isItemInitialized = uiState.isItemInitialized)
        }
    }

    fun searchDestination(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                uiState = ProfileEditUiState(item = uiState.item.copy(queryResults = listOf()), isQueryInitialized = false, isItemInitialized = uiState.isItemInitialized, isCurrentInitialized = uiState.isCurrentInitialized)

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

                uiState = ProfileEditUiState(item = uiState.item.copy(searchBarExpanded = true, queryResults = result ?: listOf()), isQueryInitialized = true, isItemInitialized = uiState.isItemInitialized, isCurrentInitialized = uiState.isCurrentInitialized)
            } catch (e: Exception) {
                Dlog.e(TAG, "error: ${e}")
            }
        }
    }

    suspend fun save() {
        uiState.item.selectedQuery?.let { query ->
            favoriteRepository.upsert(Favorite(label = query.title, address = query.roadAddress, latitude = uiState.item.selectedLatLng.latitude, longitude = uiState.item.selectedLatLng.longitude, type = uiState.item.type).apply {
                if (uiState.item.id != -1L) id = uiState.item.id
            })
        }
    }
}

data class ProfileEditUiState(
    val item: ProfileEditDetails = ProfileEditDetails(),
    val isItemInitialized: Boolean = false,
    val isQueryInitialized: Boolean = true,
    val isCurrentInitialized: Boolean = false,
)

data class ProfileEditDetails(
    val type: Int = Favorite.Type.ETC,
    val id: Long = -1,
    val query: String = "",
    val selectedQuery: QueryResult? = null,
    val queryResults: List<QueryResult> = listOf(),
    val searchBarExpanded: Boolean = false,
    val currentLatLng: MapLatLng = MapLatLng(Pref.Default.CURRENT_LATITUDE, Pref.Default.CURRENT_LONGITUDE),
    val selectedLatLng: MapLatLng = MapLatLng(Pref.Default.CURRENT_LATITUDE, Pref.Default.CURRENT_LONGITUDE)
)