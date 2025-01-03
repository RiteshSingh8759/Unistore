package com.kloc.unistore.entity.student
data class Student(
    val studentName: String,
    val parentName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val billingAddressLine1: String,
    val billingAddressLine2: String,
    val billingCity: String,
    val billingState: String,
    val billingZipCode: String,
    val selectedClass: String,
    val gender: String,
    val note: String
)
