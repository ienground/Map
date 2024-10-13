package zone.ien.map.ui.utils.view

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastSumBy
import io.github.alexzhirkevich.cupertino.CupertinoBottomSheetScaffold
import io.github.alexzhirkevich.cupertino.CupertinoBottomSheetScaffoldState
import io.github.alexzhirkevich.cupertino.CupertinoTopAppBar
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveSwitch
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveWidget
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import io.github.alexzhirkevich.cupertino.rememberCupertinoBottomSheetScaffoldState
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import io.github.alexzhirkevich.cupertino.section.CupertinoSectionDefaults
import io.github.alexzhirkevich.cupertino.section.LocalSectionStyle
import io.github.alexzhirkevich.cupertino.section.ProvideSectionStyle
import io.github.alexzhirkevich.cupertino.section.SectionItem
import io.github.alexzhirkevich.cupertino.section.SectionScope
import io.github.alexzhirkevich.cupertino.section.SectionState
import io.github.alexzhirkevich.cupertino.section.SectionStyle
import io.github.alexzhirkevich.cupertino.section.rememberSectionState
import io.github.alexzhirkevich.cupertino.section.sectionContainerBackground
import zone.ien.map.data.PrimaryAction
import zone.ien.map.ui.utils.ActionMenuItem
import zone.ien.map.ui.utils.ActionsMenu

internal object SectionScopeImpl : SectionScope

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalCupertinoApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AdaptiveTopAppBarScaffold(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    scaffoldState: CupertinoBottomSheetScaffoldState = rememberCupertinoBottomSheetScaffoldState(),
    isTopBarCentered: Boolean = false,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: List<ActionMenuItem> = listOf(),
    primaryAction: PrimaryAction? = null,
    sheetContent: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    AdaptiveWidget(
        material = {
            var menuExpanded by remember { mutableStateOf(false) }

            Scaffold(
                modifier = modifier,
                bottomBar = bottomBar,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                content = content,
                topBar = {
                    if (isTopBarCentered) {
                        CenterAlignedTopAppBar(
                            title = title,
                            navigationIcon = navigationIcon,
                            actions = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    ActionsMenu(
                                        items = actions,
                                        isOpen = menuExpanded,
                                        closeDropdown = { menuExpanded = false },
                                        onToggleOverflow = { menuExpanded = !menuExpanded },
                                        maxVisibleItems = 3
                                    )
                                }
                            },
                            modifier = Modifier.height(80.dp)
                        )
                    } else {
                        TopAppBar(
                            title = title,
                            navigationIcon = navigationIcon,
                            actions = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    ActionsMenu(
                                        items = actions,
                                        isOpen = menuExpanded,
                                        closeDropdown = { menuExpanded = false },
                                        onToggleOverflow = { menuExpanded = !menuExpanded },
                                        maxVisibleItems = 3
                                    )
                                }
                            },
                            modifier = Modifier.height(80.dp)
                        )
                    }
                },
                floatingActionButton = {
                    primaryAction?.let { action ->
                        ExtendedFloatingActionButton(
                            expanded = action.expanded,
                            icon = { Icon(imageVector = action.icon, contentDescription = action.title) },
                            text = { Text(text = action.title) },
                            onClick = action.onClick
                        )
                    }
                },
                floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center
            )
        },
        cupertino = {
            var menuExpanded by remember { mutableStateOf(false) }
            val cupertinoActions = ArrayList<ActionMenuItem>().apply {
                addAll(actions)
                primaryAction?.let { action ->
                    add(ActionMenuItem.ShownIfRoom(
                        title = action.title,
                        onClick = action.onClick,
                        icon = action.icon
                    ))
                }
            }

            CupertinoBottomSheetScaffold(
                modifier = modifier,
                bottomBar = bottomBar,
                sheetContent = sheetContent,
                sheetDragHandle = null,
                scaffoldState = scaffoldState,
                topBar = {
                    CupertinoTopAppBar(
                        isTransparent = true,
                        title = {},
                        navigationIcon = navigationIcon,
                        actions = {
                            ActionsMenu(
                                items = cupertinoActions,
                                isOpen = menuExpanded,
                                closeDropdown = { menuExpanded = false },
                                onToggleOverflow = { menuExpanded = !menuExpanded },
                                maxVisibleItems = 3
                            )
                        }
                    )
                }
            ) {
                Column(
                    modifier = Modifier.padding(it)
                ) {
                    title()
                    content(PaddingValues(0.dp))
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(horizontal = 16.dp),
                snackbar = { Snackbar(it) }
            )
        }
    )
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun AdaptiveProvideSectionStyle(
    style: SectionStyle,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    AdaptiveWidget(
        material = {
            content(modifier)
        },
        cupertino = {
            ProvideSectionStyle(
                style = style
            ) {
                content(modifier.sectionContainerBackground(style))
            }
        }
    )
}

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalCupertinoApi::class)
@Composable
fun AdaptiveSection(
    modifier : Modifier = Modifier,
    style: SectionStyle = LocalSectionStyle.current,
    state: SectionState = rememberSectionState(canCollapse = true),
    enterTransition: EnterTransition = CupertinoSectionDefaults.EnterTransition,
    exitTransition: ExitTransition = CupertinoSectionDefaults.ExitTransition,
    shape : CornerBasedShape = CupertinoSectionDefaults.shape(style),
    color: Color = if (style.grouped) CupertinoSectionDefaults.Color else Color.Transparent,
    dividerPadding: PaddingValues = PaddingValues(
        start = CupertinoSectionDefaults.DividerPadding
    ),
    contentPadding : PaddingValues = CupertinoSectionDefaults.paddingValues(
        style = style,
        includePaddingBetweenSections = true
    ),
    title : (@Composable () -> Unit)? = null,
    caption : (@Composable () -> Unit)? = null,
    content : @Composable SectionScope.() -> Unit
) {
    AdaptiveWidget(
        material = {
            SubcomposeLayout() { constraints ->
                val measurables = subcompose(null) { content(SectionScopeImpl) }

                val placeables = measurables.fastMap { it.measure(constraints) }

                layout(
                    width = constraints.maxWidth,
                    height = (placeables).fastSumBy { it.height }
                ) {
                    var h = 0
                    placeables.fastForEachIndexed { i, p ->
                        p.place(0, h)
                        h += p.height
                    }
                }
            }
        },
        cupertino = {
            CupertinoSection(modifier, style, state, enterTransition, exitTransition, shape, color, dividerPadding, contentPadding, title, caption, content)
        }
    )
}

