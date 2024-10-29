package com.kloc.unistore.entity.product

data class BundledItem(
    val allowed_variations: List<Any>,
    val bundled_item_id: Int,
    val cart_price_visibility: String,
    val cart_visibility: String,
    val default_variation_attributes: List<Any>,
    val description: String,
    val discount: String,
    val hide_thumbnail: Boolean,
    val menu_order: Int,
    val optional: Boolean,
    val order_price_visibility: String,
    val order_visibility: String,
    val override_default_variation_attributes: Boolean,
    val override_description: Boolean,
    val override_title: Boolean,
    val override_variations: Boolean,
    val priced_individually: Boolean,
    val product_id: Int,
    val quantity_max: String,
    val quantity_min: Int,
    val shipped_individually: Boolean,
    val single_product_price_visibility: String,
    val single_product_visibility: String,
    val stock_status: String,
    val title: String
)