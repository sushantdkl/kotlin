package com.example.sneakhead.view

import android.os.Bundle
import android.content.Intent
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import com.example.sneakhead.model.ProductModel
import com.example.sneakhead.model.CartItemModel
import com.example.sneakhead.repository.ProductRepositoryImplementation
import com.example.sneakhead.repository.CartRepositoryImplementation
import com.example.sneakhead.viewmodel.ProductViewModel
import com.example.sneakhead.viewmodel.CartViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import com.example.sneakhead.R
import kotlinx.coroutines.delay

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductDashboard()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh products when returning to dashboard
        // This ensures updated products are shown after editing
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDashboard() {
    val context = LocalContext.current
    
    // ✅ Fix: Use remember to prevent recreating ViewModels on every recomposition
    val productViewModel = remember { ProductViewModel(ProductRepositoryImplementation()) }
    val cartViewModel = remember { CartViewModel(CartRepositoryImplementation()) }
    
    val allProducts by productViewModel.allProducts.observeAsState(emptyList())
    val productLoading by productViewModel.loading.observeAsState(false)
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductModel?>(null) }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    
    // ✅ Fix: Get current user email once and remember it
    val currentUserEmail = remember {
        val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
        sharedPreferences.getString("email", "") ?: ""
    }

    // ✅ Fix: Use derivedStateOf to optimize filtering
    val filteredProducts by remember {
        derivedStateOf {
            allProducts.filterNotNull().filter { product ->
                val matchesSearch = searchQuery.isEmpty() || 
                    product.productName.contains(searchQuery, ignoreCase = true) ||
                    product.productDesc.contains(searchQuery, ignoreCase = true)
                val matchesCategory = selectedCategory == "All" || 
                    (selectedCategory == "Men" && product.productName.contains("men", ignoreCase = true)) ||
                    (selectedCategory == "Women" && product.productName.contains("women", ignoreCase = true))
                matchesSearch && matchesCategory
            }
        }
    }

    // ✅ Fix: Load products when the component is first created
    LaunchedEffect(Unit) {
        productViewModel.getAllProduct()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SNEAKHEAD",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "PREMIUM SHOE STORE",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showSearchBar = !showSearchBar
                        if (!showSearchBar) {
                            searchQuery = ""
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        // Refresh products to show updated images
                        productViewModel.getAllProduct()
                        Toast.makeText(context, "Refreshing products...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        context.startActivity(Intent(context, CartActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        context.startActivity(Intent(context, AddProductActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Product",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        // Clear SharedPreferences and logout
                        context.getSharedPreferences("User", Context.MODE_PRIVATE).edit().clear().apply()
                        
                        // Navigate back to LoginActivity
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.logout),
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            // Search Bar
            if (showSearchBar) {
                item {
                    SearchBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it }
                    )
                }
            }
            
            // Hero Section
            item {
                HeroSection()
            }
            
            // Trending Section with Categories
            item {
                TrendingSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }
            
            // Products Grid
            item {
                if (productLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF6B35)
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts) { product ->
                            ProductCard(
                                product = product,
                                onProductClick = {
                                    val intent = Intent(context, UpdateProductActivity::class.java)
                                    intent.putExtra("productId", product.productId)
                                    context.startActivity(intent)
                                },
                                onDeleteClick = {
                                    productToDelete = product
                                    showDeleteDialog = true
                                },
                                onEditClick = {
                                    val intent = Intent(context, UpdateProductActivity::class.java)
                                    intent.putExtra("productId", product.productId)
                                    context.startActivity(intent)
                                },
                                onAddToCart = {
                                    val cartItem = CartItemModel(
                                        cartItemId = "",
                                        userId = currentUserEmail,
                                        productId = product.productId,
                                        productName = product.productName,
                                        productPrice = product.productPrice,
                                        productImage = product.image,
                                        quantity = 1
                                    )
                                    cartViewModel.addCartItem(cartItem) { success, message ->
                                        if (success) {
                                            Toast.makeText(context, "Added to cart successfully!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to add to cart: $message", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Delete Dialog
    if (showDeleteDialog && productToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Product",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${productToDelete!!.productName}'? This action cannot be undone.",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        productViewModel.deleteProduct(productToDelete!!.productId) { success, message ->
                            if (success) {
                                Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                                // Refresh the product list
                                productViewModel.getAllProduct()
                            } else {
                                Toast.makeText(context, "Failed to delete: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showDeleteDialog = false
                        productToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteDialog = false
                        productToDelete = null
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.Gray
                    )
                }
            },
            containerColor = Color(0xFF1A1A1A),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search products...", color = Color.Gray) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFFF6B35),
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color(0xFFFF6B35),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun HeroSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B35)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Premium Shoes",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discover the latest collection of premium footwear",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun TrendingSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Categories",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listOf("All", "Men", "Women")) { category ->
                CategoryPill(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryPill(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFF6B35) else Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = category,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    onProductClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Logo and action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(20.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = "Edit",
                            tint = Color.Blue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Product Image with optimized loading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (product.image.isNotEmpty() && product.image != "noimage.png" && product.image.startsWith("http")) {
                    // Show Cloudinary image with optimized loading
                    AsyncImage(
                        model = product.image,
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        // ✅ Fix: Add caching and error handling
                        error = painterResource(id = R.drawable.noimage),
                        placeholder = painterResource(id = R.drawable.noimage)
                    )
                } else {
                    // Show local image or fallback
                    Image(
                        painter = painterResource(id = R.drawable.noimage),
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Product Details
            Text(
                text = product.productName,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "NRs. ${product.productPrice}",
                color = Color(0xFFFF6B35),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to Cart",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}