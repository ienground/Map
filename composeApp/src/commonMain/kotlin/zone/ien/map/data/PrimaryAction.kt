package zone.ien.map.data

import androidx.compose.ui.graphics.vector.ImageVector

data class PrimaryAction(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit,
    val expanded: Boolean
)