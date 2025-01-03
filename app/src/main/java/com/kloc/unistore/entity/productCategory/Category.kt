package com.kloc.unistore.entity.productCategory

data class Category(
    val _links: Links,
    val count: Int,
    val description: String,
    val display: String,
    val id: Int,
    val image: Image?,
    val menu_order: Int,
    val name: String,
    val parent: Int,
    val slug: String
)