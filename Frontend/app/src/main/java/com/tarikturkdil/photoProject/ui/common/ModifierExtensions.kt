package com.tarikturkdil.photoProject.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.graphicsLayerScale(scale: Float): Modifier = this.graphicsLayer(
    scaleX = scale,
    scaleY = scale
)

fun Modifier.clickableWithoutRipple(
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
): Modifier = this.clickable(
    interactionSource = interactionSource,
    indication = null,
    onClick = onClick
)