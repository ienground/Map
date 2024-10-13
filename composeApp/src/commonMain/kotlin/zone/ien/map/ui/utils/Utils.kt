package zone.ien.map.ui.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.sunnychung.lib.multiplatform.kdatetime.KDuration
import com.sunnychung.lib.multiplatform.kdatetime.KFixedTimeUnit
import com.sunnychung.lib.multiplatform.kdatetime.KZoneOffset
import com.sunnychung.lib.multiplatform.kdatetime.KZonedDateTime
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import io.github.alexzhirkevich.cupertino.adaptive.Theme
import io.github.alexzhirkevich.cupertino.adaptive.currentTheme
import zone.ien.map.utils.fromMillis
import zone.ien.map.utils.now
import zone.ien.map.utils.timeInMillis

const val ALARM_PATH = "files/ringtones/alarm"
const val DEFAULT_ALARM_SOUND = "morning_joy.mp3"

@Composable
fun dpToPx(size: Dp): Float = with (LocalDensity.current) { size.toPx() }
@Composable
fun pxToDp(size: Float): Dp = with (LocalDensity.current) { size.toDp() }
@Composable
fun pxToSp(size: Float): TextUnit = with (LocalDensity.current) { size.toSp() }

@Composable
expect fun SetBackHandler(onBack: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
val pastSelectableDate = object: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis >= KZonedDateTime.now().minus(KDuration.of(1, KFixedTimeUnit.Day)).timeInMillis()
    }
}

fun LazyListState.lastVisibleItemIndex(): Int = if (layoutInfo.visibleItemsInfo.isNotEmpty()) layoutInfo.visibleItemsInfo.last().index else -1
fun LazyListState.lastVisibleItemScrollOffset(): Int = if (layoutInfo.visibleItemsInfo.isNotEmpty()) layoutInfo.visibleItemsInfo.last().offset else -1

fun LazyListState.isFirstItemDisappear(): Boolean = (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0)
fun LazyListState.isLastItemDisappear(lastIndex: Int): Boolean = (lastVisibleItemIndex() == lastIndex && lastVisibleItemScrollOffset() == layoutInfo.viewportEndOffset - layoutInfo.visibleItemsInfo.first().size)

fun ContentDrawScope.drawFadedEdge(edgeWidth: Dp, leftEdge: Boolean) {
    val edgeWidthPx = edgeWidth.toPx()
    drawRect(
        topLeft = Offset(if (leftEdge) 0f else size.width - edgeWidthPx, 0f),
        size = Size(edgeWidthPx, size.height),
        brush = Brush.horizontalGradient(
            colors = listOf(Color.Transparent, Color.Black),
            startX = if (leftEdge) 0f else size.width,
            endX = if (leftEdge) edgeWidthPx else size.width - edgeWidthPx
        ),
        blendMode = BlendMode.DstIn
    )
}

/*
@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = (context as Activity?) ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

 */

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun LazyStaggeredGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

/*
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun previewDeviceSize(): WindowSizeClass {
    val dm = LocalContext.current.resources.displayMetrics
    val widthPixels = pxToDp(size = dm.widthPixels.toFloat())
    val heightPixels = pxToDp(size = dm.heightPixels.toFloat())

    return WindowSizeClass.calculateFromSize(DpSize(widthPixels, heightPixels))
}

 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMyDatePickerState(
    @Suppress("AutoBoxing") initialSelectedDateMillis: Long? = null,
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long? = initialSelectedDateMillis,
    yearRange: IntRange = DatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Picker,
    selectableDates: SelectableDates = DatePickerDefaults.AllDates
): DatePickerState {
    return rememberDatePickerState(
        initialDisplayedMonthMillis = initialDisplayedMonthMillis,
        yearRange = yearRange,
        initialDisplayMode = initialDisplayMode,
        selectableDates = selectableDates,
        initialSelectedDateMillis = initialSelectedDateMillis?.let { KZonedDateTime.fromMillis(it, KZoneOffset.UTC).timeInMillis() }
    )
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun AdaptiveDimen(
    material: Dp,
    cupertino: Dp
): Dp {
    return when (currentTheme) {
        Theme.Cupertino -> cupertino
        else -> material
    }
}