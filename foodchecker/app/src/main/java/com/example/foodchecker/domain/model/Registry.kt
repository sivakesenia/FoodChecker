package com.example.foodchecker.domain.model

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

data class Additive(
    val id: String,
    val marker: String,
    val name: String,
    val description: String,
    val risk: RiskLevel,
    val synonyms: List<String> = emptyList()
)

/**
 * @param keywords основы слов, по которым аллерген ищется в составе.
 * @param exclusions регулярные выражения для словосочетаний, где ключевое слово
 * не означает этот аллерген (например, «мускатный орех» — пряность, а не орех).
 * Совпадения с исключениями вырезаются из текста до поиска ключевых слов.
 */
data class AllergenMarker(
    val id: String,
    val allergen: Allergen,
    val keywords: List<String>,
    val description: String,
    val exclusions: List<String> = emptyList()
)
