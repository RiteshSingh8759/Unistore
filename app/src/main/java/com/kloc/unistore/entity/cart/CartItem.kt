package com.kloc.unistore.entity.cart

import com.kloc.unistore.entity.product.Product

data class CartItem(
    val product: Product,
    var quantity: Int = 1, // Default quantity is 1
    var min_Quantity: Int =0,
    var type:String="",
    var itemId: Int=0,
    var variationId: Int = 0,
    var size: String = "",
    var grade: String = "",
    var color: String = "",
    var price: String = "",
    var sku: String = "",
    var customSize:Boolean=false
)
