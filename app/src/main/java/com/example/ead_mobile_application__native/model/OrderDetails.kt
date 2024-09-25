package com.example.ead_mobile_application__native.model

data class OrderDetails (
    val productId: Int,
    val productName: String,
    val imageResId: Int,
    val price: Double,
    val quantity: Int,
    val size: String,
    val color: String
)