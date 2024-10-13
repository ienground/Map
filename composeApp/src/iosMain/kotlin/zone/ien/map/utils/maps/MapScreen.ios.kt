package zone.ien.map.utils.maps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.interop.UIKitViewController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.MapKit.MKMapView
import platform.UIKit.UIControlEventEditingChanged
import platform.UIKit.UITextField
import platform.UIKit.UIViewController

import zone.ien.map.TAG
import zone.ien.map.utils.Dlog
import zone.ien.map.utils.SwiftUIFactory

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapScreen(modifier: Modifier) {
    Dlog.i(TAG, "${SwiftUIFactory.shared}")
    Text("hello map")
    SwiftUIFactory.shared?.let {
        UIKitViewController(
            factory = {
                it.makeController()
            },
            modifier = modifier
        )
    }
}