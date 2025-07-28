package com.example.sneakhead.view

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sneakhead.R
import com.example.sneakhead.model.ProductModel
import com.example.sneakhead.repository.ProductRepositoryImplementation
import com.example.sneakhead.viewmodel.ProductViewModel

class AddProductActivity : ComponentActivity() {
    private var selectedImageUri: Uri? = null
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AddProductBody(
                onBackPressed = { finish() },
                onPickImage = { imagePickerLauncher.launch("image/*") },
                selectedImageUri = selectedImageUri
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBody(
    onBackPressed: () -> Unit,
    onPickImage: () -> Unit,
    selectedImageUri: Uri?
) {
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productDesc by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var uploadedImageUrl by remember { mutableStateOf("") }

    val repo = remember { ProductRepositoryImplementation() }
    val viewModel = remember { ProductViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add New Product",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image selection area
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2A2A2A))
                    .clickable { onPickImage() },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Image",
                            tint = Color(0xFFFF6B35),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add Image",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Text(
                text = "Tap to select product image",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Product Name", color = Color.White) },
                placeholder = { Text("Enter product name", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF6B35),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = productDesc,
                onValueChange = { productDesc = it },
                label = { Text("Product Description", color = Color.White) },
                placeholder = { Text("Enter product description", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF6B35),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Product Price", color = Color.White) },
                placeholder = { Text("Enter price", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF6B35),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Button(
                onClick = {
                    if (validateInputs(context, productName, productPrice, productDesc)) {
                        isLoading = true
                        val price = productPrice.toDoubleOrNull()
                        if (price == null) {
                            Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            return@Button
                        }
                        
                        // Upload image first if selected
                        if (selectedImageUri != null) {
                            Toast.makeText(context, "Uploading image...", Toast.LENGTH_SHORT).show()
                            viewModel.uploadImage(context, selectedImageUri!!) { imageUrl ->
                                if (imageUrl != null) {
                                    // Image uploaded successfully, now add product
                                    Toast.makeText(context, "Image uploaded! Adding product...", Toast.LENGTH_SHORT).show()
                                    val model = ProductModel(
                                        "",
                                        productName,
                                        price,
                                        productDesc,
                                        imageUrl
                                    )
                                    viewModel.addProduct(model) { success, message ->
                                        isLoading = false
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                        if (success) {
                                            activity?.finish()
                                        }
                                    }
                                } else {
                                    // Image upload failed, use default image
                                    Toast.makeText(context, "Image upload failed, using default image", Toast.LENGTH_SHORT).show()
                                    val model = ProductModel(
                                        "",
                                        productName,
                                        price,
                                        productDesc,
                                        "shoe1.png"
                                    )
                                    viewModel.addProduct(model) { success, message ->
                                        isLoading = false
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                        if (success) {
                                            activity?.finish()
                                        }
                                    }
                                }
                            }
                        } else {
                            // No image selected, use default image
                            Toast.makeText(context, "No image selected, using default image", Toast.LENGTH_SHORT).show()
                            val model = ProductModel(
                                "",
                                productName,
                                price,
                                productDesc,
                                "shoe1.png"
                            )
                            viewModel.addProduct(model) { success, message ->
                                isLoading = false
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (success) {
                                    activity?.finish()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Product",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun validateInputs(
    context: Context,
    productName: String,
    productPrice: String,
    productDesc: String
): Boolean {
    if (productName.isBlank()) {
        Toast.makeText(context, "Please enter product name", Toast.LENGTH_SHORT).show()
        return false
    }
    if (productPrice.isBlank() || productPrice.toDoubleOrNull() == null) {
        Toast.makeText(context, "Please enter valid price", Toast.LENGTH_SHORT).show()
        return false
    }
    if (productDesc.isBlank()) {
        Toast.makeText(context, "Please enter product description", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}

@Preview(showBackground = true)
@Composable
fun ProductBodyPreview() {
    AddProductBody(
        onBackPressed = {},
        onPickImage = {},
        selectedImageUri = null
    )
}