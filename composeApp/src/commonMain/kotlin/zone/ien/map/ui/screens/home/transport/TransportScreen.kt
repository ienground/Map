package zone.ien.map.ui.screens.home.transport

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import zone.ien.map.ui.AppViewModelProvider
import zone.ien.map.ui.navigation.NavigationDestination
import zone.ien.map.ui.screens.home.HomeScreenBody
import zone.ien.map.utils.maps.MapScreen

object TransportDestination: NavigationDestination {
    override val route: String = "transport"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(
    modifier: Modifier = Modifier,
    viewModel: TransportViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    BottomSheetScaffold(
        sheetContent = {
            Text("sheet", fontSize = 24.sp)
        },
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle()
            Text("drag")
        }
    ) {
        TransportScreenBody(
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun TransportScreenBody(
    modifier: Modifier = Modifier
) {
    MapScreen(
        modifier = modifier.fillMaxSize()
    )
}