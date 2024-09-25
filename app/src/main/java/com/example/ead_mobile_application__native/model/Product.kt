package com.example.ead_mobile_application__native.model

data class Product (
    val productId: String,
    val imageResId: Int,
    val name: String,
    val price: Double,
    val discount: Double
)