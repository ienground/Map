package zone.ien.map.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import zone.ien.map.TAG
import zone.ien.map.constant.Pref
import zone.ien.map.datastore.DEFAULT_DATASTORE

actual object LocationUtils: KoinComponent {
    private val context: Context by inject()
    private val lm: LocationManager by lazy { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    actual fun getCurrentLocation(setLatLng: (MapLatLng) -> Unit) {
        val isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val locationListener = LocationListener { location ->
            CoroutineScope(Dispatchers.IO).launch {
                setLatLng(MapLatLng(location.latitude, location.longitude))
            }
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isNetworkEnabled) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10f, locationListener)
            } else if (isGPSEnabled) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, locationListener)
            }
        }
    }
}