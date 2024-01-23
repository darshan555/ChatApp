package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chatapp.model.User
import com.example.chatapp.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(private val usersRepository: UsersRepository): ViewModel() {

    suspend fun getOtherUsers(): MutableList<User> {
        return usersRepository.getOtherUsers().toMutableList()
    }
    suspend fun getUser():User{
        return usersRepository.getUser()
    }
}