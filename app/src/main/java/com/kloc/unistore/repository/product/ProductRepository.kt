package com.kloc.unistore.repository.product

import com.kloc.unistore.entity.product.Product
import com.kloc.unistore.entity.productVariation.ProductVariationItem
import com.kloc.unistore.service.UnistoreApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor() {
    private val apiService = UnistoreApiService.create()
    suspend fun fetchProducts(categoryId: Int): List<Product>? {
        val response = apiService.getProductsByCategory(categoryId,"bundle")
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun fetchProductDetailsById(productId: Int): Product? {
        val response = apiService.getProductById(productId)
        return if (response.isSuccessful) response.body() else null
    }
    suspend fun fetchProductsByIds(includeIds: String): Response<List<Product>> {
        return apiService.getProductsByIds(include = includeIds)
    }
    suspend fun fetchProductVariations(productId: Int, search: String = ""): List<ProductVariationItem>? {
        val response = if (search.isBlank()){
            apiService.getAllProductVariations(productId)
        } else {
            apiService.getProductVariationsWithSearch(productId, search)
        }
        return if (response.isSuccessful) response.body() else null
    }

}