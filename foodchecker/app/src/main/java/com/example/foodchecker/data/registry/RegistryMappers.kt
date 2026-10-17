package com.example.foodchecker.data.registry

import com.example.foodchecker.data.json.AdditiveDto
import com.example.foodchecker.data.json.AllergenMarkerDto
import com.example.foodchecker.data.json.toAllergenOrNull
import com.example.foodchecker.data.json.toRiskLevel
import com.example.foodchecker.domain.model.Additive
import com.example.foodchecker.domain.model.AllergenMarker
import io.realm.kotlin.ext.toRealmList

fun AdditiveDto.toRealmObject(): AdditiveObject = AdditiveObject().also {
    it.id = id
    it.marker = marker
    it.name = name
    it.description = description
    it.risk = risk
    it.synonyms = synonyms.toRealmList()
}

fun AllergenMarkerDto.toRealmObject(): AllergenMarkerObject = AllergenMarkerObject().also {
    it.id = id
    it.allergen = allergen
    it.description = description
    it.risk = risk
    it.keywords = keywords.toRealmList()
    it.exclusions = exclusions.toRealmList()
}

fun AdditiveObject.toDomain(): Additive = Additive(
    id = id,
    marker = marker,
    name = name,
    description = description,
    risk = risk.toRiskLevel(),
    synonyms = synonyms.toList()
)

fun AllergenMarkerObject.toDomainOrNull(): AllergenMarker? {
    val allergenType = allergen.toAllergenOrNull() ?: return null
    return AllergenMarker(
        id = id,
        allergen = allergenType,
        keywords = keywords.toList(),
        description = description,
        risk = risk.toRiskLevel(),
        exclusions = exclusions.toList()
    )
}
