package com.example.ead_mobile_application__native.model

data class ProductDetails(
    val productId: String,
    val imageUri: String?,
    val name: String,
    val brand: String,
    val description: String,
    val price: Double,
    val discount: Double,
    val size: List<String>,
    val color: List<String>,
    val category: String,
    val stockQuantity: Int
)
