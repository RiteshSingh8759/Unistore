package com.kloc.unistore.entity.order

import com.kloc.unistore.entity.product.MetaData

data class LineItem(
    val product_id: Int,
    val quantity: Int,
    val variation_id: Int? = null,
    val meta_data: List<OrderMetaData>
)
