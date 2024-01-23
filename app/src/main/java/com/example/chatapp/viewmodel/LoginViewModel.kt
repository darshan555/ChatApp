package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chatapp.repository.AuthRepository
import com.example.chatapp.view.LoginActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    suspend fun loginUser(username: String, password: String, activity: LoginActivity) {
        authRepository.loginUser(username, password, activity)
    }
}