package zone.ien.map.ui.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import io.github.alexzhirkevich.cupertino.CupertinoButton
import io.github.alexzhirkevich.cupertino.CupertinoButtonDefaults.plainButtonColors
import io.github.alexzhirkevich.cupertino.CupertinoButtonSize
import io.github.alexzhirkevich.cupertino.CupertinoDropdownMenu
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.MenuAction
import io.github.alexzhirkevich.cupertino.MenuSection
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveTextButton
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveWidget
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import io.github.alexzhirkevich.cupertino.adaptive.Theme
import io.github.alexzhirkevich.cupertino.adaptive.currentTheme
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
import kotlinx.coroutines.launch
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.more_options
import org.jetbrains.compose.resources.stringResource
import zone.ien.map.ui.utils.view.AdaptiveIcon
import zone.ien.map.ui.utils.view.AdaptiveIcons
import zone.ien.map.ui.utils.view.AdaptiveText

sealed interface ActionMenuItem {
    val title: String
    val onClick: () -> Unit
    val isVisible: Boolean
    val icon: ImageVector?

    data class AlwaysShown(
        override val title: String,
        override val onClick: () -> Unit,
        override val icon: ImageVector?,
        override val isVisible: Boolean = true
    ) : ActionMenuItem

    data class ShownIfRoom(
        override val title: String,
        override val onClick: () -> Unit,
        override val icon: ImageVector?,
        override val isVisible: Boolean = true
    ) : ActionMenuItem

