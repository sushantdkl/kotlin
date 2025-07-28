package com.example.sneakhead.view
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sneakhead.repository.ProductRepositoryImplementation
import com.example.sneakhead.viewmodel.ProductViewModel

class UpdateProductActivity : ComponentActivity() {
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
            UpdateProductBody(
                onPickImage = { imagePickerLauncher.launch("image/*") },
                selectedImageUri = selectedImageUri
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductBody(
    onPickImage: () -> Unit,
    selectedImageUri: Uri?
) {
    var pName by remember { mutableStateOf("") }
    var pPrice by remember { mutableStateOf("") }
    var pDesc by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var currentProductImage by remember { mutableStateOf("") }

    val repo = remember { ProductRepositoryImplementation() }
    val viewModel = remember { ProductViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity

    val productId: String? = activity?.intent?.getStringExtra("productId")

    val products = viewModel.products.observeAsState(initial = null)

    LaunchedEffect(Unit) {
        if (!productId.isNullOrEmpty()) {
            viewModel.getProductById(productId)
        }
    }

    // Update form fields when product data is loaded
    LaunchedEffect(products.value) {
        products.value?.let { product ->
            pName = product.productName
            pDesc = product.productDesc
            pPrice = product.productPrice.toString()
            currentProductImage = product.image
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Update Product",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
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
                } else if (currentProductImage.isNotEmpty() && currentProductImage != "shoe1.png") {
                    AsyncImage(
                        model = currentProductImage,
                        contentDescription = "Current Product Image",
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
                            text = "Change Image",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Text(
                text = "Tap to change product image",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
                OutlinedTextField(
                    value = pName,
                onValueChange = { pName = it },
                label = { Text("Product Name", color = Color.White) },
                placeholder = { Text("Enter product name", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF6B35),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pPrice,
                onValueChange = { pPrice = it },
                label = { Text("Product Price", color = Color.White) },
                placeholder = { Text("Enter price", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF6B35),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pDesc,
                onValueChange = { pDesc = it },
                label = { Text("Product Description", color = Color.White) },
                placeholder = { Text("Enter description", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF6B35),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                    if (validateInputs(context, pName, pPrice, pDesc)) {
                        isLoading = true
                        val price = pPrice.toDoubleOrNull()
                        if (price == null) {
                            Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            return@Button
                        }
                        
                        // Upload image first if selected
                        if (selectedImageUri != null) {
                            viewModel.uploadImage(context, selectedImageUri!!) { imageUrl ->
                                if (imageUrl != null) {
                                    // Image uploaded successfully, now update product
                                    val data = mutableMapOf<String, Any?>()
                                    data["productDesc"] = pDesc
                                    data["productPrice"] = price
                                    data["productName"] = pName
                                    data["image"] = imageUrl
                                    data["productId"] = productId
                                    viewModel.updateProduct(productId.toString(), data) { success, message ->
                                        isLoading = false
                                        if (success) {
                                            Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                                            // Navigate back to DashboardActivity
                                            val intent = Intent(context, DashboardActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "Failed to update: $message", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    // Image upload failed, use current image
                                    val data = mutableMapOf<String, Any?>()
                                    data["productDesc"] = pDesc
                                    data["productPrice"] = price
                                    data["productName"] = pName
                                    data["image"] = currentProductImage
                                    data["productId"] = productId
                                    viewModel.updateProduct(productId.toString(), data) { success, message ->
                                        isLoading = false
                                        if (success) {
                                            Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                                            // Navigate back to DashboardActivity
                                            val intent = Intent(context, DashboardActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "Failed to update: $message", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        } else {
                            // No new image selected, use current image
                            val data = mutableMapOf<String, Any?>()
                        data["productDesc"] = pDesc
                            data["productPrice"] = price
                        data["productName"] = pName
                            data["image"] = currentProductImage
                        data["productId"] = productId
                            viewModel.updateProduct(productId.toString(), data) { success, message ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                                    // Navigate back to DashboardActivity
                                    val intent = Intent(context, DashboardActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "Failed to update: $message", Toast.LENGTH_SHORT).show()
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
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delete Product",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    if (showDeleteDialog) {
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
                    text = "Are you sure you want to delete '$pName'? This action cannot be undone.",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!productId.isNullOrEmpty()) {
                            viewModel.deleteProduct(productId) { success, message ->
                                if (success) {
                                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                                    // Navigate back to DashboardActivity
                                    val intent = Intent(context, DashboardActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "Failed to delete: $message", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        showDeleteDialog = false
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
                    onClick = { showDeleteDialog = false }
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
fun UpdateProductPreview() {
    UpdateProductBody(
        onPickImage = {},
        selectedImageUri = null
    )
}