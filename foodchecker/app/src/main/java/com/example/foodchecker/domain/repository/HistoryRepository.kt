package com.example.foodchecker.domain.repository

import com.example.foodchecker.domain.model.Product
import com.example.foodchecker.domain.model.ScannedProduct
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HistoryRepository {
    val history: Flow<List<ScannedProduct>>
    fun observe(barcode: String): Flow<ScannedProduct?>
    suspend fun add(product: Product, scannedAt: LocalDate): ScannedProduct
}