@OptIn(ExperimentalCupertinoApi::class, ExperimentalAdaptiveApi::class)
@Composable
fun SectionScope.AdaptiveSectionItem(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = CupertinoSectionDefaults.PaddingValues,
    leadingContent: @Composable () -> Unit = {},
    enableCupertinoLeadingContent: Boolean = false,
    trailingContent: @Composable () -> Unit = {},
    onClick: () -> Unit,
    title: @Composable () -> Unit
) {
    AdaptiveWidget(
        material = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClick() }
            ) {
                leadingContent()
                title()
                Spacer(modifier = Modifier.weight(1f))
                trailingContent()
            }
        },
        cupertino = {
            SectionItem(
                modifier,
                paddingValues,
                if (enableCupertinoLeadingContent) leadingContent else {{}},
                trailingContent,
                title
            )
        }
    )
}

@OptIn(ExperimentalCupertinoApi::class, ExperimentalAdaptiveApi::class)
@Composable
fun SectionScope.AdaptiveSectionSwitchItem(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = CupertinoSectionDefaults.PaddingValues,
    leadingContent: @Composable () -> Unit = {},
    enableCupertinoLeadingContent: Boolean = false,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: @Composable () -> Unit
) {
    AdaptiveWidget(
        material = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onCheckedChange(!checked) }
            ) {
                leadingContent()
                title()
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        cupertino = {
            SectionItem(
                modifier,
                paddingValues,
                if (enableCupertinoLeadingContent) leadingContent else {{}},
                trailingContent = {
                    AdaptiveSwitch(
                        checked = checked,
                        onCheckedChange = onCheckedChange
                    )
                },
                title
            )
        }
    )
}


@OptIn(ExperimentalCupertinoApi::class, ExperimentalAdaptiveApi::class)
@Composable
fun SectionScope.AdaptiveSectionCheckboxItem(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = CupertinoSectionDefaults.PaddingValues,
    leadingContent: @Composable () -> Unit = {},
    enableCupertinoLeadingContent: Boolean = false,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: @Composable () -> Unit
) {
    AdaptiveWidget(
        material = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onCheckedChange(!checked) }
            ) {
                leadingContent()
                title()
                Spacer(modifier = Modifier.weight(1f))
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        cupertino = {
            SectionItem(
                modifier,
                paddingValues,
                if (enableCupertinoLeadingContent) leadingContent else {{}},
                trailingContent = {
                    AdaptiveSwitch(
                        checked = checked,
                        onCheckedChange = onCheckedChange
                    )
                },
                title
            )
        }
    )
}

