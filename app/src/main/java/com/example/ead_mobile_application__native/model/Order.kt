package com.example.ead_mobile_application__native.model

data class Order(
    val orderId: Int,
    val productImageResId: Int,
    val orderDate: String,
    val status: String,
    val totalOrderPrice: Double
)
