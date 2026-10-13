package com.example.foodchecker.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.foodchecker.R
import com.example.foodchecker.domain.model.Allergen
import com.example.foodchecker.domain.model.ProductCheck
import com.example.foodchecker.domain.model.UserProfile
import com.example.foodchecker.ui.components.Avatar
import com.example.foodchecker.ui.components.VerdictBadge
import com.example.foodchecker.ui.components.title
import com.example.foodchecker.ui.theme.FoodCheckerTheme
import java.time.format.DateTimeFormatter

private val DateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileCard(
    profile: UserProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(initials = profile.initials)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name.ifBlank { stringResource(R.string.profile_no_name) },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (profile.criticalAllergens.isEmpty()) {
                    Text(
                        text = stringResource(R.string.profile_allergens_empty),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        profile.criticalAllergens.sorted().forEach { allergen ->
                            AllergenPill(allergen = allergen)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(R.string.profile_edit)
            )
        }
    }
}

@Composable
private fun AllergenPill(allergen: Allergen) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Text(
            text = allergen.title(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(R.string.search_placeholder),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.search_clear))
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun HistoryHeader(
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.history_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(R.string.history_count, count),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProductHistoryItem(
    check: ProductCheck,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = check.product
    val dangerousColor = FoodCheckerTheme.verdictColors.dangerous
    val issues = check.criticalAllergens.sorted().map { it.title() } + check.harmfulAdditives.map { it.marker }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(
                        R.string.product_subtitle,
                        product.brand,
                        product.scannedAt.format(DateFormatter)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AnimatedVisibility(
                    visible = issues.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = stringResource(
                            R.string.product_found_issues,
                            issues.joinToString(", ")
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = dangerousColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            VerdictBadge(verdict = check.verdict)
        }
    }
}

@Composable
fun HistoryEmptyState(
    query: String,
    hasHistory: Boolean,
    modifier: Modifier = Modifier
) {
    val isSearchResult = hasHistory && query.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSearchResult) Icons.Filled.SearchOff else Icons.Filled.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(
                if (isSearchResult) R.string.search_empty_title else R.string.history_empty_title
            ),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isSearchResult) {
                stringResource(R.string.search_empty_hint, query.trim())
            } else {
                stringResource(R.string.history_empty_hint)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
