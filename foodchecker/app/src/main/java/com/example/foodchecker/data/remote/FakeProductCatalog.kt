package com.example.foodchecker.data.remote

import com.example.foodchecker.data.json.AssetJsonDataSource
import com.example.foodchecker.data.json.toDomain
import com.example.foodchecker.domain.model.Product
import com.example.foodchecker.domain.repository.ProductCatalog
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeProductCatalog(
    private val assets: AssetJsonDataSource
) : ProductCatalog {

    private val mutex = Mutex()
    private var products: Map<String, Product>? = null

    override suspend fun findByBarcode(barcode: String): Product? {
        delay(NETWORK_DELAY_MS)
        return loadProducts()[barcode]
    }

    private suspend fun loadProducts(): Map<String, Product> = mutex.withLock {
        products ?: assets.readProductCatalog()
            .map { it.toDomain() }
            .associateBy { it.barcode }
            .also { products = it }
    }

    private companion object {
        const val NETWORK_DELAY_MS = 700L
    }
}
