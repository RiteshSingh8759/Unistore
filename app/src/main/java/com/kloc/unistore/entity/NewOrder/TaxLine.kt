package com.kloc.unistore.entity.NewOrder

data class TaxLine(
    val compound: Boolean,
    val id: Int,
    val label: String,
    val meta_data: List<Any>,
    val rate_code: String,
    val rate_id: Int,
    val rate_percent: Double,
    val shipping_tax_total: String,
    val tax_total: String
)