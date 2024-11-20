package com.kloc.unistore.entity.NewOrder

data class Links(
    val collection: List<Collection>,
    val customer: List<Customer>,
    val self: List<Self>
)