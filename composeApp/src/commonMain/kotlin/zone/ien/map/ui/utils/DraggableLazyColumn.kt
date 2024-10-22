package zone.ien.map.ui.utils

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.Job
import zone.ien.map.TAG
import zone.ien.map.utils.Dlog

fun <T> MutableList<T>.move(from: Int, to: Int): Boolean {
    if (from !in 0 until size || to !in 0 until size) return false
    if (from == to) return false
    val element = removeAt(from) ?: return false
    add(to, element)
    return true
}

class DragDropListState(
    val state: LazyListState,
    private val onMove: (Int, Int) -> Boolean
) {
    // 드래그된 거리
    private var draggedDistance by mutableFloatStateOf(0f)
    // 드래그 시작한 아이템 인포
    private var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
    // 드래그 시작한 아이템의 상, 하단 오프셋
    private val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let { Pair(it.offset, it.offset + it.size) }
    // 드래그 중인 아이템의 현재 위치
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
    // 드래그 중인 아이템을 이동시킬 거리
    val elementDisplacement
        get() = currentIndexOfDraggedItem?.let {
            state.layoutInfo.visibleItemsInfo.getOrNull(it - state.layoutInfo.visibleItemsInfo.first().index)
        }?.let { item ->
            (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
        }
    // 드래그 중인 아이템 인포
    private val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            state.layoutInfo.visibleItemsInfo.getOrNull(it - state.layoutInfo.visibleItemsInfo.first().index)
        }

    // 오버스크롤 job
    private var overscrollJob by mutableStateOf<Job?>(null)

    fun onDragStart(index: Int) {
        state.layoutInfo.visibleItemsInfo[index].let { item ->
            initiallyDraggedElement = item
            currentIndexOfDraggedItem = index
        }
    }

    fun onDragEnd() {
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overscrollJob?.cancel()
    }

    fun onDrag(offset: Offset) {
        draggedDistance += offset.y
        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance // 드래그 시작한 아이템의 상단 오프셋 + 드래그 거리
            val endOffset = bottomOffset + draggedDistance // 드래그 시작한 아이템의 하단 오프셋 + 드래그 거리

            currentElement?.let { selected ->
                state.layoutInfo.visibleItemsInfo.filterNot { item ->
                    item.offset + item.size <= startOffset || item.offset >= endOffset || selected.index == item.index
                }.firstOrNull { item ->
                    when {
                        startOffset > selected.offset -> (endOffset > item.offset + item.size / 2)
                        else -> (startOffset < item.offset + item.size / 2)
                    }
                }?.also { item ->
                    currentIndexOfDraggedItem?.let { current ->
                        onMove(current, item.index)
                    }
                    currentIndexOfDraggedItem = item.index
//                    currentIndexOfDraggedItem?.let { current ->
//                        val result = onMove(current, item.index)
//                        if (result) draggedDistance = 0f
//                    }
//                    currentIndexOfDraggedItem = item.index
                }
            }
        }

    }

    // 오버스크롤 체크
    fun checkForOverScroll(): Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offset + it.size + draggedDistance

            return@let when {
                draggedDistance > 0 -> (endOffset - state.layoutInfo.viewportEndOffset).takeIf { diff -> diff > 0 }
                draggedDistance < 0 -> (startOffset - state.layoutInfo.viewportStartOffset).takeIf { diff -> diff < 0 }
                else -> null
            }
        } ?: 0f
    }


}

/*
class DragDropListState(
    val state: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    // 드래그된 거리
    private var draggedDistance by mutableFloatStateOf(0f)
    // 드래그 시작한 아이템 인포
    private var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
    // 드래그 시작한 아이템의 상하단 오프셋
    private val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let { Pair(it.offset, it.offset + it.size) }
    // 드래그 중인 아이템의 현재 위치 (인덱스)
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
    // 드래그 중인 아이템을 이동시킬 거리
    val elementDisplacement
        get() = currentIndexOfDraggedItem?.let {
            state.layoutInfo.visibleItemsInfo.getOrNull(it - state.layoutInfo.visibleItemsInfo.first().index)
        }?.let { item ->
            (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
        }
    // 드래그 중인 아이템 인포
    private val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            state.layoutInfo.visibleItemsInfo.getOrNull(it - state.layoutInfo.visibleItemsInfo.first().index)
        }
    // 오버스크롤 job
    private var overscrollJob by mutableStateOf<Job?>(null)
    // 드래그 시작
    fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo.firstOrNull { item ->
            offset.y.toInt() in item.offset..(item.offset + item.size)
        }?.also {
            currentIndexOfDraggedItem = it.index
            initiallyDraggedElement = it
        }
    }
    // 드래그 종료
    fun onDragEnd() {
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overscrollJob?.cancel()
    }
    // 드래그 중
    fun onDrag(offset: Offset) {
        draggedDistance += offset.y

        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance // 드래그 시작한 아이템의 상단 오프셋 + 드래그 거리
            val endOffset = bottomOffset + draggedDistance // 드래그 시작한 아이템의 하단 오프셋 + 드래그 거리

            currentElement?.let { hovered -> // 드래그 중인 아이템
                state.layoutInfo.visibleItemsInfo.filterNot { item -> // 드래그 중인 아이템과 겹치지 않는 아이템 필터링
                    item.offset + item.size <= startOffset || item.offset >= endOffset || hovered.index == item.index
                }.firstOrNull { item -> // 드래그 중인 아이템의 현재 위치에 있는 아이템 찾기
                    when { // 임계점을 넘었는지 판단
                        startOffset > hovered.offset -> (endOffset > item.offset + item.size / 2) // 드래그 중인 아이템이 위로 이동할 때
                        else -> (startOffset < item.offset + item.size / 2) // 드래그 중인 아이템이 아래로 이동할 때
                    }
                }?.also { item ->
                    currentIndexOfDraggedItem?.let { current -> // 드래그 중인 아이템의 현재 위치와 이동할 위치
                        onMove.invoke(current, item.index)
                    }
                    currentIndexOfDraggedItem = item.index
                }
            }
        }
    }
    // 오버스크롤 체크
    fun checkForOverScroll(): Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offset + it.size + draggedDistance

            return@let when {
                draggedDistance > 0 -> (endOffset - state.layoutInfo.viewportEndOffset).takeIf { diff -> diff > 0 }
                draggedDistance < 0 -> (startOffset - state.layoutInfo.viewportStartOffset).takeIf { diff -> diff < 0 }
                else -> null
            }
        } ?: 0f
    }
}

 */

@Composable
fun rememberDragDropListState(
    lazyListState: LazyListState = rememberLazyListState(),
    onMove: (Int, Int) -> Boolean,
): DragDropListState {
    return remember {
        DragDropListState(
            state = lazyListState,
            onMove = onMove
        )
    }
}