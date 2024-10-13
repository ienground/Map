package zone.ien.map.ui.utils.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoNavigateBackButton
import io.github.alexzhirkevich.cupertino.CupertinoNavigationTitle
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.adaptive.AdaptiveWidget
import io.github.alexzhirkevich.cupertino.adaptive.ExperimentalAdaptiveApi
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.back
import org.jetbrains.compose.resources.stringResource
import zone.ien.map.ui.utils.isFirstItemDisappear
import zone.ien.map.ui.utils.isLastItemDisappear

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun AdaptiveText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle? = null
) {
    AdaptiveWidget(
        material = {
            Text(text, modifier, color, fontSize, fontStyle, fontWeight, fontFamily, letterSpacing, textDecoration, textAlign, lineHeight, overflow, softWrap, maxLines, minLines, onTextLayout, style ?: androidx.compose.material3.LocalTextStyle.current)
        },
        cupertino = {
            CupertinoText(text, modifier, color, fontSize, fontStyle, fontWeight, fontFamily, letterSpacing, textDecoration, textAlign, lineHeight, overflow, softWrap, maxLines, minLines, onTextLayout, style ?: io.github.alexzhirkevich.LocalTextStyle.current)
        }
    )
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun AdaptiveIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    AdaptiveWidget(
        material = {
            Icon(imageVector = imageVector, contentDescription = contentDescription, modifier = modifier, tint = tint ?: androidx.compose.material3.LocalContentColor.current)
        },
        cupertino = {
            CupertinoIcon(imageVector = imageVector, contentDescription = contentDescription, modifier = modifier, tint = tint ?: io.github.alexzhirkevich.LocalContentColor.current)
        }
    )
}

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalCupertinoApi::class)
@Composable
fun AdaptiveBackButton(
    onClick: () -> Unit
) {
    AdaptiveWidget(
        material = {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(Res.string.back))
            }
        },
        cupertino = {
            CupertinoNavigateBackButton(
                onClick = onClick
            ) {
                CupertinoText(stringResource(Res.string.back))
            }
        }
    )
}

@Composable
fun <K, T> SingleChoiceItemList(
    map: Map<K, T>,
    select: K,
    setSelect: (K) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val keys = map.keys.toList()
    val values = map.values.toList()
//    val configuration = LocalConfiguration.current

    val firstItemDisappear by remember {
        derivedStateOf { scrollState.isFirstItemDisappear() }
    }
    val lastItemDisappear by remember {
        derivedStateOf { scrollState.isLastItemDisappear(keys.lastIndex) }
    }
    val firstDividerAlpha: Float by animateFloatAsState(targetValue = if (firstItemDisappear) 0f else 1f, label = "first_divider")
    val lastDividerAlpha: Float by animateFloatAsState(targetValue = if (lastItemDisappear) 0f else 1f, label = "last_divider")

    LaunchedEffect(Unit) {
        keys.indexOfFirst { it == select }.let { if (it != -1) scrollState.scrollToItem(it) }
    }

    HorizontalDivider(modifier = Modifier.alpha(firstDividerAlpha))
    LazyColumn(state = scrollState, modifier = Modifier
//        .width((configuration.screenWidthDp - 32).dp)
        .height(400.dp)) {
        itemsIndexed(items = values) { index, value ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    setSelect(keys[index])
                }) {
                RadioButton(selected = select == keys[index], onClick = {
                    setSelect(keys[index])
                }, modifier = Modifier.padding(horizontal = 8.dp))
//                Text(text = value.let { if (it == Pref.Default.DEFAULT_SOUND) stringResource(Res.string.default_ring) else it.toString() }, fontSize = 16.sp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
    HorizontalDivider(modifier = Modifier.alpha(lastDividerAlpha))
}

@OptIn(ExperimentalAdaptiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppbarTitle(
    modifier: Modifier = Modifier,
    material: String,
    cupertino: String
) {
    AdaptiveWidget(
        material = {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = modifier.fillMaxHeight()
            ) {
                AnimatedContent(
                    targetState = material,
                    label = "app_bar_title_material"
                ) {
                    AdaptiveText(
                        text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .wrapContentWidth()
                            .fillMaxWidth()
                            .basicMarquee(iterations = Int.MAX_VALUE)

                    )
                }
            }
        },
        cupertino = {
            CupertinoNavigationTitle(
                modifier = modifier
            ) {
                AdaptiveText(
                    text = cupertino,
                )
            }
        }
    )
}