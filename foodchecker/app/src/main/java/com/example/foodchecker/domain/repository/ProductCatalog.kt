package com.example.foodchecker.domain.repository

import com.example.foodchecker.domain.model.Product

interface ProductCatalog {
    suspend fun findByBarcode(barcode: String): Product?
}
