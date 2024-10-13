package zone.ien.map.ui

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import zone.ien.map.ui.screens.home.HomeViewModel
import zone.ien.map.ui.screens.home.profile.ProfileViewModel
import zone.ien.map.ui.screens.home.transport.TransportViewModel
import zone.ien.map.ui.screens.permissions.PermissionsViewModel

object AppViewModelProvider: KoinComponent {
//    private val container: AppContainer by inject()

    val factory = viewModelFactory {
        initializer {
            HomeViewModel()
        }

        initializer {
            PermissionsViewModel()
        }

        initializer {
            TransportViewModel()
        }

        initializer {
            ProfileViewModel()
        }

//        initializer {
//            HomeViewModel(
//                container.alarmRepository
//            )
//        }
    }
}

const val TIMEOUT_MILLIS = 5_000L