package com.example.sneakhead.repository

import com.example.sneakhead.model.CartItemModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartRepositoryImplementation : CartRepository {
    
    private val database = FirebaseDatabase.getInstance()
    private val cartRef = database.reference.child("cart")
    
    override fun addToCart(cartItem: CartItemModel, callback: (Boolean, String) -> Unit) {
        val cartItemId = cartRef.push().key.toString()
        
        // Create a new cart item with the generated ID
        val newCartItem = cartItem.copy(cartItemId = cartItemId)
        
        cartRef.child(cartItemId).setValue(newCartItem).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Item added to cart successfully")
            } else {
                callback(false, "Failed to add item to cart: ${task.exception?.message}")
            }
        }
    }
    
    override fun removeFromCart(cartItemId: String, callback: (Boolean, String) -> Unit) {
        cartRef.child(cartItemId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Item removed from cart successfully")
            } else {
                callback(false, "Failed to remove item from cart: ${task.exception?.message}")
            }
        }
    }
    
    override fun updateQuantity(cartItemId: String, quantity: Int, callback: (Boolean, String) -> Unit) {
        cartRef.child(cartItemId).child("quantity").setValue(quantity).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Quantity updated successfully")
            } else {
                callback(false, "Failed to update quantity: ${task.exception?.message}")
            }
        }
    }
    
    override fun getCartItems(userId: String, callback: (List<CartItemModel>, Boolean, String) -> Unit) {
        cartRef.orderByChild("userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = mutableListOf<CartItemModel>()
                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val cartItem = itemSnapshot.getValue(CartItemModel::class.java)
                        if (cartItem != null) {
                            cartItems.add(cartItem)
                        }
                    }
                }
                callback(cartItems, true, "Cart items fetched successfully")
            }
            
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), false, "Failed to fetch cart items: ${error.message}")
            }
        })
    }
    
    override fun clearCart(userId: String, callback: (Boolean, String) -> Unit) {
        cartRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val removeTasks = mutableListOf<com.google.android.gms.tasks.Task<Void>>()
                
                for (itemSnapshot in snapshot.children) {
                    removeTasks.add(itemSnapshot.ref.removeValue())
                }
                
                if (removeTasks.isEmpty()) {
                    callback(true, "Cart is already empty")
                    return
                }
                
                com.google.android.gms.tasks.Tasks.whenAll(removeTasks).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Cart cleared successfully")
                    } else {
                        callback(false, "Failed to clear cart: ${task.exception?.message}")
                    }
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                callback(false, "Failed to clear cart: ${error.message}")
            }
        })
    }
}