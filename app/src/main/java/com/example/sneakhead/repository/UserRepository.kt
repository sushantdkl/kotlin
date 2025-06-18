package com.example.sneakhead.repository

import com.example.sneakhead.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    //login
    //register
    //forgetPassword
    //UpdateProfile
    //getUserDetails
    //getCurrentUser
    //addUserToDatabase
    //logout

    fun login(
        email: String, password: String,
        callback: (Boolean, String) -> Unit
    )

    //authentication function
    fun register(
        email: String, password: String,
        callback: (Boolean, String, String) -> Unit
    )

    //databasefunction
    fun addUserToDatabase(
        userId: String, model: UserModel, callback: (Boolean, String) -> Unit
    )

    fun updateProfile(
        userId: String, data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    )

    fun forgetPassword(
        email: String, callback: (Boolean, String) -> Unit

    )


    fun getCurrentUser(): FirebaseUser?
    fun getUserById(userId: String, callback: (UserModel?, Boolean, String) -> Unit)

    fun logout(
        callback: (Boolean, String) -> Unit
    )

}