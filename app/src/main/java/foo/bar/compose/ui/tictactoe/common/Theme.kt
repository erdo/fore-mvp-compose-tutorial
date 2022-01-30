package foo.bar.compose.ui.tictactoe.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

private val LightColors = lightColors(
    primary = Color(0xFFF9AA3C),
    primaryVariant = Color(0xFFF28330),
    secondary = Color(0xFFEE5330),
    background = Color(0xFFFFFDE9),
    surface = Color(0xFFFFF4A3),
    error = Color(0xFFCC0000),
    onPrimary = Color(0xFF3C4AF9),
    onSecondary = Color(0xFF2D34E0),
    onBackground = Color(0xFFF28330),
    onSurface = Color(0xFF5D69FD),
    onError = Color(0xFF262626),
)

private val DarkColors = darkColors(
    primary = Color(0xFFDA481A),
    primaryVariant = Color(0xFFDF6523),
    secondary = Color(0xFFB8174E),
    background = Color(0xFF181818),
    surface = Color(0xFF252525),
    error = Color(0xFF960000),
    onPrimary = Color(0xFFCECECE),
    onSecondary = Color(0xFFBEBEBE),
    onBackground = Color(0xFF727272),
    onSurface = Color(0xFFFF9800),
    onError = Color(0xFF050505),
)

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

val Typography = Typography(
    defaultFontFamily = FontFamily.Default
)

val colorPastels = listOf(
    Color(0xFFBAD851),
    Color(0xFFECE926),
    Color(0xFFE4C625),
    Color(0xFF83C55F),
    Color(0xFF8CD0CC),
    Color(0xFF6EABD5),
    Color(0xFF7C7ABA),
    Color(0xFF9E7CB7),
    Color(0xFFBA6AA8),
)
