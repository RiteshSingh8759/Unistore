package com.kloc.unistore.repository.order

import android.util.Log
import com.kloc.unistore.entity.order.Order
import com.kloc.unistore.service.UnistoreApiService
import javax.inject.Inject

class OrderRepository @Inject constructor() {
    private val apiService = UnistoreApiService.create()

    suspend fun createOrder(order: Order): Order? {
        return try {
            val response = apiService.createOrder(order)
            if (response.isSuccessful) {
                Log.d("debug", "Order creation successful: ${response.body()}")
                response.body() // Return the Order object if successful
            } else {
                Log.e("debug", "Order creation failed: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("debug", "Exception during order creation", e)
            null
        }
    }
}