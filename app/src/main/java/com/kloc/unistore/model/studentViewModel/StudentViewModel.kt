package com.kloc.unistore.model.studentViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kloc.unistore.entity.student.Student
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor() : ViewModel() {

    private val _studentDetails = MutableStateFlow<Student?>(null)
    val studentDetails: StateFlow<Student?> = _studentDetails

    fun saveStudentDetails(student: Student): String {
        _studentDetails.value = student
        return "Student details saved successfully."
    }

    fun clearStudentDetails() {
        _studentDetails.value = null
    }
}
