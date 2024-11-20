package com.kloc.unistore.entity.order

data class OrderMetaData(
    val key: String,
    val value: TmcpValue // Change value type to TmcpValue
)

data class TmcpValue(
    val tmcp_post_fields: TmcpPostFields
)

data class TmcpPostFields(
    val tmcp_textfield_0: String,
    val tmcp_textfield_1: String,
    val tmcp_textfield_2: String,
    val tmcp_textfield_3: String,
    val tmcp_select_4: String,
)
