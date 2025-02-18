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
    fun addToCart(product: Product, quantity: Int,min_Quantity:Int, selectedSize: String?, selectedGrade: String, selectedColor: String,sizeType: String,variationId:Int,itemId:Int,customSize:Boolean): String {
        val sizeToCompare = selectedSize ?: "" // Handle products without sizes
        val existingItem = _cartItems.value.find {
            it.product.id == product.id && it.size == sizeToCompare && it.color == selectedColor && it.grade == selectedGrade
        }
        return if (existingItem != null) {
            // Update the quantity of the existing item
            _cartItems.value = _cartItems.value.map {
                if (it == existingItem) it.copy(quantity = it.quantity + quantity) else it
            }
            "Product with selected size and color already exists.Quantity updated by $quantity"
        } else {
            _cartItems.value = _cartItems.value + CartItem(product, quantity, min_Quantity,sizeType,itemId,variationId,selectedSize?:"", selectedGrade?:"", selectedColor?:"",customSize=customSize)
            "Product added to cart."
        }.also {
            // Trigger state update regardless
            _cartItems.value = _cartItems.value.toList()
        }
    }
    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        _cartItems.value = _cartItems.value.map { item ->
            if (item.product.id == cartItem.product.id && item.size == cartItem.size && item.color == cartItem.color) {
                // Update the quantity of the cart item
                item.copy(quantity = newQuantity)
            } else {
                item
            }
        }
    }
    fun removeFromCart(cartItem: CartItem) {
        _cartItems.value = _cartItems.value.filterNot {
            it.product.id == cartItem.product.id && it.size == cartItem.size && it.color == cartItem.color
        }
    }
    fun clearCart() {
        _cartItems.value = emptyList()
    }
}



