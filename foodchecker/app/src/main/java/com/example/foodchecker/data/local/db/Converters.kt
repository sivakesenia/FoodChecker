package com.example.foodchecker.data.local.db

import androidx.room.TypeConverter
import com.example.foodchecker.domain.model.Allergen
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun epochDayToLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    @TypeConverter
    fun allergenToName(allergen: Allergen): String = allergen.name

    @TypeConverter
    fun nameToAllergen(name: String): Allergen = Allergen.valueOf(name)
}
