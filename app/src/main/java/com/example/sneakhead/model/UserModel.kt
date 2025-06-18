package com.example.sneakhead.model

import android.location.Address

data class UserModel(
    val userID: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val gender: String = "",
    val dob: String = "",
    val country: String = ""
)

