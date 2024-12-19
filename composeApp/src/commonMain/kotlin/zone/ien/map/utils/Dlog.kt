package zone.ien.map.utils

import co.touchlab.kermit.Logger

object Dlog {
    /**
     * Log Level Error
     */
    fun e(tag: String, message: String) {
        if (PlatformDependent.isDebug) Logger.e(tag) { message }
    }

    /**
     * Log Level Warning
     */
    fun w(tag: String, message: String) {
        if (PlatformDependent.isDebug) Logger.w(tag) { message }
    }

    /**
     * Log Level Information
     */
    fun i(tag: String, message: String) {
        if (PlatformDependent.isDebug) Logger.i(tag) { message }
    }

    /**
     * Log Level Debug
     */
    fun d(tag: String, message: String) {
        if (PlatformDependent.isDebug) Logger.w(tag) { message }
    }

    /**
     * Log Level Verbose
     */
    fun v(tag: String, message: String) {
        if (PlatformDependent.isDebug) Logger.v(tag) { message }
    }
}