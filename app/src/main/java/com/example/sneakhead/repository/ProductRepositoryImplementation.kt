package com.example.sneakhead.repository
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.sneakhead.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.util.concurrent.Executors

class ProductRepositoryImplementation : ProductRepository {

    val database = FirebaseDatabase.getInstance()
    val ref = database.reference.child("products")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dwc16gyvc",
            "api_key" to "781183576491768",
            "api_secret" to "EEJeYZ6hTUtUXaa-LqDxmixV9XM"
        )
    )

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                // ✅ Fix: Remove extensions from file name before upload
                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                // ✅ Run UI updates on the Main Thread
                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    override fun addProduct(
        productModel: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        var id = ref.push().key.toString()
        productModel.productId = id
        ref.child(id).setValue(productModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "product added")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "product deleted")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }

    override fun getAllProducts(callback: (List<ProductModel?>, Boolean, String) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var allProducts = mutableListOf<ProductModel>()
                if (snapshot.exists()) {
                    for (eachData in snapshot.children) {
                        var products = eachData.getValue(ProductModel::class.java)
                        if (products != null) {
                            allProducts.add(products)
                        }
                    }
                    callback(allProducts,true, "fetched")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback( emptyList(),false, error.message)
            }
        })

    }

    override fun getProductById(
        productId: String,
        callback: (ProductModel?, Boolean, String) -> Unit
    ) {

        ref.child(productId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var products = snapshot.getValue(ProductModel::class.java)
                    if (products != null) {
                        callback(products,true, "product fetched")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null,false, error.message)
            }
        })
    }



    override fun updateProduct(
        productId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productId).setValue(data).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "product updated")
            } else {
                callback(false, "${it.exception?.message}")

            }
        }
    }
}