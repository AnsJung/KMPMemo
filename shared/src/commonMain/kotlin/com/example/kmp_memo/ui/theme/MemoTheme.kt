package com.example.kmp_memo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MemoLightColorScheme = lightColorScheme(
    primary = MemoPrimary,
    onPrimary = MemoOnPrimary,
    primaryContainer = MemoPrimaryContainer,
    onPrimaryContainer = MemoOnPrimaryContainer,
    secondary = MemoSecondary,
    onSecondary = MemoOnSecondary,
    secondaryContainer = MemoSecondaryContainer,
    onSecondaryContainer = MemoOnSecondaryContainer,
    tertiary = MemoTertiary,
    onTertiary = MemoOnTertiary,
    tertiaryContainer = MemoTertiaryContainer,
    onTertiaryContainer = MemoOnTertiaryContainer,
    background = MemoBackground,
    onBackground = MemoOnBackground,
    surface = MemoSurface,
    onSurface = MemoOnSurface,
    surfaceVariant = MemoSurfaceVariant,
    onSurfaceVariant = MemoOnSurfaceVariant,
    outline = MemoOutline,
    outlineVariant = MemoOutlineVariant,
    error = MemoError,
    onError = MemoOnError,
    errorContainer = MemoErrorContainer,
    onErrorContainer = MemoOnErrorContainer,
    scrim = MemoScrim,
)

/** Pocket Memo의 공통 디자인 토큰을 모든 화면에 제공한다. */
@Composable
fun MemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MemoLightColorScheme,
        typography = MemoTypography,
        content = content,
    )
}
