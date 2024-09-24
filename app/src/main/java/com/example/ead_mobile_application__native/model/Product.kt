package com.example.ead_mobile_application__native.model

data class Product (
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val imageResId: Int,
    val rating: Float,
    val category: String,
    val stockQuantity: Int
)