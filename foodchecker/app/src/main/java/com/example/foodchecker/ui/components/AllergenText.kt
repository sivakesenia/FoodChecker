package com.example.foodchecker.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.foodchecker.R
import com.example.foodchecker.domain.model.Allergen

@StringRes
fun Allergen.titleRes(): Int = when (this) {
    Allergen.PEANUT -> R.string.allergen_peanut
    Allergen.TREE_NUTS -> R.string.allergen_tree_nuts
    Allergen.LACTOSE -> R.string.allergen_lactose
    Allergen.GLUTEN -> R.string.allergen_gluten
    Allergen.EGGS -> R.string.allergen_eggs
    Allergen.SOY -> R.string.allergen_soy
    Allergen.FISH -> R.string.allergen_fish
    Allergen.SHELLFISH -> R.string.allergen_shellfish
    Allergen.SESAME -> R.string.allergen_sesame
    Allergen.MUSTARD -> R.string.allergen_mustard
    Allergen.CELERY -> R.string.allergen_celery
    Allergen.SULFITES -> R.string.allergen_sulfites
}

@Composable
fun Allergen.title(): String = stringResource(titleRes())

@Composable
fun Set<Allergen>.titles(): String = sorted().map { it.title() }.joinToString(", ")
