package com.kloc.unistore.model.productCategoryViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kloc.unistore.entity.productCategory.Category
import com.kloc.unistore.repository.productCategory.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchoolCategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    fun fetchCategories(schoolId: Int) {
        viewModelScope.launch {
            val response = repository.getCategoriesByParent(schoolId)
            _categories.value = response ?: emptyList()
        }
    }
}
