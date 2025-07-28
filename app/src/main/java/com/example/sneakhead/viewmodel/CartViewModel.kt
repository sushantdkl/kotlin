package com.example.sneakhead.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sneakhead.model.CartItemModel
import com.example.sneakhead.repository.CartRepository

class CartViewModel(private val repo: CartRepository) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItemModel?>>()
    val cartItems: LiveData<List<CartItemModel?>> get() = _cartItems

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun getCartItems(userId: String) {
        _loading.postValue(true)
        repo.getCartItems(userId) { data, success, msg ->
            _loading.postValue(false)
            if (success) {
                _cartItems.postValue(data)
            } else {
                _cartItems.postValue(emptyList())
            }
        }
    }

    fun addCartItem(item: CartItemModel, callback: (Boolean, String) -> Unit) {
        repo.addToCart(item, callback)
    }

    fun updateCartItem(item: CartItemModel, callback: (Boolean, String) -> Unit) {
        repo.updateQuantity(item.cartItemId, item.quantity, callback)
    }

    fun deleteCartItem(cartItemId: String, callback: (Boolean, String) -> Unit = { _, _ -> }) {
        repo.removeFromCart(cartItemId, callback)
    }

    fun removeCartItem(productId: String, userId: String, callback: (Boolean, String) -> Unit) {
        getCartItems(userId)
        val item = _cartItems.value?.find { it?.productId == productId }
        if (item != null) {
            deleteCartItem(item.cartItemId, callback)
        } else {
            callback(false, "Item not found")
        }
    }

    fun updateCartItemQuantity(productId: String, userId: String, newQuantity: Int, callback: (Boolean, String) -> Unit) {
        getCartItems(userId)
        val item = _cartItems.value?.find { it?.productId == productId }
        if (item != null) {
            val updated = item.copy(quantity = newQuantity)
            updateCartItem(updated, callback)
        } else {
            callback(false, "Item not found")
        }
    }
}
