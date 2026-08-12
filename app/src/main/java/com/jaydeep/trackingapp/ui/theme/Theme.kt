// theme.kt
package com.jaydeep.trackingapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider

@Immutable
data class CategoryColors(
    val food: Color = Color.Unspecified,
    val travel: Color = Color.Unspecified,
    val shopping: Color = Color.Unspecified,
    val bills: Color = Color.Unspecified,
    val health: Color = Color.Unspecified,
    val entertainment: Color = Color.Unspecified,
    val education: Color = Color.Unspecified,
    val subscription: Color = Color.Unspecified,
    val income: Color = Color.Unspecified,
    val other: Color = Color.Unspecified,
    // Protein
    val chicken: Color = Color.Unspecified,
    val egg: Color = Color.Unspecified,
    val fish: Color = Color.Unspecified,
    val whey: Color = Color.Unspecified,
    val paneer: Color = Color.Unspecified,
    val milk: Color = Color.Unspecified,
    val dal: Color = Color.Unspecified,
    val soy: Color = Color.Unspecified,
    val nuts: Color = Color.Unspecified
)

@Immutable
data class TrackerColors(
    val protein: Color = Color.Unspecified,
    val expense: Color = Color.Unspecified,
    val success: Color = Color.Unspecified,
    val categories: CategoryColors = CategoryColors()
)

val LocalTrackerColors = staticCompositionLocalOf { TrackerColors() }

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreenDark,
    onPrimary = OnBrandGreen,
    primaryContainer = BrandGreenDark.copy(alpha = 0.2f),
    onPrimaryContainer = OnBrandGreen,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = OnSurfaceVariantDark,
    tertiary = SurfaceVariantDark,
    onTertiary = AccentForegroundDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
    outline = OutlineDark,
    outlineVariant = OutlineDark.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = OnBrandGreen,
    primaryContainer = BrandGreen.copy(alpha = 0.2f),
    onPrimaryContainer = OnBrandGreen,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = OnSurfaceVariantLight,
    tertiary = SurfaceVariantLight,
    onTertiary = AccentForegroundLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorLight,
    onError = Color.White,
    outline = OutlineLight,
    outlineVariant = OutlineLight.copy(alpha = 0.5f)
)

@Composable
fun TrackingAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val trackerColors = if (darkTheme) {
        TrackerColors(
            protein = ProteinColorDark, 
            expense = ExpenseColorDark,
            success = SuccessColorDark,
            categories = CategoryColors(
                food = CategoryFoodColorDark,
                travel = CategoryTravelColorDark,
                shopping = CategoryShoppingColorDark,
                bills = CategoryBillsColorDark,
                health = CategoryHealthColorDark,
                entertainment = CategoryEntertainmentColorDark,
                education = CategoryEducationColorDark,
                subscription = CategorySubscriptionColorDark,
                income = CategoryIncomeColorDark,
                other = CategoryOtherColorDark,
                chicken = CategoryChickenColorDark,
                egg = CategoryEggColorDark,
                fish = CategoryFishColorDark,
                whey = CategoryWheyColorDark,
                paneer = CategoryPaneerColorDark,
                milk = CategoryMilkColorDark,
                dal = CategoryDalColorDark,
                soy = CategorySoyColorDark,
                nuts = CategoryNutsColorDark
            )
        )
    } else {
        TrackerColors(
            protein = ProteinColor, 
            expense = ExpenseColor,
            success = SuccessColor,
            categories = CategoryColors(
                food = CategoryFoodColor,
                travel = CategoryTravelColor,
                shopping = CategoryShoppingColor,
                bills = CategoryBillsColor,
                health = CategoryHealthColor,
                entertainment = CategoryEntertainmentColor,
                education = CategoryEducationColor,
                subscription = CategorySubscriptionColor,
                income = CategoryIncomeColor,
                other = CategoryOtherColor,
                chicken = CategoryChickenColor,
                egg = CategoryEggColor,
                fish = CategoryFishColor,
                whey = CategoryWheyColor,
                paneer = CategoryPaneerColor,
                milk = CategoryMilkColor,
                dal = CategoryDalColor,
                soy = CategorySoyColor,
                nuts = CategoryNutsColor
            )
        )
    }

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
        typography = Typography
    ) {
        CompositionLocalProvider(LocalTrackerColors provides trackerColors) {
            content()
        }
    }
}