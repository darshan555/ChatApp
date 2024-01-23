package com.example.chatapp.util

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.model.Message

class MessageDffUtil(
    private val oldMessages:List<Message?>,
    private val newMessages:List<Message?>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldMessages.size
    }

    override fun getNewListSize(): Int {
        return newMessages.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldMessages[oldItemPosition]?.messageId
        val newUser = newMessages[newItemPosition]?.messageId
        return oldUser == newUser
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldMessages[oldItemPosition]
        val newUser = newMessages[newItemPosition]
        return oldUser == newUser
    }
}