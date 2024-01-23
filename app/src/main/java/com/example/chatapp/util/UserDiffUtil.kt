package com.example.chatapp.util

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.model.LastMessage
import com.example.chatapp.model.User

class UserDiffUtil(
    private val oldList: MutableList<User>,
    private val newList: MutableList<User>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser.userid == newUser.userid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser == newUser
    }
}