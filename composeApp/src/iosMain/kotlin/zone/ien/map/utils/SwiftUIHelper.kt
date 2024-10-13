package zone.ien.map.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

interface SwiftUIFactory {
    companion object {
        var shared: SwiftUIFactory? = null
    }

    fun makeController() : UIViewController
}