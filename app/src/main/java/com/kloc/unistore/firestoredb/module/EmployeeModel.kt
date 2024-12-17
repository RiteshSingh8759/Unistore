package com.kloc.unistore.firestoredb.module

class EmployeeModel(
    val employee: FirestoreUser?,
    val key:String? = ""
)
{
    data class FirestoreUser(
        val name:String?="",
        val email:String?="",
        val id:String?="",
    )
}