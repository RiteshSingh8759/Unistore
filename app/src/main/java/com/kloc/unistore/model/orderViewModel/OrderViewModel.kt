package com.kloc.unistore.model.orderViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kloc.unistore.entity.order.Order
import com.kloc.unistore.repository.order.OrderRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    fun placeOrder(order: Order, callback: (Order?) -> Unit) {
        viewModelScope.launch {
            try {
                val result = orderRepository.createOrder(order)
                callback(result) // Return the Order object or null
            } catch (e: Exception) {
                callback(null) // Pass null if there's an error
            }
        }
    }
}



