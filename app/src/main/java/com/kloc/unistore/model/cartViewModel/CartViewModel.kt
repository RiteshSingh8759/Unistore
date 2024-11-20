package com.kloc.unistore.model.cartViewModel

import androidx.lifecycle.ViewModel
import com.kloc.unistore.entity.cart.CartItem
import com.kloc.unistore.entity.product.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addToCart(product: Product, quantity: Int, selectedSize: String?,sizeType: String): String {
        val existingItem = _cartItems.value.find {
            it.product.id == product.id && it.size == selectedSize
        }
        return if (existingItem != null) {
            "Product with the selected size already exists in the cart."
        } else {
            _cartItems.value = _cartItems.value + CartItem(product, quantity, sizeType,selectedSize?:"")
            "Product added to cart."
        }.also {
            // Trigger state update regardless
            _cartItems.value = _cartItems.value.toList()
        }
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        _cartItems.value = _cartItems.value.map { item ->
            if (item.product.id == cartItem.product.id && item.size == cartItem.size) {
                // Update the quantity of the cart item
                item.copy(quantity = newQuantity)
            } else {
                item
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        _cartItems.value = _cartItems.value.filterNot {
            it.product.id == cartItem.product.id && it.size == cartItem.size
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}
