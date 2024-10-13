package zone.ien.map.ui.utils.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NotificationAdd
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Today
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.alexzhirkevich.cupertino.adaptive.icons.AdaptiveIcons
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.filled.QuestionmarkCircle
import io.github.alexzhirkevich.cupertino.icons.outlined.Bell
import io.github.alexzhirkevich.cupertino.icons.outlined.BellBadge
import io.github.alexzhirkevich.cupertino.icons.outlined.Calendar
import io.github.alexzhirkevich.cupertino.icons.outlined.CheckerboardShield
import io.github.alexzhirkevich.cupertino.icons.outlined.Desktopcomputer
import io.github.alexzhirkevich.cupertino.icons.outlined.EllipsisCircle
import io.github.alexzhirkevich.cupertino.icons.outlined.Gearshape
import io.github.alexzhirkevich.cupertino.icons.outlined.Location
import io.github.alexzhirkevich.cupertino.icons.outlined.PlusSquare
import io.github.alexzhirkevich.cupertino.icons.outlined.Trash


enum class AdaptiveIcons {
    Save, Delete, ShowMore, Settings, Guide, Location

    ;

    companion object {
        @Composable
        fun get(key: zone.ien.map.ui.utils.view.AdaptiveIcons): ImageVector {
            return when (key) {
                Save ->
                    AdaptiveIcons.vector(
                        material = { Icons.Rounded.Save },
                        cupertino = { CupertinoIcons.Default.PlusSquare }
                    )
                Delete ->
                    AdaptiveIcons.vector(
                        material = { Icons.Rounded.Delete },
                        cupertino = { CupertinoIcons.Default.Trash }
                    )
                ShowMore ->
                    AdaptiveIcons.vector(
                        material = { Icons.Rounded.MoreVert },
                        cupertino = { CupertinoIcons.Default.EllipsisCircle }
                    )
                Settings ->
                    AdaptiveIcons.vector(
                        material = { Icons.Rounded.Settings },
                        cupertino = { CupertinoIcons.Default.Gearshape }
                    )

                Guide ->
                    AdaptiveIcons.vector(
                        material = { Icons.AutoMirrored.Rounded.Help },
                        cupertino = { CupertinoIcons.Filled.QuestionmarkCircle }
                    )

                Location ->
                    AdaptiveIcons.vector(
                        material = { Icons.Rounded.LocationOn },
                        cupertino = { CupertinoIcons.Default.Location }
                    )
            }
        }
    }
}