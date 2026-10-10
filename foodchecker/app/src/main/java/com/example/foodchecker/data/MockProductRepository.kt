package com.example.foodchecker.data

import com.example.foodchecker.model.Allergen
import com.example.foodchecker.model.ScannedProduct
import com.example.foodchecker.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import java.time.LocalDate

class MockProductRepository : ProductRepository {

    private val profileState = MutableStateFlow(sampleProfile)
    private val historyState = MutableStateFlow(sampleHistory)

    override val profile: Flow<UserProfile> = profileState.asStateFlow()

    override val history: Flow<List<ScannedProduct>> = historyState
        .asStateFlow()
        .onStart { delay(LOADING_DELAY_MS) }

    override suspend fun updateProfile(profile: UserProfile) {
        profileState.value = profile
    }

    companion object {
        private const val LOADING_DELAY_MS = 600L

        val sampleProfile = UserProfile(
            name = "Иван Иванов",
            criticalAllergens = setOf(Allergen.PEANUT, Allergen.LACTOSE, Allergen.GLUTEN)
        )

        val sampleHistory = listOf(
            ScannedProduct(
                id = "1",
                barcode = "4680019651234",
                name = "Батончик «Ореховый рай»",
                brand = "Sweet Co",
                scannedAt = LocalDate.of(2026, 9, 12),
                allergens = setOf(Allergen.PEANUT, Allergen.TREE_NUTS, Allergen.SOY)
            ),
            ScannedProduct(
                id = "2",
                barcode = "4607025392012",
                name = "Йогурт натуральный 2,5%",
                brand = "MilkHouse",
                scannedAt = LocalDate.of(2026, 9, 11),
                allergens = setOf(Allergen.LACTOSE)
            ),
            ScannedProduct(
                id = "3",
                barcode = "4601234567890",
                name = "Рисовые хлебцы",
                brand = "GrainLife",
                scannedAt = LocalDate.of(2026, 9, 10),
                allergens = emptySet()
            ),
            ScannedProduct(
                id = "4",
                barcode = "4600123409876",
                name = "Овсяное печенье",
                brand = "GrainLife",
                scannedAt = LocalDate.of(2026, 9, 9),
                allergens = null
            ),
            ScannedProduct(
                id = "5",
                barcode = "4601987654321",
                name = "Хлеб пшеничный с кунжутом",
                brand = "Bakery Plus",
                scannedAt = LocalDate.of(2026, 9, 8),
                allergens = setOf(Allergen.GLUTEN, Allergen.SESAME)
            ),
            ScannedProduct(
                id = "6",
                barcode = "4607001234561",
                name = "Тунец в собственном соку",
                brand = "Ocean Line",
                scannedAt = LocalDate.of(2026, 9, 7),
                allergens = setOf(Allergen.FISH)
            ),
            ScannedProduct(
                id = "7",
                barcode = "4600987123450",
                name = "Майонез «Провансаль»",
                brand = "Vkusno",
                scannedAt = LocalDate.of(2026, 9, 5),
                allergens = setOf(Allergen.EGGS, Allergen.MUSTARD)
            ),
            ScannedProduct(
                id = "8",
                barcode = "4602345678901",
                name = "Сок яблочный осветлённый",
                brand = "Sad Fruit",
                scannedAt = LocalDate.of(2026, 9, 3),
                allergens = emptySet()
            )
        )
    }
}
