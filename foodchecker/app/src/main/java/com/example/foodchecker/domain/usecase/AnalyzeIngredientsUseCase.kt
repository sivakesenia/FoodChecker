package com.example.foodchecker.domain.usecase

import com.example.foodchecker.domain.model.AllergenFinding
import com.example.foodchecker.domain.model.AllergenMarker
import com.example.foodchecker.domain.model.ComponentRisk
import com.example.foodchecker.domain.model.IngredientAnalysis
import com.example.foodchecker.domain.model.IngredientToken
import com.example.foodchecker.domain.model.MedicalRegistry
import com.example.foodchecker.domain.model.ProductCheck
import com.example.foodchecker.domain.model.RiskLevel
import com.example.foodchecker.domain.model.ScannedProduct
import com.example.foodchecker.domain.model.UserProfile
import com.example.foodchecker.domain.model.Verdict
import java.util.concurrent.ConcurrentHashMap

class AnalyzeIngredientsUseCase {

    private val exclusionCache = ConcurrentHashMap<String, Regex>()

    operator fun invoke(
        ingredients: String?,
        profile: UserProfile,
        registry: MedicalRegistry
    ): IngredientAnalysis {
        if (ingredients.isNullOrBlank()) return IngredientAnalysis.Empty

        val tokens = tokenize(ingredients).map { (text, isSeparator) ->
            if (isSeparator) {
                IngredientToken(text = text, isSeparator = true)
            } else {
                analyzeComponent(text, profile, registry)
            }
        }
        val components = tokens.filterNot { it.isSeparator }
        val foundAllergens = components.flatMap { it.allergens }.toSet()
        val findings = registry.allergenMarkers
            .filter { it.allergen in foundAllergens }
            .map { AllergenFinding(marker = it, isCritical = it.allergen in profile.criticalAllergens) }
            .sortedByDescending { it.isCritical }
        val additives = components
            .flatMap { it.additives }
            .distinctBy { it.id }
            .sortedByDescending { it.risk }
        val unknownCodes = components.flatMap { it.unknownCodes }.distinct()
        val isDangerous = findings.any { it.isCritical } || additives.any { it.risk == RiskLevel.HIGH }

        return IngredientAnalysis(
            tokens = tokens,
            allergens = findings,
            additives = additives,
            unknownAdditiveCodes = unknownCodes,
            verdict = if (isDangerous) Verdict.DANGEROUS else Verdict.SAFE
        )
    }

    fun check(product: ScannedProduct, profile: UserProfile, registry: MedicalRegistry): ProductCheck {
        val analysis = invoke(product.ingredients, profile, registry)
        return ProductCheck(
            product = product,
            verdict = analysis.verdict,
            criticalAllergens = analysis.criticalAllergens,
            harmfulAdditives = analysis.harmfulAdditives
        )
    }

    private fun analyzeComponent(
        text: String,
        profile: UserProfile,
        registry: MedicalRegistry
    ): IngredientToken {
        val lower = text.lowercase()
        val allergens = registry.allergenMarkers
            .filter { marker -> marker.matches(lower) }
            .map { it.allergen }
            .toSet()
        val codes = AdditiveCodePattern.findAll(text).map { normalizeCode(it) }.distinct().toList()
        val additivesByCode = codes.mapNotNull { registry.findAdditive(it) }
        val unknownCodes = codes.filter { registry.findAdditive(it) == null }
        val additivesByName = registry.additives.filter { additive ->
            additive.synonyms.any { lower.contains(it) }
        }
        val additives = (additivesByCode + additivesByName).distinctBy { it.id }

        val risk = when {
            allergens.any { it in profile.criticalAllergens } -> ComponentRisk.HIGH
            additives.any { it.risk == RiskLevel.HIGH } -> ComponentRisk.HIGH
            allergens.isNotEmpty() -> ComponentRisk.MEDIUM
            additives.any { it.risk == RiskLevel.MEDIUM } -> ComponentRisk.MEDIUM
            unknownCodes.isNotEmpty() -> ComponentRisk.MEDIUM
            additives.isNotEmpty() -> ComponentRisk.LOW
            else -> ComponentRisk.NONE
        }

        return IngredientToken(
            text = text,
            isSeparator = false,
            allergens = allergens,
            additives = additives,
            unknownCodes = unknownCodes,
            risk = risk
        )
    }

    private fun AllergenMarker.matches(lowerText: String): Boolean {
        val text = exclusions.fold(lowerText) { acc, pattern ->
            acc.replace(exclusionRegex(pattern), " ")
        }
        return keywords.any { text.contains(it) }
    }

    private fun exclusionRegex(pattern: String): Regex =
        exclusionCache.getOrPut(pattern) { Regex(pattern, RegexOption.IGNORE_CASE) }

    private fun tokenize(text: String): List<Pair<String, Boolean>> {
        val result = mutableListOf<Pair<String, Boolean>>()
        val current = StringBuilder()

        fun flush() {
            if (current.isEmpty()) return
            val value = current.toString()
            current.clear()
            val trimmed = value.trim()
            if (trimmed.isEmpty()) {
                result += value to true
                return
            }
            val start = value.indexOf(trimmed)
            val leading = value.substring(0, start)
            val trailing = value.substring(start + trimmed.length)
            if (leading.isNotEmpty()) result += leading to true
            result += trimmed to false
            if (trailing.isNotEmpty()) result += trailing to true
        }

        text.forEachIndexed { index, char ->
            val isNumberPart = (char == ',' || char == '.') &&
                index > 0 && index < text.lastIndex &&
                text[index - 1].isDigit() && text[index + 1].isDigit()
            if (char in Separators && !isNumberPart) {
                flush()
                result += char.toString() to true
            } else {
                current.append(char)
            }
        }
        flush()
        return result
    }

    private fun normalizeCode(match: MatchResult): String {
        val digits = match.groupValues[1]
        val suffix = match.groupValues[2].lowercase().map { SuffixMapping[it] ?: it }.joinToString("")
        return "E$digits$suffix"
    }

    private companion object {
        val Separators = setOf(',', ';', '(', ')', '[', ']', ':', '.')
        val AdditiveCodePattern = Regex("(?<![\\p{L}\\d])[EeЕе][\\s-]?(\\d{3,4})([a-dA-DабсдАБСД]?)(?![\\p{L}\\d])")
        val SuffixMapping = mapOf('а' to 'a', 'б' to 'b', 'с' to 'c', 'д' to 'd')
    }
}
