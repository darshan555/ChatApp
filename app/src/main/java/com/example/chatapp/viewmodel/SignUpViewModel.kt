package com.example.chatapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.UserDiffUtil
import com.example.chatapp.model.Users
import com.example.chatapp.repository.AuthRepository
import com.example.chatapp.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    fun createUserProfile(username: String, password: String, email: String) {
        val model = Users(username, password, authRepository.getUserId(), email)
        firestore.collection("Users").whereEqualTo("username", username)
            .get().addOnSuccessListener {
                if (it.isEmpty) {
                    viewModelScope.launch {
                        authRepository.createUser(model).collectLatest {
                            when (it) {
                                is Resource.Loading -> {
                                    Log.d("TAG555", "Loading: ")
                                }

                                is Resource.Error -> {
                                    Log.d("TAG555", "Error: ")
                                }

                                is Resource.Success -> {
                                    sendEmailVarification(email, password)
                                }
                            }
                        }
                        UserDiffUtil.showToast("Please Verify Your Mail.")
                    }
                } else {
                    UserDiffUtil.showToast("Username Already Exists!")
                }
            }
    }

    private fun sendEmailVarification(email: String, password: String) {
        viewModelScope.launch {
            authRepository.sendEmailVerification(email, password)
        }
    }
}