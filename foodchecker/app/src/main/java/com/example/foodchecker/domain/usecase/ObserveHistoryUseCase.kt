package com.example.foodchecker.domain.usecase

import com.example.foodchecker.domain.model.HistoryOverview
import com.example.foodchecker.domain.repository.HistoryRepository
import com.example.foodchecker.domain.repository.MedicalRegistryRepository
import com.example.foodchecker.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class ObserveHistoryUseCase(
    private val historyRepository: HistoryRepository,
    private val profileRepository: ProfileRepository,
    private val registryRepository: MedicalRegistryRepository,
    private val analyzeIngredients: AnalyzeIngredientsUseCase,
    private val defaultDispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<HistoryOverview> =
        combine(
            profileRepository.profile,
            historyRepository.history,
            registryRepository.registry
        ) { profile, history, registry ->
            HistoryOverview(
                profile = profile,
                checks = history.map { analyzeIngredients.check(it, profile, registry) }
            )
        }
            .flowOn(defaultDispatcher)
}
