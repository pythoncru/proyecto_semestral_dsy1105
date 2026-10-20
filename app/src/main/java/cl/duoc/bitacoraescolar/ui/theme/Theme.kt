package cl.duoc.bitacoraescolar.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// Paleta azul y verde original, con superficies neutras y contraste mejorado.
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8DC4FF), onPrimary = Color(0xFF002F55),
    primaryContainer = Color(0xFF16496F), onPrimaryContainer = Color(0xFFD9EBFF),
    secondary = Color(0xFF8CD4AA), onSecondary = Color(0xFF073A22),
    secondaryContainer = Color(0xFF20533B), onSecondaryContainer = Color(0xFFD0F4DF),
    background = Color(0xFF101820), onBackground = Color(0xFFE7EEF5),
    surface = Color(0xFF17232E), onSurface = Color(0xFFE7EEF5),
    surfaceVariant = Color(0xFF283744), onSurfaceVariant = Color(0xFFBFCDD9),
    outline = Color(0xFF8193A2), error = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF287DCE), onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEEFF), onPrimaryContainer = Color(0xFF113D62),
    secondary = Color(0xFF368D61), onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDF3E5), onSecondaryContainer = Color(0xFF174C30),
    background = Color(0xFFF6F9FC), onBackground = Color(0xFF1A2C3A),
    surface = Color.White, onSurface = Color(0xFF1A2C3A),
    surfaceVariant = Color(0xFFEBF1F5), onSurfaceVariant = Color(0xFF4D6271),
    outline = Color(0xFF728695), error = Color(0xFFBA1A1A)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp), small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp), large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun BitacoraEscolarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}