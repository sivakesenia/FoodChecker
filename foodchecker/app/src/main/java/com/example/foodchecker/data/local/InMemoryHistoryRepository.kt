package com.example.foodchecker.data.local

import com.example.foodchecker.data.sample.SampleData
import com.example.foodchecker.domain.model.Product
import com.example.foodchecker.domain.model.ScannedProduct
import com.example.foodchecker.domain.repository.HistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class InMemoryHistoryRepository : HistoryRepository {

    private val state = MutableStateFlow(SampleData.history)

    override val history: Flow<List<ScannedProduct>> = state
        .asStateFlow()
        .onStart { delay(LOADING_DELAY_MS) }

    override fun observe(barcode: String): Flow<ScannedProduct?> = state
        .map { list -> list.firstOrNull { it.barcode == barcode } }
        .distinctUntilChanged()

    override suspend fun add(product: Product, scannedAt: LocalDate): ScannedProduct {
        val entry = ScannedProduct(
            barcode = product.barcode,
            name = product.name,
            brand = product.brand,
            ingredients = product.ingredients,
            scannedAt = scannedAt
        )
        state.update { list -> listOf(entry) + list.filterNot { it.barcode == product.barcode } }
        return entry
    }

    private companion object {
        const val LOADING_DELAY_MS = 600L
    }
}
