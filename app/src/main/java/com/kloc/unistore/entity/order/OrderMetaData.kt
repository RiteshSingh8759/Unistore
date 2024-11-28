package com.kloc.unistore.entity.order

data class OrderMetaData(
    val id: Int,
    val key: String,
    val value: Any, // This could be a Tcmp, Map, or primitive depending on the key
)
data class TmcpData(
    val tmcp_post_fields: TmcpPostFields
)

data class TmcpPostFields(
    val tmcp_textfield_0: String,
    val tmcp_textfield_1: String,
    val tmcp_textfield_2: String,
    val tmcp_textfield_3: String,
    val tmcp_select_4: String,
)
data class ItemStampData(
    val stamps: HashMap<String, StampData> = hashMapOf() // HashMap field added
)
data class StampData(
    val product_id: Int,
    val quantity: Int,
    val attributes: Attributes,
    val variation_id: String?,
    val discount: String?
)

data class Attributes(
    val attribute_class: String,
    val attribute_pa_color: String?,
    val attribute_pa_size: String,
    val attribute_pa_custom_size: String
)


data class TMCardEpoData(
    val mode: String,
    val cssclass: String,
    val hideLabelInCart: String,
    val hideValueInCart: String,
    val hideLabelInOrder: String,
    val hideValueInOrder: String,
    val element: Element,
    val name: String,
    val value: String,
    val price: Int,
    val section: String,
    val sectionLabel: String,
    val percentCurrentTotal: Int,
    val fixedCurrentTotal: Int,
    val currencies: List<String>,
    val pricePerCurrency: List<String>,
    val quantity: Int,
    val keyId: Int,
    val keyValueId: Int
)

data class Element(
    val type: String,
    val rules: Any?,
    val rulesType: Any?,
    val _metaDataSubElement : MetaDataSubElement
)

data class MetaDataSubElement(
    val priceType: Boolean
)

data class StaffMetaData(
    val staff_id: String,
)

