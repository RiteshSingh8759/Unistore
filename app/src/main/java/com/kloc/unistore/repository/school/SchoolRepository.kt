package com.kloc.unistore.repository.school

import com.kloc.unistore.entity.school.SchoolCategory
import com.kloc.unistore.service.UnistoreApiService
import javax.inject.Inject

class SchoolRepository @Inject constructor(){
    private val apiService = UnistoreApiService.create()

    suspend fun fetchSchoolDetails(slug: String): List<SchoolCategory>? {
        val response = apiService.fetchSchoolDetailsBySlug(slug)
        return if (response.isSuccessful) response.body() else null
    }
}
