package com.kloc.unistore.entity.product
data class MetaData(
    val id: Int,
    val key: String,
    val value: ValueType // Use `ValueType` to handle both cases
)
