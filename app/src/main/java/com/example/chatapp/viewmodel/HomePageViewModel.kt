package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.model.Message
import com.example.chatapp.model.User
import com.example.chatapp.repository.UsersRepository
import com.example.chatapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(private val usersRepository: UsersRepository): ViewModel() {

    suspend fun getOtherUsers(): MutableList<User> {
        return usersRepository.getOtherUsers().toMutableList()
    }
    suspend fun getUser():User{
        return usersRepository.getUser()
    }
    private var _lastMessages = MutableStateFlow<String?>("")
    val lastMessages: StateFlow<String?> = _lastMessages


    suspend fun getLastMessages(receiverId: String) =
        viewModelScope.launch {
            usersRepository.getLastMsg(receiverId).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _lastMessages.value = it.data
                    }

                    is Resource.Loading -> {
//                    iMessagesView.showProgressBar()
                    }

                    is Resource.Error -> {
//                    iMessagesView.showError(it.message?:"An Error Occurred")
                    }
                }
            }
        }
}