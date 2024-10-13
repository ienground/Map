package zone.ien.map.ui.utils.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.Composable
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import io.github.alexzhirkevich.cupertino.adaptive.Theme
import io.github.alexzhirkevich.cupertino.adaptive.currentTheme
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.save
import org.jetbrains.compose.resources.stringResource
import zone.ien.map.ui.utils.ActionMenuItem

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun ButtonSave(
    onClick: () -> Unit,
    isVisible: Boolean = true
): ActionMenuItem =
    ActionMenuItem.ShownIfRoom(
        title = stringResource(Res.string.save),
        icon = if (currentTheme == Theme.Material3) Icons.Rounded.Save else null,
        onClick = onClick,
        isVisible = isVisible
    )