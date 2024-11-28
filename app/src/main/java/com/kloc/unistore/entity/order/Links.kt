package com.kloc.unistore.entity.order

data class Links(
    val collection: List<Collection>,
    val customer: List<Customer>,
    val self: List<Self>
)