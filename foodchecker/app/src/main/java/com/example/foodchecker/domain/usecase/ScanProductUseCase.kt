package com.example.foodchecker.domain.usecase

import com.example.foodchecker.domain.model.ScanResult
import com.example.foodchecker.domain.repository.HistoryRepository
import com.example.foodchecker.domain.repository.ProductCatalog
import java.time.Clock
import java.time.LocalDate

class ScanProductUseCase(
    private val productCatalog: ProductCatalog,
    private val historyRepository: HistoryRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(barcode: String): ScanResult {
        val product = productCatalog.findByBarcode(barcode) ?: return ScanResult.NotFound(barcode)
        val entry = historyRepository.add(product, LocalDate.now(clock))
        return ScanResult.Found(entry)
    }
}
