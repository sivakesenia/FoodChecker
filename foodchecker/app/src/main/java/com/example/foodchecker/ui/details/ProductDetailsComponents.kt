package com.example.foodchecker.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.foodchecker.R
import com.example.foodchecker.domain.model.Additive
import com.example.foodchecker.domain.model.AllergenFinding
import com.example.foodchecker.domain.model.ComponentRisk
import com.example.foodchecker.domain.model.IngredientAnalysis
import com.example.foodchecker.domain.model.ScannedProduct
import com.example.foodchecker.domain.model.Verdict
import com.example.foodchecker.ui.components.color
import com.example.foodchecker.ui.components.highlightColor
import com.example.foodchecker.ui.components.icon
import com.example.foodchecker.ui.components.labelRes
import com.example.foodchecker.ui.components.title
import com.example.foodchecker.ui.components.titles
import com.example.foodchecker.ui.theme.FoodCheckerTheme
import java.time.format.DateTimeFormatter

private val DateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@Composable
fun ProductHeader(
    product: ScannedProduct,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = product.brand,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.QrCode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = product.barcode,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.details_scanned_at, product.scannedAt.format(DateFormatter)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun VerdictBanner(
    analysis: IngredientAnalysis,
    modifier: Modifier = Modifier
) {
    val verdict = analysis.verdict
    val color = verdict.color()
    val reasons = buildList {
        when (verdict) {
            Verdict.UNKNOWN -> add(stringResource(R.string.verdict_reason_unknown))
            Verdict.SAFE -> add(stringResource(R.string.verdict_reason_safe))
            Verdict.DANGEROUS -> {
                if (analysis.criticalAllergens.isNotEmpty()) {
                    add(stringResource(R.string.verdict_reason_allergens, analysis.criticalAllergens.titles()))
                }
                if (analysis.harmfulAdditives.isNotEmpty()) {
                    add(
                        stringResource(
                            R.string.verdict_reason_additives,
                            analysis.harmfulAdditives.joinToString(", ") { it.marker }
                        )
                    )
                }
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = color.copy(alpha = 0.14f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = verdict.icon(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(verdict.labelRes()),
                    style = MaterialTheme.typography.titleLarge,
                    color = color
                )
                reasons.forEach { reason ->
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompositionSection(
    analysis: IngredientAnalysis,
    modifier: Modifier = Modifier
) {
    SectionCard(title = stringResource(R.string.composition_title), modifier = modifier) {
        if (!analysis.hasIngredients) {
            EmptySectionText(text = stringResource(R.string.composition_empty))
        } else {
            val highHighlight = ComponentRisk.HIGH.highlightColor()
            val mediumHighlight = ComponentRisk.MEDIUM.highlightColor()
            val lowHighlight = ComponentRisk.LOW.highlightColor()
            val annotated = buildAnnotatedString {
                analysis.tokens.forEach { token ->
                    val highlight = when (token.risk) {
                        ComponentRisk.HIGH -> highHighlight
                        ComponentRisk.MEDIUM -> mediumHighlight
                        ComponentRisk.LOW -> lowHighlight
                        ComponentRisk.NONE -> null
                    }
                    if (token.isSeparator || highlight == null) {
                        append(token.text)
                    } else {
                        withStyle(
                            SpanStyle(
                                color = highlight,
                                fontWeight = FontWeight.SemiBold,
                                background = highlight.copy(alpha = 0.12f)
                            )
                        ) {
                            append(token.text)
                        }
                    }
                }
            }
            Text(
                text = annotated,
                style = MaterialTheme.typography.bodyLarge
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (highHighlight != null) LegendItem(color = highHighlight, label = stringResource(R.string.legend_high))
                if (mediumHighlight != null) LegendItem(color = mediumHighlight, label = stringResource(R.string.legend_medium))
                if (lowHighlight != null) LegendItem(color = lowHighlight, label = stringResource(R.string.legend_low))
            }
        }
    }
}

@Composable
fun AllergensSection(
    findings: List<AllergenFinding>,
    modifier: Modifier = Modifier
) {
    SectionCard(title = stringResource(R.string.allergens_title), modifier = modifier) {
        if (findings.isEmpty()) {
            EmptySectionText(text = stringResource(R.string.allergens_none))
        } else {
            val colors = FoodCheckerTheme.verdictColors
            findings.forEachIndexed { index, finding ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                val color = if (finding.isCritical) colors.dangerous else colors.unknown
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(10.dp)
                    ) {
                        Surface(shape = CircleShape, color = color, modifier = Modifier.size(10.dp)) {}
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = finding.marker.allergen.title(),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (finding.isCritical) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Tag(text = stringResource(R.string.allergen_critical), color = color)
                            }
                        }
                        Text(
                            text = finding.marker.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdditivesSection(
    additives: List<Additive>,
    unknownCodes: List<String>,
    modifier: Modifier = Modifier
) {
    SectionCard(title = stringResource(R.string.additives_title), modifier = modifier) {
        if (additives.isEmpty() && unknownCodes.isEmpty()) {
            EmptySectionText(text = stringResource(R.string.additives_none))
        } else {
            additives.forEachIndexed { index, additive ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                AdditiveRow(
                    code = additive.marker,
                    name = additive.name,
                    description = additive.description,
                    color = additive.risk.color(),
                    riskLabel = stringResource(additive.risk.labelRes())
                )
            }
            unknownCodes.forEachIndexed { index, code ->
                if (additives.isNotEmpty() || index > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                AdditiveRow(
                    code = code,
                    name = null,
                    description = stringResource(R.string.additive_unknown),
                    color = FoodCheckerTheme.verdictColors.unknown,
                    riskLabel = null
                )
            }
        }
    }
}

@Composable
private fun AdditiveRow(
    code: String,
    name: String?,
    description: String,
    color: Color,
    riskLabel: String?
) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.widthIn(min = 64.dp)
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (name != null) {
                Text(text = name, style = MaterialTheme.typography.titleSmall)
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (riskLabel != null) {
                Tag(text = riskLabel, color = color)
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun EmptySectionText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = color, modifier = Modifier.size(10.dp)) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Tag(text: String, color: Color) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
