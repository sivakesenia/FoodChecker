package com.example.foodchecker.data.registry

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey

class AdditiveObject : RealmObject {
    @PrimaryKey
    var id: String = ""

    @Index
    var marker: String = ""
    var name: String = ""
    var description: String = ""
    var risk: String = ""
    var synonyms: RealmList<String> = realmListOf()
}

class AllergenMarkerObject : RealmObject {
    @PrimaryKey
    var id: String = ""

    @Index
    var allergen: String = ""
    var description: String = ""
    var risk: String = ""
    var keywords: RealmList<String> = realmListOf()
    var exclusions: RealmList<String> = realmListOf()
}
