package com.example.foodchecker.di

import androidx.room.Room
import com.example.foodchecker.data.init.AppDataInitializer
import com.example.foodchecker.data.json.AssetJsonDataSource
import com.example.foodchecker.data.local.RoomHistoryRepository
import com.example.foodchecker.data.local.RoomProfileRepository
import com.example.foodchecker.data.local.db.FoodCheckerDatabase
import com.example.foodchecker.data.preferences.AppSettingsDataSource
import com.example.foodchecker.data.preferences.settingsDataStore
import com.example.foodchecker.data.registry.RealmMedicalRegistryRepository
import com.example.foodchecker.data.registry.RegistryRealmProvider
import com.example.foodchecker.data.remote.FakeProductCatalog
import com.example.foodchecker.domain.repository.HistoryRepository
import com.example.foodchecker.domain.repository.MedicalRegistryRepository
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
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.time.Clock

val dispatchersModule = module {
    single<CoroutineDispatcher>(AppDispatchers.Default) { Dispatchers.Default }
    single<CoroutineDispatcher>(AppDispatchers.IO) { Dispatchers.IO }
}

val storageModule = module {
    single {
        Json { ignoreUnknownKeys = true }
    }
    single { androidContext().settingsDataStore }
    single {
        Room.databaseBuilder(androidContext(), FoodCheckerDatabase::class.java, FoodCheckerDatabase.NAME)
            .build()
    }
    single { get<FoodCheckerDatabase>().profileDao() }
    single { get<FoodCheckerDatabase>().historyDao() }
    single { RegistryRealmProvider(ioDispatcher = get(AppDispatchers.IO)) }
    single {
        AssetJsonDataSource(
            context = androidContext(),
            json = get(),
            ioDispatcher = get(AppDispatchers.IO)
        )
    }
    singleOf(::AppSettingsDataSource)
    single {
        AppDataInitializer(
            assets = get(),
            settings = get(),
            realmProvider = get(),
            database = get(),
            ioDispatcher = get(AppDispatchers.IO)
        )
    }
}

val dataModule = module {
    singleOf(::RoomProfileRepository) { bind<ProfileRepository>() }
    singleOf(::RoomHistoryRepository) { bind<HistoryRepository>() }
    singleOf(::RealmMedicalRegistryRepository) { bind<MedicalRegistryRepository>() }
    singleOf(::FakeProductCatalog) { bind<ProductCatalog>() }
    single<Clock> { Clock.systemDefaultZone() }
}

val domainModule = module {
    singleOf(::AnalyzeIngredientsUseCase)
    factory {
        ObserveHistoryUseCase(
            historyRepository = get(),
            profileRepository = get(),
            registryRepository = get(),
            analyzeIngredients = get(),
            defaultDispatcher = get(AppDispatchers.Default)
        )
    }
    factory {
        ObserveProductDetailsUseCase(
            historyRepository = get(),
            profileRepository = get(),
            registryRepository = get(),
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

val appModules = listOf(dispatchersModule, storageModule, dataModule, domainModule, presentationModule)
