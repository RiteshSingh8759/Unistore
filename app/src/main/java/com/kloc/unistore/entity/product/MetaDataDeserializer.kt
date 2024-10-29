package com.kloc.unistore.entity.product

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
class MetaDataDeserializer : JsonDeserializer<MetaData> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MetaData {
        val jsonObject = json.asJsonObject
        val id = jsonObject["id"].asInt
        val key = jsonObject["key"].asString
        val valueElement = jsonObject["value"]

        val value = when {
            valueElement.isJsonArray -> {
                val values = context.deserialize<List<Value>>(valueElement, object : TypeToken<List<Value>>() {}.type)
                ValueType.ValueArray(values)
            }
            valueElement.isJsonObject -> {
                val singleValue = context.deserialize<Value>(valueElement, Value::class.java)
                ValueType.SingleValue(singleValue)
            }
            valueElement.isJsonPrimitive && valueElement.asJsonPrimitive.isString -> {
                ValueType.StringValue(valueElement.asString)
            }
            else -> {
                throw JsonParseException("Unexpected value type for 'value' field in MetaData")
            }
        }

        return MetaData(id, key, value)
    }
}
class IntTypeAdapter : JsonDeserializer<Int> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Int {
        return if (json.asString.isEmpty()) {
            0 // Default value if the field is an empty string
        } else {
            json.asInt
        }
    }
}

class DoubleTypeAdapter : JsonDeserializer<Double> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Double {
        return if (json.asString.isEmpty()) {
            0.0 // Default value if the field is an empty string
        } else {
            json.asDouble
        }
    }
}

