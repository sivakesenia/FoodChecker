package com.example.foodchecker.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.foodchecker.R
import com.example.foodchecker.domain.model.Verdict
import com.example.foodchecker.ui.theme.FoodCheckerTheme

@Composable
fun VerdictBadge(
    verdict: Verdict,
    modifier: Modifier = Modifier
) {
    val targetColor = verdict.color()
    val color by animateColorAsState(targetValue = targetColor, label = "verdictColor")

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = color.copy(alpha = 0.15f)
    ) {
        AnimatedContent(
            targetState = verdict,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "verdictContent"
        ) { value ->
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = value.icon(),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(value.labelRes()),
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
        }
    }
}

@Composable
fun Verdict.color(): Color {
    val colors = FoodCheckerTheme.verdictColors
    return when (this) {
        Verdict.SAFE -> colors.safe
        Verdict.DANGEROUS -> colors.dangerous
        Verdict.UNKNOWN -> colors.unknown
    }
}

fun Verdict.icon(): ImageVector = when (this) {
    Verdict.SAFE -> Icons.Filled.CheckCircle
    Verdict.DANGEROUS -> Icons.Filled.Warning
    Verdict.UNKNOWN -> Icons.Filled.Info
}

fun Verdict.labelRes(): Int = when (this) {
    Verdict.SAFE -> R.string.verdict_safe
    Verdict.DANGEROUS -> R.string.verdict_dangerous
    Verdict.UNKNOWN -> R.string.verdict_unknown
}
