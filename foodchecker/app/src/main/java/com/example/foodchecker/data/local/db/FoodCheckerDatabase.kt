package com.example.foodchecker.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserProfileEntity::class,
        ProfileAllergenEntity::class,
        ProductEntity::class,
        ScanEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FoodCheckerDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    abstract fun historyDao(): HistoryDao

    companion object {
        const val NAME = "foodchecker.db"
    }
}
