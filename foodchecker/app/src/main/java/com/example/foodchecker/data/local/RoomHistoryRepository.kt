package com.example.foodchecker.data.local

import com.example.foodchecker.data.init.AppDataInitializer
import com.example.foodchecker.data.local.db.HistoryDao
import com.example.foodchecker.domain.model.Product
import com.example.foodchecker.domain.model.ScannedProduct
import com.example.foodchecker.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.time.LocalDate

class RoomHistoryRepository(
    private val historyDao: HistoryDao,
    private val initializer: AppDataInitializer
) : HistoryRepository {

    override val history: Flow<List<ScannedProduct>> = historyDao
        .observeHistory()
        .map { rows -> rows.map { it.toDomain() } }
        .onStart { initializer.ensureInitialized() }

    override fun observe(barcode: String): Flow<ScannedProduct?> = historyDao
        .observeHistoryItem(barcode)
        .map { it?.toDomain() }
        .distinctUntilChanged()
        .onStart { initializer.ensureInitialized() }

    override suspend fun add(product: Product, scannedAt: LocalDate): ScannedProduct {
        historyDao.recordScan(product.toEntity(), scannedAt)
        return checkNotNull(historyDao.getHistoryItem(product.barcode)).toDomain()
    }
}
