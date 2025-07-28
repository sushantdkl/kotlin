package com.example.sneakhead.model

data class CartItemModel(
    var cartItemId: String = "",
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImage: String = "",
    val quantity: Int = 1,
    val userId: String = ""
)
