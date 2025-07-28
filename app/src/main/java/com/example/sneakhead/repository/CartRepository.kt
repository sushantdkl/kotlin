package com.example.sneakhead.repository

import com.example.sneakhead.model.CartItemModel

interface CartRepository {
    fun addToCart(cartItem: CartItemModel, callback: (Boolean, String) -> Unit)
    
    fun removeFromCart(cartItemId: String, callback: (Boolean, String) -> Unit)
    
    fun updateQuantity(cartItemId: String, quantity: Int, callback: (Boolean, String) -> Unit)
    
    fun getCartItems(userId: String, callback: (List<CartItemModel>, Boolean, String) -> Unit)
    
    fun clearCart(userId: String, callback: (Boolean, String) -> Unit)
}