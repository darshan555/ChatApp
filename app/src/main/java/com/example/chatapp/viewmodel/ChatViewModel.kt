package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.model.Message
import com.example.chatapp.repository.UsersRepository
import com.example.chatapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val usersRepository: UsersRepository) :
    ViewModel() {
    suspend fun sendMessage(message: Message) {
        viewModelScope.launch {
            usersRepository.sendMessage(message = message)
        }
    }

    private var _messages = MutableStateFlow<List<Message?>>(emptyList())
    val messages: StateFlow<List<Message?>> = _messages

    suspend fun getAllMessages(receiverId: String) =
        viewModelScope.launch {
            usersRepository.getMessage(receiverId).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _messages.value = it.data
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