    data class NeverShown(
        override val title: String,
        override val onClick: () -> Unit,
        override val icon: ImageVector?,
        override val isVisible: Boolean = true
    ): ActionMenuItem

}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalAdaptiveApi::class, ExperimentalCupertinoApi::class
)
@Composable
fun ActionsMenu(
    items: List<ActionMenuItem>,
    isOpen: Boolean,
    closeDropdown: () -> Unit,
    onToggleOverflow: () -> Unit,
    maxVisibleItems: Int,
) {
    val menuItems = remember(items, maxVisibleItems) { splitMenuItems(items, maxVisibleItems) }
    val coroutineScope = rememberCoroutineScope()
    menuItems.alwaysShownItems.forEach { item ->
        val tooltipState = rememberMyBasicTooltipState(isPersistent = false)
        var width = 0
        val positionProvider = object: PopupPositionProvider {
            override fun calculatePosition(anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize): IntOffset {
                if (popupContentSize.width != 0) width = popupContentSize.width
                val x = anchorBounds.left + (anchorBounds.width - width) / 2
                val y = anchorBounds.bottom
                return IntOffset(x, y)
            }
        }

        if (item.isVisible) {
            MyBasicTooltipBox(
                positionProvider = positionProvider,
                state = tooltipState,
                focusable = false,
                enableUserInput = false,
                tooltip = {
                    AdaptiveText(
                        text = item.title,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp)
                    )
                },
            ) {
                item.icon?.let { icon ->
                    AdaptiveIconButton(
                        onClick = item.onClick,
                        onLongClick = {
                            coroutineScope.launch {
                                tooltipState.show()
                            }
                        },
                    ) {
                        AnimatedContent(
                            targetState = item.icon,
                            label = "menu_icon"
                        ) {
                            AdaptiveIcon(
                                imageVector = icon,
                                contentDescription = item.title,
                            )
                        }
                    }
                } ?: run {
                    AdaptiveTextButton(
                        onClick = item.onClick,
                    ) {
                        AdaptiveText(text = item.title)
                    }
                }
            }
        }
    }

    if (menuItems.overflowItems.isNotEmpty()) {
        val tooltipState = rememberMyBasicTooltipState(isPersistent = false)
        var width = 0
        val positionProvider = object: PopupPositionProvider {
            override fun calculatePosition(anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize): IntOffset {
                if (popupContentSize.width != 0) width = popupContentSize.width
                val x = anchorBounds.left + (anchorBounds.width - width) / 2
                val y = anchorBounds.bottom
                return IntOffset(x, y)
            }
        }
        MyBasicTooltipBox(
            positionProvider = positionProvider,
            state = tooltipState,
            focusable = false,
            enableUserInput = false,
            tooltip = {
                AdaptiveText(
                    text = stringResource(Res.string.more_options),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp)
                )
            },
        ) {
            val isMaterialTheme = currentTheme == Theme.Material3
            AdaptiveIconButton(
                onClick = onToggleOverflow,
                onLongClick = {
                    if (isMaterialTheme) {
                        coroutineScope.launch {
                            tooltipState.show()
                        }
                    }
                },
            ) {
                AdaptiveIcon(
                    imageVector = AdaptiveIcons.get(AdaptiveIcons.ShowMore),
                    contentDescription = stringResource(Res.string.more_options),
                    tint = if (isOpen) { if (isMaterialTheme) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f) else CupertinoTheme.colorScheme.accent
                        .copy(alpha = 0.35f) } else null
                )
            }
        }
        AdaptiveWidget(
            material = {
                DropdownMenu(
                    expanded = isOpen,
                    onDismissRequest = onToggleOverflow,
                ) {
                    menuItems.overflowItems.forEach { item ->
                        if (item.isVisible) {
                            DropdownMenuItem(
                                text = { AdaptiveText(text = item.title) },
                                trailingIcon = { item.icon?.let { AdaptiveIcon(imageVector = it, contentDescription = item.title) } },
                                onClick = {
                                    closeDropdown()
                                    item.onClick()
                                }
                            )
                        }
                    }
                }
            },
            cupertino = {
                CupertinoDropdownMenu(
                    expanded = isOpen,
                    onDismissRequest = onToggleOverflow
                ) {
                    MenuSection {
                        menuItems.overflowItems.forEach { item ->
                            if (item.isVisible) {
                                MenuAction(
                                    icon = { item.icon?.let { AdaptiveIcon(imageVector = it, contentDescription = item.title) } },
                                    title = { AdaptiveText(text = item.title) },
                                    onClick = {
                                        closeDropdown()
                                        item.onClick()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalCupertinoApi::class, ExperimentalFoundationApi::class)
@Composable
fun AdaptiveIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    AdaptiveWidget(
        material = {
            @Suppress("DEPRECATION_ERROR")
            val buttonColors = IconButtonDefaults.iconButtonColors()
            val containerColor = if (enabled) buttonColors.containerColor else buttonColors.disabledContainerColor
            val contentColor = if (enabled) buttonColors.contentColor else buttonColors.disabledContentColor
            Box(
                modifier = modifier
                    .minimumInteractiveComponentSize()
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = containerColor)
                    .combinedClickable(
                        enabled = enabled,
                        onClick = onClick,
                        onLongClick = onLongClick,
                        role = Role.Button,
                    ),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
            }
        },
        cupertino = {
            val buttonColors = plainButtonColors()

            CupertinoButton(
                onClick = onClick,
                modifier = modifier.size(42.dp),
                enabled = enabled,
                colors = buttonColors,
                size = CupertinoButtonSize.Regular,
                shape = CircleShape,
                border = null,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(0.dp),
                content = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        content()
                    }
                }
            )
        }
    )
}

private data class MenuItems(
    val alwaysShownItems: List<ActionMenuItem>,
    val overflowItems: List<ActionMenuItem>,
)

private fun splitMenuItems(
    items: List<ActionMenuItem>,
    maxVisibleItems: Int,
): MenuItems {
    val alwaysShownItems: MutableList<ActionMenuItem> = items.filterIsInstance<ActionMenuItem.AlwaysShown>().toMutableList()
    val ifRoomItems: MutableList<ActionMenuItem> = items.filterIsInstance<ActionMenuItem.ShownIfRoom>().toMutableList()
    val overflowItems = items.filterIsInstance<ActionMenuItem.NeverShown>()

    val hasOverflow = overflowItems.isNotEmpty() || (alwaysShownItems.size + ifRoomItems.size - 1) > maxVisibleItems
    val usedSlots = alwaysShownItems.size + (if (hasOverflow) 1 else 0)
    val availableSlots = maxVisibleItems - usedSlots
    if (availableSlots > 0 && ifRoomItems.isNotEmpty()) {
        val visible = ifRoomItems.subList(0, availableSlots.coerceAtMost(ifRoomItems.size))
        alwaysShownItems.addAll(visible)
        ifRoomItems.removeAll(visible)
    }

    return MenuItems(
        alwaysShownItems = alwaysShownItems,
        overflowItems = ifRoomItems + overflowItems,
    )
}