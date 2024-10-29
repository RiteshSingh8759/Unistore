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

    private val _productDetails = MutableStateFlow<Product?>(null)
    val productDetails: StateFlow<Product?> = _productDetails

    private val _bundledProducts = MutableStateFlow<List<Product>>(emptyList())
    val bundledProducts: StateFlow<List<Product>> = _bundledProducts

    fun getProducts(categoryId: Int) {
            viewModelScope.launch {
                    val productList = repository.fetchProducts(categoryId)
                    _products.value = productList?: emptyList() // Update the StateFlow
        }
    }

    fun fetchProductDetailsById(productId: Int) {
        viewModelScope.launch {
            val product = repository.fetchProductDetailsById(productId)
            _productDetails.value = product // Update the StateFlow with the product details
        }
    }


    fun fetchBundledProducts(productIds: List<Int>) {
        viewModelScope.launch {
            // Fetch details for all provided product IDs
            val products = productIds.mapNotNull { id ->
                repository.fetchProductDetailsById(id) // Adjust this as needed
            }
            _bundledProducts.value = products // Update the state flow with the list of products
        }
    }
}
