package com.kloc.unistore.model.viewModel
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.Toast
import android.widget.TextView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dokar.sonner.ToastType
import com.kloc.unistore.model.cartViewModel.CartViewModel
import com.kloc.unistore.model.studentViewModel.StudentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application // Inject the Application context
) : ViewModel() {
    var logOut by mutableStateOf(false)
    var skipAddress by mutableStateOf(false)
    var className by  mutableStateOf("")
    // Singleton instance of CartViewModel
    val cartViewModel: CartViewModel by lazy {
        CartViewModel()
    }
    // Singleton instance of StudentViewModel
    val studentViewModel: StudentViewModel by lazy {
        StudentViewModel()
    }
}