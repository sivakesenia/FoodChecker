package com.example.foodchecker

import android.app.Application
import com.example.foodchecker.data.MockProductRepository
import com.example.foodchecker.data.ProductRepository

class FoodCheckerApplication : Application() {
    val repository: ProductRepository by lazy { MockProductRepository() }
}
