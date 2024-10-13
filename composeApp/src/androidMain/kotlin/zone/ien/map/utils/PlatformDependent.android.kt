package zone.ien.map.utils

import zone.ien.map.BuildConfig

actual object PlatformDependent {
    actual val isDebug: Boolean = BuildConfig.DEBUG
}