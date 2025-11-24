package com.example.binauralbeats.ui.theme

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AnimateBackground(){
    val infiniteTransition = rememberInfiniteTransition()

    val color1 = infiniteTransition.animateColor(
        initialValue = Color(0xFF6DA5C0),
        targetValue = Color(0xFF05161A),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val color2 = infiniteTransition.animateColor(
        initialValue = Color(0xFF072E33),
        targetValue = Color(0xFF0C7075),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color3 = infiniteTransition.animateColor(
        initialValue = Color(0xFF294D61),
        targetValue = Color(0xFF0F969C),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(modifier = Modifier.fillMaxSize()
        .background(brush = Brush.verticalGradient(
            colors = listOf(color1.value, color2.value, color3.value),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )))
}