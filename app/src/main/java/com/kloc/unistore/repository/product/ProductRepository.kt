package com.kloc.unistore.repository.product

import com.kloc.unistore.entity.product.Product
import com.kloc.unistore.service.UnistoreApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor() {
    private val apiService = UnistoreApiService.create()
    suspend fun fetchProducts(categoryId: Int): List<Product>? {
        val response = apiService.getProductsByCategory(categoryId)
        return if (response.isSuccessful) response.body() else null
    }
}