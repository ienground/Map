package zone.ien.map.constant

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Pref {
    object Key {
        val CURRENT_LATITUDE = doublePreferencesKey("current_latitude")
        val CURRENT_LONGITUDE = doublePreferencesKey("current_longitude")
    }

    object Default {
//        const val CURRENT_LATITUDE = 37.552987017
//        const val CURRENT_LONGITUDE = 126.972591728
        const val CURRENT_LATITUDE = 37.555637422905775
        const val CURRENT_LONGITUDE = 126.94176515750824
    }
}