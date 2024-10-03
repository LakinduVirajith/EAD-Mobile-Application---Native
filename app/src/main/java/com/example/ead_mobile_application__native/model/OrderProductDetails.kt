package com.example.ead_mobile_application__native.model

data class OrderProductDetails(
    val orderItemId: String,
    val productId: String,
    val productName: String,
    val imageUri: String?,
    val price: Double,
    val quantity: Int,
    val size: String,
    val color: String,
    val status: String,
)
