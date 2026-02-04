package com.example.todoapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = LightSteelBlue,
    onPrimary = DarkBlue,
    primaryContainer = SteelBlue,
    onPrimaryContainer = White,
    secondary = LightSteelBlue,
    onSecondary = DarkBlue,
    secondaryContainer = NavyBlue,
    onSecondaryContainer = White,
    tertiary = White,
    background = DarkBlue,
    surface = NavyBlue,
    onBackground = White,
    onSurface = White,
    onSurfaceVariant = LightSteelBlue,
)

@Composable
fun ToDoAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}