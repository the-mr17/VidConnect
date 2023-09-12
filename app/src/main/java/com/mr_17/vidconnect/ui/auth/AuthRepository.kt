package com.mr_17.vidconnect.ui.auth

import com.google.firebase.auth.FirebaseUser
import com.mr_17.vidconnect.data.Resource

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signup(name: String, email: String, password: String, confirmPassword: String): Resource<FirebaseUser>
    suspend fun forgotPassword(email: String): Resource<String>
    fun logout()
}