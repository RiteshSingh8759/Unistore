package com.kloc.unistore.entity.student

data class Student(
    val studentName: String,
    val parentName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val selectedClass: String,
    val paymentMethod: String,
    val gender: String
)
