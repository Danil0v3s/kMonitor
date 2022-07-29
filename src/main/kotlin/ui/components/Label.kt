import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun Label(
    text: String
) = Text(
    text = text,
    style = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
)
