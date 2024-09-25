package com.example.ead_mobile_application__native.model

data class ProductDetails(
    val productId: String,
    val imageResId: Int,
    val name: String,
    val price: Double,
    val discount: Double,
    val size: List<String>,
    val color: List<String>,
    val description: String,
    val category: String,
    val stockQuantity: Int,
    val rating: Float,
)
