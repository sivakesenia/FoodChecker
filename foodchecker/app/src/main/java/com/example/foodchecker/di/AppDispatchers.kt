package com.example.foodchecker.di

import org.koin.core.qualifier.named

object AppDispatchers {
    val Default = named("DefaultDispatcher")
    val IO = named("IoDispatcher")
}
