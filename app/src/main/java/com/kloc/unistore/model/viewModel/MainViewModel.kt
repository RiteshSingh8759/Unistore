package com.kloc.unistore.model.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.kloc.unistore.model.cartViewModel.CartViewModel
import com.kloc.unistore.model.studentViewModel.StudentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application // Inject the Application context
) : ViewModel() {
    // Singleton instance of CartViewModel
    val cartViewModel: CartViewModel by lazy {
        CartViewModel()
    }

    // Singleton instance of StudentViewModel
    val studentViewModel: StudentViewModel by lazy {
        StudentViewModel()
    }
    fun showToast(message: String) {
        Toast.makeText(application, message, Toast.LENGTH_SHORT).show() // Use application context for the toast
    }
}