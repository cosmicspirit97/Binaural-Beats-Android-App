package com.example.binauralbeats.color



import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color



@Composable
public fun gradientBackgroundBrush(
    isVerticalGradient: Boolean,
    colors: List<Color>

): Brush {
    val endOffset = if (isVerticalGradient) {
        Offset(0f, Float.POSITIVE_INFINITY)
    } else {
        Offset(Float.POSITIVE_INFINITY, 0f)
    }

    return Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = endOffset
    )
}

val gradientColors = listOf(
    Color(0xFFF1F7F6),
    Color(0xFF00DF81),
    Color(0xFF00DF81),
    Color(0xFF2CC295),
    Color(0xFF03624C),
    Color(0xFF032221),
)
