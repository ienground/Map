package zone.ien.map.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.NativePtr
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject
import zone.ien.map.TAG

actual object LocationUtils {
    @OptIn(ExperimentalForeignApi::class)
    actual fun getCurrentLocation(setLatLng: (MapLatLng) -> Unit) {
        val manager = IosLocationManager()

        CoroutineScope(Dispatchers.IO).launch {
            val location = manager.requestCurrentLocation()
            if (location.isSuccess) {
                location.getOrNull()?.let {
                    it.coordinate.useContents {
                        setLatLng(MapLatLng(latitude, longitude))
                    }
                }
            }
        }
    }
}