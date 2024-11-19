package com.kloc.unistore.entity.student

data class Student(
    val studentName: String,
    val parentName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val billingAddress: String,
    val shipingAddress: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val selectedClass: String,
    val gender: String
)
