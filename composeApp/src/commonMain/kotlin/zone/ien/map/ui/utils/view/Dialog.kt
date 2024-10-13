package zone.ien.map.ui.utils.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.github.alexzhirkevich.cupertino.AlertActionStyle
import org.jetbrains.compose.resources.stringResource
import co.touchlab.kermit.Logger
import io.github.alexzhirkevich.cupertino.CupertinoActionSheet
import io.github.alexzhirkevich.cupertino.CupertinoBottomSheetContent
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.CupertinoTimePicker
import io.github.alexzhirkevich.cupertino.CupertinoTopAppBar
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveWidget
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import io.github.alexzhirkevich.cupertino.cancel
import io.github.alexzhirkevich.cupertino.default
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.Checkmark
import io.github.alexzhirkevich.cupertino.rememberCupertinoTimePickerState
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import io.github.alexzhirkevich.cupertino.section.SectionItem
import io.github.alexzhirkevich.cupertino.section.SectionStyle
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
import kotlinx.coroutines.CoroutineScope
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.ask_to_save
import map.composeapp.generated.resources.ask_to_save_message
import map.composeapp.generated.resources.cancel
import map.composeapp.generated.resources.cannot_be_undone
import map.composeapp.generated.resources.delete_title
import map.composeapp.generated.resources.not_save
import map.composeapp.generated.resources.ok
import map.composeapp.generated.resources.save
import zone.ien.map.ui.utils.AutoSizeText

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalCupertinoApi::class
)
@Composable
fun TimePickerDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean = false,
    title: String,
    onCancel: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    AdaptiveWidget(
        material = {
            if (visible) {
                val timePickerState = rememberTimePickerState(
                    initialHour = initialHour,
                    initialMinute = initialMinute,
                    is24Hour = is24Hour,
                )
                var isTimePickerDial by remember { mutableStateOf(true) }

                Dialog(onDismissRequest = onCancel, properties = DialogProperties(usePlatformDefaultWidth = true)) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        tonalElevation = 6.dp,
                        modifier = modifier
                            .height(IntrinsicSize.Min)
                            .background(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.surface
                            ),
                    ) {
                        Column {
                            Text(text = title, modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 16.dp))
                            if (isTimePickerDial) {
                                TimePicker(state = timePickerState, modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp))
                            } else {
                                TimeInput(state = timePickerState, modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp))
                            }
                            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                                androidx.compose.material3.IconButton(onClick = {
                                    isTimePickerDial = !isTimePickerDial
                                }) {
                                    AnimatedContent(
                                        targetState = if (isTimePickerDial) Icons.Rounded.Keyboard else Icons.Rounded.AccessTime,
                                        label = "time_picker_dial"
                                    ) {
                                        Icon(imageVector = it, contentDescription = "", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = onCancel) {
                                    Text(text = stringResource(Res.string.cancel))
                                }
                                TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                                    Text(text = stringResource(Res.string.ok))
                                }
                            }
                        }
                    }
                }
            }
        },
        cupertino = {
            val timePickerState = if (visible) {
                rememberCupertinoTimePickerState(
                    initialHour = initialHour,
                    initialMinute = initialMinute,
                    is24Hour = is24Hour
                )
            } else null

            CupertinoActionSheet(
                visible = visible,
                title = { CupertinoText(text = title) },
                content = {
                    timePickerState?.let { state ->
                        CupertinoTimePicker(
                            state = state,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                buttons = {
                    default(
                        title = { CupertinoText(text = stringResource(Res.string.ok)) },
                        onClick = {
                            timePickerState?.let { state ->
                                onConfirm(state.hour, state.minute)
                            }
                        }
                    )
                    cancel(
                        title = { CupertinoText(text = stringResource(Res.string.cancel)) },
                        onClick = onCancel
                    )
                },
                onDismissRequest = onCancel
            )
        }
    )
}

@Composable
fun BaseDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    title: String?,
    content: @Composable (modifier: Modifier) -> Unit,
    onCancel: () -> Unit,
    buttons: @Composable (modifier: Modifier) -> Unit
) {
    Dialog(onDismissRequest = onCancel, properties = DialogProperties(usePlatformDefaultWidth = true)) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = modifier
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                icon?.let {
                    Icon(
                        imageVector = it, contentDescription = title,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                title?.let {
                    AutoSizeText(
                        text = it,
                        style = MaterialTheme.typography.displayLarge,
                        maxTextSize = 24.sp,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth()
                            .height(24.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    )
                }
                content(Modifier.padding(bottom = 16.dp))
                buttons(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp)
                        .height(40.dp)
                        .padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalCupertinoApi::class)
@Composable
fun AlertDialog(
    visible: Boolean,
    icon: ImageVector?,
    title: String?,
    message: String? = null,
    content: @Composable (modifier: Modifier) -> Unit = {},
    roleCancel: AlertActionStyle = AlertActionStyle.Cancel,
    onCancel: () -> Unit,
    roleConfirm: AlertActionStyle = AlertActionStyle.Default,
    onConfirm: (() -> Unit)?
) {
    AdaptiveWidget(
        material = {
            if (visible) {
                BaseDialog(
                    icon = icon,
                    title = title,
                    content = {
                        message?.let { str ->
                            Text(
                                text = str,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .fillMaxWidth()
                            )
                        }
                        content(it)
                    },
                    onCancel = onCancel,
                    buttons = {
                        Row(modifier = it) {
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = onCancel) { Text(stringResource(Res.string.cancel)) }
                            if (onConfirm != null) TextButton(onClick = onConfirm) { Text(stringResource(Res.string.ok)) }
                        }
                    }
                )
            }
        },
        cupertino = {
            CupertinoActionSheet(
                visible = visible,
                title = { title?.let { CupertinoText(text = it) } },
                message = { message?.let { CupertinoText(text = it) } },
                content = { content(Modifier) },
                buttons = {
                    action(
                        onClick = onCancel,
                        style = roleCancel,
                        title = { CupertinoText(text = stringResource(Res.string.cancel)) }
                    )
                    onConfirm?.let {
                        action(
                            onClick = it,
                            style = roleConfirm,
                            title = { CupertinoText(text = stringResource(Res.string.ok)) }
                        )
                    }
                },
                onDismissRequest = onCancel
            )
        }
    )
}

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalCupertinoApi::class)
@Composable
fun AlertDialog(
    visible: Boolean,
    icon: ImageVector?,
    title: String?,
    message: String?,
    content: @Composable (Modifier) -> Unit = {},
    textRoleNeutral: Pair<String, AlertActionStyle>,
    onNeutral: (() -> Unit)?,
    textRoleNegative: Pair<String, AlertActionStyle>,
    onNegative: () -> Unit,
    textRolePositive: Pair<String, AlertActionStyle>,
    onPositive: (() -> Unit)?
) {
    AdaptiveWidget(
        material = {
            if (visible) {
                BaseDialog(
                    icon = icon,
                    title = title,
                    content = {
                        message?.let { str ->
                            Text(
                                text = str,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .fillMaxWidth()
                            )
                        }
                        content(it)
                    },
                    onCancel = onNegative,
                    buttons = {
                        Row(modifier = it) {
                            if (onNeutral != null) TextButton(onClick = onNeutral) { Text(textRoleNeutral.first) }
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = onNegative) { Text(textRoleNegative.first) }
                            if (onPositive != null) TextButton(onClick = onPositive) { Text(textRolePositive.first) }
                        }
                    }
                )
            }
        },
        cupertino = {
            CupertinoActionSheet(
                visible = visible,
                title = { title?.let { CupertinoText(text = it) } },
                message = { message?.let { CupertinoText(text = it) } },
                content = { content(Modifier) },
                buttons = {
                    action(
                        onClick = onNegative,
                        style = textRoleNegative.second,
                        title = { CupertinoText(text = textRoleNegative.first) }
                    )
                    onPositive?.let {
                        action(
                            onClick = it,
                            style = textRolePositive.second,
                            title = { CupertinoText(text = textRolePositive.first) }
                        )
                    }
                    onNeutral?.let {
                        action(
                            onClick = it,
                            style = textRoleNeutral.second,
                            title = { CupertinoText(text = textRoleNeutral.first) }
                        )
                    }
                },
                onDismissRequest = onNegative
            )
        }
    )
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun DeleteAlertDialog(
    visible: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable (modifier: Modifier) -> Unit = {}
) {
    AdaptiveWidget(
        material = {
            AlertDialog(
                visible = visible,
                icon = AdaptiveIcons.get(AdaptiveIcons.Delete),
                title = stringResource(Res.string.delete_title),
                message = stringResource(Res.string.cannot_be_undone),
                content = content,
                onCancel = onCancel,
                onConfirm = onConfirm
            )
        },
        cupertino = {
            AlertDialog(
                visible = visible,
                icon = AdaptiveIcons.get(AdaptiveIcons.Delete),
                title = stringResource(Res.string.delete_title),
                message = stringResource(Res.string.cannot_be_undone),
                content = content,
                onCancel = onCancel,
                roleConfirm = AlertActionStyle.Destructive,
                onConfirm = onConfirm
            )
        }
    )
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun SaveAlertDialog(
    visible: Boolean,
    onCancel: () -> Unit,
    onUnsave: () -> Unit,
    onSave: () -> Unit,
    content: @Composable (modifier: Modifier) -> Unit = {}
) {
    AdaptiveWidget(
        material = {
            AlertDialog(
                visible = visible,
                icon = AdaptiveIcons.get(AdaptiveIcons.Save),
                title = stringResource(Res.string.ask_to_save),
                message = stringResource(Res.string.ask_to_save_message),
                content = content,
                textRoleNegative = Pair(stringResource(Res.string.cancel), AlertActionStyle.Cancel),
                onNegative = onCancel,
                textRoleNeutral = Pair(stringResource(Res.string.not_save), AlertActionStyle.Destructive),
                onNeutral = onUnsave,
                textRolePositive = Pair(stringResource(Res.string.save), AlertActionStyle.Default),
                onPositive = onSave
            )
        },
        cupertino = {
            AlertDialog(
                visible = visible,
                icon = AdaptiveIcons.get(AdaptiveIcons.Save),
                title = stringResource(Res.string.ask_to_save),
                message = stringResource(Res.string.ask_to_save_message),
                content = content,
                textRoleNegative = Pair(stringResource(Res.string.cancel), AlertActionStyle.Cancel),
                onNegative = onCancel,
                textRoleNeutral = Pair(stringResource(Res.string.not_save), AlertActionStyle.Destructive),
                onNeutral = onUnsave,
                textRolePositive = Pair(stringResource(Res.string.save), AlertActionStyle.Default),
                onPositive = onSave
            )
        }
    )
}

@Composable
fun <K: Any, T> SingleSelectDialog(
    visible: Boolean,
    icon: ImageVector,
    title: String,
    initialValue: K,
    content: Map<K, T>,
    onSelect: CoroutineScope.(K) -> Unit,
    onResume: () -> Unit,
    onPause: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: (K) -> Unit
) {
    val currentSelection = if (visible) { remember { mutableStateOf(initialValue) } } else null
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(currentSelection?.value) {
        if (currentSelection?.value != null) {
            onSelect(currentSelection.value)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> onResume()
                Lifecycle.Event.ON_PAUSE -> onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AlertDialog(
        visible = visible,
        icon = icon,
        title = title,
        content = {
            SingleChoiceItemList(
                map = content,
                select = currentSelection?.value ?: initialValue,
                setSelect = {
                    currentSelection?.value = it
                }
            )
        },
        onCancel = onCancel,
        onConfirm = {
            onConfirm(currentSelection?.value ?: initialValue)
        }
    )
}

/*
@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun <K: Any, T> SingleSelectBottomSheet(
    visible: Boolean,
    modifier: Modifier = Modifier,
    title: String,
    initialValue: K,
    content: Map<K, T>,
    onSelect: (K) -> Unit,
    onResume: () -> Unit,
    onPause: () -> Unit,
    onCancel: () -> Unit
) {
    if (visible) {
        var currentSelection by remember { mutableStateOf(initialValue) }
        val lifecycleOwner = LocalLifecycleOwner.current

        LaunchedEffect(currentSelection) {
            onSelect(currentSelection)
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> onResume()
                    Lifecycle.Event.ON_PAUSE -> onPause()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        CupertinoBottomSheetContent(
            topBar = {
                CupertinoTopAppBar(
                    navigationIcon = {
                        AdaptiveBackButton(onClick = onCancel)
                    },
                    title = { CupertinoText(title) }
                )
            },
            modifier = modifier
        ) {
            AdaptiveProvideSectionStyle(
                style = SectionStyle.InsetGrouped,
                modifier = Modifier.padding(it)
            ) {
                Column(
                    modifier = it.fillMaxHeight()
                ) {
                    CupertinoSection {
                        LazyColumn(
                            modifier = Modifier
                        ) {
                            items(items = content.toList(), key = { it.first }) { item ->
                                SectionItem(
                                    leadingContent = {
                                        if (currentSelection == item.first) {
                                            CupertinoIcon(
                                                imageVector = CupertinoIcons.Default.Checkmark, contentDescription = null, tint = CupertinoTheme.colorScheme.link,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        } else {
                                            Box(modifier = Modifier.size(14.dp))
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        currentSelection = item.first
                                    }
                                ) {
                                    CupertinoText(text = item.second.let { if (it == Pref.Default.DEFAULT_SOUND) stringResource(Res.string.default_ring) else it.toString() })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
expect fun RingtoneSelectDialog(
    visible: Boolean,
    title: String,
    ringtones: Map<String, String>,
    initialValue: String,
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit
)

@Composable
expect fun RingtoneSelectBottomSheetContent(
    modifier: Modifier = Modifier,
    visible: Boolean,
    title: String,
    ringtones: Map<String, String>,
    initialValue: String,
    onSelect: (String) -> Unit,
    onCancel: () -> Unit
)

 */