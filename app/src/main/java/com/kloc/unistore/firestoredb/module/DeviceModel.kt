package com.kloc.unistore.firestoredb.module

class DeviceModel(
    val device: FirestoreDevice?,
    val key:String? = ""
)
{
    data class FirestoreDevice(
        val device_id:String?="",
        val merchant_id:String?="",
        val security_token:String?="",
        val store_id:String?="",
        val user_id:String?=""
    )
}