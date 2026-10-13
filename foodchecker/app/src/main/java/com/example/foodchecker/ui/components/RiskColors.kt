package com.example.foodchecker.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.foodchecker.R
import com.example.foodchecker.domain.model.ComponentRisk
import com.example.foodchecker.domain.model.RiskLevel
import com.example.foodchecker.ui.theme.FoodCheckerTheme

@Composable
fun RiskLevel.color(): Color {
    val colors = FoodCheckerTheme.verdictColors
    return when (this) {
        RiskLevel.HIGH -> colors.dangerous
        RiskLevel.MEDIUM -> colors.unknown
        RiskLevel.LOW -> colors.safe
    }
}

@StringRes
fun RiskLevel.labelRes(): Int = when (this) {
    RiskLevel.HIGH -> R.string.risk_high
    RiskLevel.MEDIUM -> R.string.risk_medium
    RiskLevel.LOW -> R.string.risk_low
}

@Composable
fun ComponentRisk.highlightColor(): Color? {
    val colors = FoodCheckerTheme.verdictColors
    return when (this) {
        ComponentRisk.HIGH -> colors.dangerous
        ComponentRisk.MEDIUM -> colors.unknown
        ComponentRisk.LOW -> colors.safe
        ComponentRisk.NONE -> null
    }
}
