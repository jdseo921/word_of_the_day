package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = FormalPrimaryDark,
    onPrimary = FormalOnPrimaryDark,
    primaryContainer = FormalPrimaryContainerDark,
    onPrimaryContainer = FormalOnPrimaryContainerDark,
    secondary = FormalSecondaryDark,
    tertiary = FormalTertiaryDark,
    background = FormalBackgroundDark,
    surface = FormalSurfaceDark,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = FormalPrimary,
    onPrimary = FormalOnPrimary,
    primaryContainer = FormalPrimaryContainer,
    onPrimaryContainer = FormalOnPrimaryContainer,
    secondary = FormalSecondary,
    tertiary = FormalTertiary,
    background = FormalBackground,
    surface = FormalSurface,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val ColorblindLightColorScheme = lightColorScheme(
    primary = CBPrimary,
    secondary = CBSecondary,
    tertiary = CBTertiary,
    background = CBBackground,
    surface = CBBackground,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

private val ColorblindDarkColorScheme = darkColorScheme(
    primary = CBPrimary,
    secondary = CBSecondary,
    tertiary = CBTertiary,
    background = CBBackgroundDark,
    surface = CBBackgroundDark,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

@Composable
fun CP3406_CP5307UtilityAppStarterTemplateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isColorblindMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isColorblindMode && darkTheme -> ColorblindDarkColorScheme
        isColorblindMode -> ColorblindLightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context.findActivity())?.window ?: return@SideEffect
            // window.statusBarColor = colorScheme.primary.toArgb() // Let EdgeToEdge handle this
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
