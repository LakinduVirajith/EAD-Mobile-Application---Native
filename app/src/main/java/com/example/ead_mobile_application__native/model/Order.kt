package com.example.ead_mobile_application__native.model

data class Order(
    val orderId: String,
    val imageUri: String?,
    val orderDate: String,
    val status: String,
    val totalOrderPrice: Double
)
