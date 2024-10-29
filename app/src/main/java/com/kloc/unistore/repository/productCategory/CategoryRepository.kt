package com.kloc.unistore.repository.productCategory

import com.kloc.unistore.entity.productCategory.Category
import com.kloc.unistore.service.UnistoreApiService
import javax.inject.Inject

class CategoryRepository @Inject constructor() {
    private val apiService = UnistoreApiService.create()
    suspend fun getCategoriesByParent(parentId: Int): List<Category>? {
        val response = apiService.getCategoriesByParent(parentId)
        return if (response.isSuccessful) response.body() else null
    }
}
