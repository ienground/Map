package zone.ien.map.utils

import kotlin.experimental.ExperimentalNativeApi

actual object PlatformDependent {
    @OptIn(ExperimentalNativeApi::class)
    actual val isDebug = Platform.isDebugBinary
}