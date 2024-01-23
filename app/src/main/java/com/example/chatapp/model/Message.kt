package com.example.chatapp.model

data class Message(
    val content: String = "",
    val time: Long = 0,
    val messageId: String = "",
    val currentUser: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val chatId: String = "",
)
