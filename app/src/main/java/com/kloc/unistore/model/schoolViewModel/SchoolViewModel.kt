package com.kloc.unistore.model.schoolViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kloc.unistore.entity.school.SchoolCategory
import com.kloc.unistore.repository.school.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class SchoolViewModel @Inject constructor(
    private val repository: SchoolRepository
) : ViewModel()
{

    private val _schoolDetails = MutableStateFlow<List<SchoolCategory>?>(null)
    val schoolDetails: StateFlow<List<SchoolCategory>?> = _schoolDetails

    fun getSchoolDetails(slug: String) {
        viewModelScope.launch {
            try {
                _schoolDetails.value = null // Reset previous data
                _schoolDetails.value = repository.fetchSchoolDetails(slug)
            } catch (e: Exception) {
                _schoolDetails.value = emptyList() // Indicate error by setting empty list
            }
        }
    }
}
