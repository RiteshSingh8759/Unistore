package com.kloc.unistore.entity.product

sealed class ValueType {
    data class ValueArray(val values: List<Value>) : ValueType()
    data class SingleValue(val value: Value) : ValueType()
    data class StringValue(val value: String) : ValueType()  // Add this case
}
