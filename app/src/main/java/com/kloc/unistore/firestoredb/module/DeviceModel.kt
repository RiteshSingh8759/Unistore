package com.kloc.unistore.firestoredb.module

class DeviceModel(
    val device: FirestoreDevice?,
    val key:String? = ""
)
{
    data class FirestoreDevice(
        val device_id:String?=""
    )
}