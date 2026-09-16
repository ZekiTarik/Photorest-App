package com.tarikturkdil.photoProject.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val colorScheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        colorScheme.surfaceVariant.copy(alpha = 0.6f),
        colorScheme.surfaceVariant.copy(alpha = 0.2f),
        colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    return this.drawWithContent {
        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim, translateAnim),
            end = Offset(translateAnim + 300f, translateAnim + 300f)
        )
        drawRect(brush = brush, size = size)
        drawContent()
    }
}