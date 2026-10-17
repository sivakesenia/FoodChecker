package com.example.foodchecker.domain.usecase

import com.example.foodchecker.domain.model.ProductDetails
import com.example.foodchecker.domain.repository.HistoryRepository
import com.example.foodchecker.domain.repository.MedicalRegistryRepository
import com.example.foodchecker.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class ObserveProductDetailsUseCase(
    private val historyRepository: HistoryRepository,
    private val profileRepository: ProfileRepository,
    private val registryRepository: MedicalRegistryRepository,
    private val analyzeIngredients: AnalyzeIngredientsUseCase,
    private val defaultDispatcher: CoroutineDispatcher
) {
    operator fun invoke(barcode: String): Flow<ProductDetails?> =
        combine(
            historyRepository.observe(barcode),
            profileRepository.profile,
            registryRepository.registry
        ) { product, profile, registry ->
            product?.let {
                ProductDetails(
                    product = it,
                    analysis = analyzeIngredients(it.ingredients, profile, registry)
                )
            }
        }
            .flowOn(defaultDispatcher)
}
