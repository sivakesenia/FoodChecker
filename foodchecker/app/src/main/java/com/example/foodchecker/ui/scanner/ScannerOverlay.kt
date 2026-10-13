package com.example.foodchecker.ui.scanner

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.foodchecker.ui.theme.FoodCheckerTheme
import kotlin.math.min

@Composable
fun ScannerOverlay(
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = FoodCheckerTheme.verdictColors.safe
    val transition = rememberInfiniteTransition(label = "scanLine")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLineProgress"
    )

    Canvas(
        modifier = modifier.graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    ) {
        val frameWidth = min(size.width * 0.8f, 320.dp.toPx())
        val frameHeight = frameWidth * 0.6f
        val topLeft = Offset((size.width - frameWidth) / 2f, (size.height - frameHeight) / 2f)
        val frameSize = Size(frameWidth, frameHeight)
        val cornerRadius = CornerRadius(20.dp.toPx())
        val inset = 16.dp.toPx()

        drawRect(color = Color.Black.copy(alpha = 0.55f))
        drawRoundRect(
            color = Color.Transparent,
            topLeft = topLeft,
            size = frameSize,
            cornerRadius = cornerRadius,
            blendMode = BlendMode.Clear
        )
        drawRoundRect(
            color = Color.White,
            topLeft = topLeft,
            size = frameSize,
            cornerRadius = cornerRadius,
            style = Stroke(width = 3.dp.toPx())
        )
        if (isScanning) {
            val y = topLeft.y + inset + (frameHeight - inset * 2) * progress
            drawLine(
                color = lineColor,
                start = Offset(topLeft.x + inset, y),
                end = Offset(topLeft.x + frameWidth - inset, y),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
