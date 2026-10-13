package com.example.foodchecker.di

import org.koin.core.qualifier.named

/**
 * Квалификаторы для внедрения CoroutineDispatcher через Koin.
 * Use case получает диспетчер извне, поэтому в тестах его можно подменить на тестовый.
 */
object AppDispatchers {
    val Default = named("DefaultDispatcher")
}
