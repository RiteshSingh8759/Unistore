package com.kloc.unistore.entity.cart

import com.kloc.unistore.entity.product.Product

data class CartItem(
    val product: Product,
    var quantity: Int = 1, // Default quantity is 1
    var type:String="",
    var size: String = ""
)