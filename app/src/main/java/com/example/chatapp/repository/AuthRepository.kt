package com.example.chatapp.repository

import com.example.chatapp.model.Users
import com.example.chatapp.util.Resource
import com.example.chatapp.view.LoginActivity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getUserId(): String
    fun createUser(user: Users): Flow<Resource<Boolean>>
    fun sendEmailVerification(email: String,password: String)
    suspend fun loginUser(username:String, password:String,activity: LoginActivity)
}