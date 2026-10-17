package com.example.foodchecker.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.foodchecker.domain.model.Allergen
import java.time.LocalDate

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long = DEFAULT_ID,
    val name: String
) {
    companion object {
        const val DEFAULT_ID = 1L
    }
}

@Entity(
    tableName = "profile_allergens",
    primaryKeys = ["profile_id", "allergen"],
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profile_id")]
)
data class ProfileAllergenEntity(
    @ColumnInfo(name = "profile_id")
    val profileId: Long,
    val allergen: Allergen
)

data class ProfileWithAllergens(
    @Embedded
    val profile: UserProfileEntity,
    @Relation(parentColumn = "id", entityColumn = "profile_id")
    val allergens: List<ProfileAllergenEntity>
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val barcode: String,
    val name: String,
    val brand: String,
    val ingredients: String?
)

@Entity(
    tableName = "scans",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["barcode"],
            childColumns = ["barcode"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("barcode")]
)
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val barcode: String,
    @ColumnInfo(name = "scanned_at")
    val scannedAt: LocalDate
)

data class HistoryRow(
    val barcode: String,
    val name: String,
    val brand: String,
    val ingredients: String?,
    @ColumnInfo(name = "scanned_at")
    val scannedAt: LocalDate,
    @ColumnInfo(name = "last_scan_id")
    val lastScanId: Long,
    @ColumnInfo(name = "scan_count")
    val scanCount: Int
)
