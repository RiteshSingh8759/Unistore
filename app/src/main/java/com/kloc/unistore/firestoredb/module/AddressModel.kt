package com.kloc.unistore.firestoredb.module

class AddressModel(
    val address: FirestoreAddress,
    val key:String? = ""
)
{
        data class FirestoreAddress(
            val school_id: String?="",
            val addressLine1: String?="",
            val addressLine2: String?="",
            val city: String?="",
            val state: String?="",
            val zipcode: String?="",
            val country: String?=""
        )
}