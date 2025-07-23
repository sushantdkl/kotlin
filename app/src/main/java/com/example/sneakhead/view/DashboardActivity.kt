package com.example.sneakhead.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sneakhead.model.ProductModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SneakerDashboard()
        }
    }
}

@Composable
fun SneakerDashboard() {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Men", "Women")
    val scrollState = rememberScrollState()

    val productList = remember {
        mutableStateListOf(
            ProductModel(
                productId = "1",
                productName = "HERO HONDA",
                productPrice = 12999.0,
                productDesc = "Premium street sneaker",
                image = "https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/99486859-0ff3-46b4-949b-2d16af2ad421/custom-nike-dunk-high-by-you-shoes.png"
            ),
            ProductModel(
                productId = "2",
                productName = "BULLET",
                productPrice = 24999.0,
                productDesc = "High performance running shoe",
                image = "https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/2a9f4e0f-f8d5-4c28-98ff-e7270b296e12/air-max-plus-shoes-LVTPP1.png"
            ),
            ProductModel(
                productId = "3",
                productName = "XPULSE",
                productPrice = 8999.0,
                productDesc = "Casual lifestyle sneaker",
                image = "https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/4f37fca8-6bce-43e7-ad07-f57ae3c13142/air-force-1-07-shoes-WrLlWX.png"
            ),
            ProductModel(
                productId = "4",
                productName = "AVENGER",
                productPrice = 18999.0,
                productDesc = "Sport performance sneaker",
                image = "https://sneakernews.com/wp-content/uploads/2021/11/adidas-Yeezy-Boost-350-v2-Blue-Tint-Restock-2022.jpg"
            ),
            // Adding more products to show scrolling
            ProductModel(
                productId = "5",
                productName = "TORNADO",
                productPrice = 15999.0,
                productDesc = "Urban lifestyle sneaker",
                image = "https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/99486859-0ff3-46b4-949b-2d16af2ad421/custom-nike-dunk-high-by-you-shoes.png"
            ),
            ProductModel(
                productId = "6",
                productName = "BLAZE",
                productPrice = 22999.0,
                productDesc = "High-top basketball shoe",
                image = "https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/2a9f4e0f-f8d5-4c28-98ff-e7270b296e12/air-max-plus-shoes-LVTPP1.png"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(scrollState)
    ) {
        // Header
        TopHeader()

        // Hero Section
        HeroSection()

        // Trending Section
        TrendingSection(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            products = productList
        )
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFFF6B35), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SH",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Icons
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* TODO: Search functionality */ }
            )
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* TODO: Cart functionality */ }
            )
        }
    }
}

@Composable
fun HeroSection() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // New Stock Text
        Text(
            text = "New Stock",
            color = Color(0xFFFF6B35),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        // Summer Collection Title
        Text(
            text = "Summer\nCollection",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Hero Section with Overlapping Shoe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            // Orange Circle Background
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                    .offset(y = 20.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFB84D),
                                Color(0xFFFF8C42),
                                Color(0xFFFF6B35)
                            ),
                            radius = 300f
                        ),
                        shape = CircleShape
                    )
            )

            // Shoe Image - Positioned to pop out
            AsyncImage(
                model = "https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/99486859-0ff3-46b4-949b-2d16af2ad421/custom-nike-dunk-high-by-you-shoes.png",
                contentDescription = "Featured Sneaker",
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.Center)
                    .offset(x = 15.dp, y = (-20).dp),
                contentScale = ContentScale.Fit
            )

            // Shadow effect behind shoe
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
                    .offset(x = 25.dp, y = 30.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            radius = 200f
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Pagination Dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            width = if (index == 1) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .background(
                            color = if (index == 1) Color(0xFFFF6B35) else Color.Gray,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun TrendingSection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    products: List<ProductModel>
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Trending Now Title
        Text(
            text = "Trending Now",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Category Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(categories) { category ->
                CategoryPill(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelected(category) }
                )
            }
        }

        // Products Grid - Now with fixed height for scrolling
        Column {
            repeat((products.size + 1) / 2) { rowIndex ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    // First product in row
                    val firstIndex = rowIndex * 2
                    if (firstIndex < products.size) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(products[firstIndex])
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Second product in row
                    val secondIndex = rowIndex * 2 + 1
                    if (secondIndex < products.size) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(products[secondIndex])
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
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
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(
                color = if (isSelected) Color(0xFFFF6B35) else Color(0xFF2A2A2A),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProductCard(product: ProductModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { /* TODO: Navigate to product detail */ }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Product Image Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            Color(0xFF2A2A2A),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Product Name
                Text(
                    text = product.productName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                // Price Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "₹${product.productPrice.toInt()}",
                        color = Color(0xFFFF6B35),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₹${(product.productPrice * 1.2).toInt()}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                }
            }

            // Brand Logo (Top Left)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
                    .background(Color(0xFFFF6B35), RoundedCornerShape(6.dp))
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SH",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Discount Badge (Top Right)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "20% OFF",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Add Button (Bottom Right)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
                    .align(Alignment.BottomEnd)
                    .clickable { /* TODO: Add to cart */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to Cart",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSneakerDashboard() {
    SneakerDashboard()
}