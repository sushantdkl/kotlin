package com.example.sneakhead.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sneakhead.repository.CartRepository

class CartViewModelFactory(private val repo: CartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 