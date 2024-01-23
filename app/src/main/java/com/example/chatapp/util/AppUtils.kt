package com.example.chatapp.util

import com.example.chatapp.SharedPrefs

fun getChatIdFromSenderAndReceiver(senderId: String, receiverId: String): String {
    val list = arrayListOf(senderId, receiverId)
    list.sort()
    return list[0] + "_" + list[1]
}
fun trimReceiver(userid: String): String? {
    val loginUser = SharedPrefs.setUserCredential
    val removeUnderscore = userid.trim('_')
    return loginUser?.let { removeUnderscore.removePrefix(it).removeSuffix(loginUser) }
}
