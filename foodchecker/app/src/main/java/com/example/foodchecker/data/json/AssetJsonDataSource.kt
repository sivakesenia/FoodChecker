package com.example.foodchecker.data.json

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class AssetJsonDataSource(
    private val context: Context,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun readMedicalRegistry(): MedicalRegistryDto =
        json.decodeFromString(MedicalRegistryDto.serializer(), readAsset(MEDICAL_REGISTRY_FILE))

    suspend fun readProductCatalog(): List<ProductDto> =
        json.decodeFromString(ProductCatalogDto.serializer(), readAsset(PRODUCT_CATALOG_FILE)).products

    suspend fun readDemoData(): DemoDataDto =
        json.decodeFromString(DemoDataDto.serializer(), readAsset(DEMO_DATA_FILE))

    private suspend fun readAsset(fileName: String): String = withContext(ioDispatcher) {
        context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private companion object {
        const val MEDICAL_REGISTRY_FILE = "medical_registry.json"
        const val PRODUCT_CATALOG_FILE = "product_catalog.json"
        const val DEMO_DATA_FILE = "demo_data.json"
    }
}
