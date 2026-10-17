package com.example.foodchecker.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProfileDao {

    @Transaction
    @Query("SELECT * FROM user_profile WHERE id = :profileId")
    abstract fun observeProfile(profileId: Long): Flow<ProfileWithAllergens?>

    @Query("SELECT EXISTS(SELECT 1 FROM user_profile)")
    abstract suspend fun hasProfile(): Boolean

    @Upsert
    abstract suspend fun upsertProfile(profile: UserProfileEntity)

    @Query("DELETE FROM profile_allergens WHERE profile_id = :profileId")
    abstract suspend fun deleteAllergens(profileId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertAllergens(allergens: List<ProfileAllergenEntity>)

    @Transaction
    open suspend fun saveProfile(profile: UserProfileEntity, allergens: List<ProfileAllergenEntity>) {
        upsertProfile(profile)
        deleteAllergens(profile.id)
        insertAllergens(allergens)
    }
}
