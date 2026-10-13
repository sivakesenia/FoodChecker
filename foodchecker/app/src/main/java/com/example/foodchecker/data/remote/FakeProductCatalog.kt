package com.example.foodchecker.data.remote

import com.example.foodchecker.data.sample.SampleData
import com.example.foodchecker.domain.model.Product
import com.example.foodchecker.domain.repository.ProductCatalog
import kotlinx.coroutines.delay

class FakeProductCatalog : ProductCatalog {

    private val products = SampleData.catalog.associateBy { it.barcode }

    override suspend fun findByBarcode(barcode: String): Product? {
        delay(NETWORK_DELAY_MS)
        return products[barcode]
    }

    private companion object {
        const val NETWORK_DELAY_MS = 700L
    }
}
