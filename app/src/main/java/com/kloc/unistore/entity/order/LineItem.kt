package com.kloc.unistore.entity.order


data class LineItem(
    val bundled_by: String,
    val bundled_item_title: String,
    val bundled_items: List<Any>,
    val image: Image,
    val meta_data: List<Any>,
    val name: String,
    val parent_name: Any,
    val price: Int,
    val product_id: Int,
    val quantity: Int,
    val sku: String,
    val subtotal: String,
    val subtotal_tax: String,
//    val tax_class: String,
    val taxes: List<Taxe>,
    val total: String,
    val total_tax: String,
    val variation_id: Int
)
