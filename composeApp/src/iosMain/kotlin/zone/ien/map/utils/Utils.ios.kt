package zone.ien.map.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

/*
actual fun getPermissions(): List<Permissions> {
    return listOf(
        Permissions(icon = AdaptiveIcons.Calendar, color = CupertinoColor.systemBlue, title = Res.string.read_calendar, content = Res.string.read_calendar_desc, permissions = listOf(PermissionId.READ_CALENDAR)),
        Permissions(icon = AdaptiveIcons.Notifications, color = CupertinoColor.systemPink, title = Res.string.post_notification, content = Res.string.post_notification_desc, permissions = listOf(PermissionId.POST_NOTIFICATIONS)),
        Permissions(icon = AdaptiveIcons.Location, color = CupertinoColor.systemPurple, title = Res.string.access_location, content = Res.string.access_location_desc, permissions = listOf(PermissionId.ACCESS_LOCATION))
    )
}

actual object Utils: KoinComponent {
    actual fun checkPermissions(): Boolean {
        var result = true
        val locationPermissionHandler: LocationPermissionHandler by inject()

        getPermissions().forEach { permissions ->
            when {
                permissions.permissions.contains(PermissionId.POST_NOTIFICATIONS) -> {
                    UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { if (it?.authorizationStatus != UNAuthorizationStatusAuthorized) result = false }
                }
                permissions.permissions.contains(PermissionId.READ_CALENDAR) -> {
                    if (EKEventStore.authorizationStatusForEntityType(entityType = EKEntityType.EKEntityTypeEvent) != EKAuthorizationStatusFullAccess) result = false
                }
                permissions.permissions.contains(PermissionId.ACCESS_LOCATION) -> {
                    if (locationPermissionHandler.isLocationPermissionGranted() != 1) result = false
                }
            }
        }

        return result
    }

    actual fun getAlarmRingtones(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        result[Pref.Default.DEFAULT_SOUND] = Pref.Default.DEFAULT_SOUND

        for ((path, name) in ringtones) {
            if (path == "morning_joy.mp3") {
                continue
            } else {
                result["${ALARM_PATH}/$path"] = name
            }
        }

        return result
    }

    actual fun setAlarmClock(entity: Alarm): KZonedDateTime {
        TODO("Not yet implemented")
    }
}

 */