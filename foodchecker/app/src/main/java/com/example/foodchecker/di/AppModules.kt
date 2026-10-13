package com.example.foodchecker.di

import com.example.foodchecker.data.local.InMemoryHistoryRepository
import com.example.foodchecker.data.local.InMemoryProfileRepository
import com.example.foodchecker.data.registry.InMemoryMedicalRegistry
import com.example.foodchecker.data.remote.FakeProductCatalog
import com.example.foodchecker.domain.repository.HistoryRepository
import com.example.foodchecker.domain.repository.MedicalRegistry
import com.example.foodchecker.domain.repository.ProductCatalog
import com.example.foodchecker.domain.repository.ProfileRepository
import com.example.foodchecker.domain.usecase.AnalyzeIngredientsUseCase
import com.example.foodchecker.domain.usecase.ObserveHistoryUseCase
import com.example.foodchecker.domain.usecase.ObserveProductDetailsUseCase
import com.example.foodchecker.domain.usecase.ScanProductUseCase
import com.example.foodchecker.ui.details.ProductDetailsViewModel
import com.example.foodchecker.ui.main.MainViewModel
import com.example.foodchecker.ui.profile.ProfileViewModel
import com.example.foodchecker.ui.scanner.ScannerViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.time.Clock

val dispatchersModule = module {
    single<CoroutineDispatcher>(AppDispatchers.Default) { Dispatchers.Default }
}

val dataModule = module {
    singleOf(::InMemoryProfileRepository) { bind<ProfileRepository>() }
    singleOf(::InMemoryHistoryRepository) { bind<HistoryRepository>() }
    singleOf(::FakeProductCatalog) { bind<ProductCatalog>() }
    singleOf(::InMemoryMedicalRegistry) { bind<MedicalRegistry>() }
    single<Clock> { Clock.systemDefaultZone() }
}

val domainModule = module {
    factoryOf(::AnalyzeIngredientsUseCase)
    factory {
        ObserveHistoryUseCase(
            historyRepository = get(),
            profileRepository = get(),
            analyzeIngredients = get(),
            defaultDispatcher = get(AppDispatchers.Default)
        )
    }
    factory {
        ObserveProductDetailsUseCase(
            historyRepository = get(),
            profileRepository = get(),
            analyzeIngredients = get(),
            defaultDispatcher = get(AppDispatchers.Default)
        )
    }
    factoryOf(::ScanProductUseCase)
}

val presentationModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ScannerViewModel)
    viewModel { parameters ->
        ProductDetailsViewModel(
            barcode = parameters.get(),
            observeProductDetails = get()
        )
    }
}

val appModules = listOf(dispatchersModule, dataModule, domainModule, presentationModule)
