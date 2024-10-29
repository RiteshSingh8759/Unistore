package com.kloc.unistore.model.productViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kloc.unistore.entity.product.Product
import com.kloc.unistore.repository.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    fun getProducts(categoryId: Int) {
            viewModelScope.launch {
                    val productList = repository.fetchProducts(categoryId)
                    _products.value = productList?: emptyList() // Update the StateFlow
        }
    }
}