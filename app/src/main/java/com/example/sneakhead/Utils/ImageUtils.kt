package com.example.sneakhead.Utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class ImageUtils(
    private val activity: Activity,
    private val registryOwner: ActivityResultRegistryOwner
) {

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var onImageSelectedCallback: ((Uri?) -> Unit)? = null

    fun registerLaunchers(onImageSelected: (Uri?) -> Unit) {
        onImageSelectedCallback = onImageSelected

        // Register launcher for selecting image from gallery
        galleryLauncher = registryOwner.activityResultRegistry.register(
            "galleryLauncher", ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.d("ImageUtils", "Gallery result received: $result")
            if (result != null && result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    Log.d("ImageUtils", "Image URI: $uri")
                    onImageSelectedCallback?.invoke(uri)
                } else {
                    Log.e("ImageUtils", "Result returned but URI is null")
                    onImageSelectedCallback?.invoke(null)
                }
            } else {
                Log.e("ImageUtils", "Image selection cancelled or failed")
                onImageSelectedCallback?.invoke(null)
            }
        }

        // Register launcher for requesting permission
        permissionLauncher = registryOwner.activityResultRegistry.register(
            "permissionLauncher", ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            Log.d("ImageUtils", "Permission granted: $isGranted")
            if (isGranted) {
                openGallery()
            } else {
                Log.e("ImageUtils", "Permission denied")
            }
        }
    }

    fun launchImagePicker() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        Log.d("ImageUtils", "Checking permission for: $permission")

        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d("ImageUtils", "Permission not granted, requesting...")
            permissionLauncher.launch(permission)
        } else {
            Log.d("ImageUtils", "Permission already granted, opening gallery...")
            openGallery()
        }
    }

    private fun openGallery() {
        Log.d("ImageUtils", "Opening gallery...")
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        galleryLauncher.launch(intent)
    }
}
