package zone.ien.map.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import map.composeapp.generated.resources.Res
import map.composeapp.generated.resources.pretendard_bold
import map.composeapp.generated.resources.pretendard_regular

@Composable
fun getPretendard(): FontFamily {
    val Pretendard = FontFamily(
        Font(Res.font.pretendard_regular, FontWeight.Normal),
        Font(Res.font.pretendard_bold, FontWeight.Bold)
    )

    return Pretendard
}