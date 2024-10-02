package com.example.ead_mobile_application__native.model

data class Cart(
    val cartId: String,
    val imageUri: String?,
    val productId: String,
    val productName: String,
    val size: String,
    val color: String,
    val price: Double,
    val discount: Double,
    var quantity: Int
)
