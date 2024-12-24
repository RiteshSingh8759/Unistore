package com.kloc.unistore.model.productViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kloc.unistore.entity.product.Product
import com.kloc.unistore.repository.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Semaphore
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val productList = repository.fetchProducts(categoryId)
                withContext(Dispatchers.Main) {
                    _products.value = productList ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _products.value = emptyList()
                }
            }
        }
    }


    fun fetchProductDetailsById(productId: Int) {
        viewModelScope.launch {
            val product = repository.fetchProductDetailsById(productId)
            _productDetails.value = product // Update the StateFlow with the product details
        }
    }


    fun fetchBundledProducts(productIds: List<Int>) {
        val semaphore = Semaphore(10) // Limit concurrency to 10
        viewModelScope.launch {
            try {
                val products = productIds.map { id ->
                    async(Dispatchers.IO) {
                        semaphore.acquire()
                        try {
                            repository.fetchProductDetailsById(id)
                        } finally {
                            semaphore.release()
                        }
                    }
                }.awaitAll()
                _bundledProducts.value = products.filterNotNull()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun resetProductData()
    {
        _products.value = emptyList()
        _productDetails.value = null
        _bundledProducts.value = emptyList()
    }
}
