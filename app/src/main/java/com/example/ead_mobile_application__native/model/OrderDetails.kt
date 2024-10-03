package com.example.ead_mobile_application__native.model

data class OrderDetails (
    val orderId: String,
    val orderDate: String,
    val status: String,
    val totalOrderPrice: Double,
    val phoneNumber: String,
    val userName: String,
    val address: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val orderItemDetails: List<OrderProductDetails>
)
