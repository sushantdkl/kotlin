package com.example.sneakhead.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sneakhead.R
import com.example.sneakhead.model.CartItemModel
import com.example.sneakhead.repository.CartRepositoryImplementation
import com.example.sneakhead.viewmodel.CartViewModel
import com.example.sneakhead.viewmodel.CartViewModelFactory

class CartActivity : ComponentActivity() {
    private val cartViewModel: CartViewModel by viewModels {
        CartViewModelFactory(CartRepositoryImplementation())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get current user email from SharedPreferences
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("email", "") ?: ""

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
                    val loading by cartViewModel.loading.observeAsState(false)

                    // Refresh cart items when activity becomes visible
                    LaunchedEffect(Unit) {
                        cartViewModel.getCartItems(userId)
                    }

                    if (loading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
                        CartScreen(
                            cartItems = cartItems.filterNotNull(),
                            onDelete = { item ->
                                cartViewModel.deleteCartItem(item.cartItemId) { success, message ->
                                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                                    if (success) cartViewModel.getCartItems(userId)
                                }
                            },
                            onUpdateQuantity = { item, newQuantity ->
                                cartViewModel.updateCartItemQuantity(item.productId, userId, newQuantity) { success, message ->
                                    if (success) {
                                        cartViewModel.getCartItems(userId)
                                    } else {
                                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onProceedToCheckout = {
                                val intent = Intent(this, CheckoutActivity::class.java)
                                startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItemModel>, 
    onDelete: (CartItemModel) -> Unit,
    onUpdateQuantity: (CartItemModel, Int) -> Unit,
    onProceedToCheckout: () -> Unit
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "My Cart",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { (context as? CartActivity)?.finish() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black
            )
        )
        
        // Cart Items
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                CartItemCard(
                    item = item,
                    onDelete = { onDelete(item) },
                    onUpdateQuantity = { newQuantity -> onUpdateQuantity(item, newQuantity) }
                )
            }
        }
        
        // Order Summary and Checkout Button
        OrderSummary(
            cartItems = cartItems,
            onProceedToCheckout = onProceedToCheckout
        )
    }
}

@Composable
fun CartItemCard(
    item: CartItemModel,
    onDelete: () -> Unit,
    onUpdateQuantity: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = if (item.productImage.isNotEmpty()) item.productImage else "shoe1.png",
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.productName,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "NRs. ${item.productPrice}",
                    color = Color(0xFFFF6B35),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { 
                            if (item.quantity > 1) {
                                onUpdateQuantity(item.quantity - 1)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.remove),
                            contentDescription = "Decrease",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Text(
                        text = "${item.quantity}",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            // Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun OrderSummary(
    cartItems: List<CartItemModel>,
    onProceedToCheckout: () -> Unit
) {
    val itemTotal = cartItems.sumOf { it.productPrice * it.quantity }
    val discount = 0.0 // Fixed discount
    val deliveryFee = 200.0 // Fixed delivery fee
    val grandTotal = itemTotal + deliveryFee - discount
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Order Summary
            Text(
                text = "Order Summary",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Item Total",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "NRs. ${String.format("%.0f", itemTotal)}",
                    color = Color(0xFFFF6B35),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Discount",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "NRs. ${String.format("%.0f", discount)}",
                    color = Color(0xFFFF6B35),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery Fee",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "NRs. ${String.format("%.0f", deliveryFee)}",
                    color = Color(0xFFFF6B35),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Grand Total",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "NRs. ${String.format("%.0f", grandTotal)}",
                    color = Color(0xFFFF6B35),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Proceed to Checkout Button
            Button(
                onClick = onProceedToCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Proceed to Checkout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Checkout",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
