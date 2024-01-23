package com.example.chatapp.repository

import com.example.chatapp.model.LastMessage
import com.example.chatapp.model.Message
import com.example.chatapp.model.User
import com.example.chatapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun getOtherUsers(): List<User>
    suspend fun sendMessage(message: Message)
    suspend fun getUser(): User
    suspend fun getMessage(receiverId: String): Flow<Resource<List<Message?>>>
    suspend fun getLastMessageAndUnreadCount(receiverId: String): Flow<Resource<LastMessage>>
}