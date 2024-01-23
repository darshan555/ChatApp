package com.example.chatapp.reposImpl

import android.util.Log
import com.example.chatapp.Constant
import com.example.chatapp.SharedPrefs
import com.example.chatapp.model.Message
import com.example.chatapp.model.User
import com.example.chatapp.repository.UsersRepository
import com.example.chatapp.util.Resource
import com.example.chatapp.util.getChatIdFromSenderAndReceiver
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(private val firebaseFireStore: FirebaseFirestore) :
    UsersRepository {
    override suspend fun getOtherUsers(): MutableList<User> = withContext(Dispatchers.IO) {
        val userList = mutableListOf<User>()
        try {
            val querySnapshot = firebaseFireStore.collection("Users").get().await()
            for (document in querySnapshot) {
                val username= document.get("username").toString()
                val userId = document.get("userid").toString()
                if (userId != SharedPrefs.setUserCredential) {
                    userList += User(username = username, userid = userId)
                }
            }
        } catch (exception: Exception) {
            Log.e("TAG555", "Error getting users", exception)
        }
        return@withContext userList
    }

    override suspend fun sendMessage(message: Message) {
        try {
            val chatId = message.chatId
            val chatReference = firebaseFireStore.collection(Constant.CHAT_COLLECTION).document(chatId)
            chatReference.set(mapOf("lastMessage" to message.content), SetOptions.merge())
                .addOnSuccessListener {
                    Log.e("TAG111", "sendMessage: lastMessage updated or created successfully")
                }.addOnFailureListener {
                    Log.e("TAG111", "sendMessage: failed to update or create lastMessage - ${it.message}")
                }
            chatReference.collection(chatId).add(message)
                .addOnSuccessListener {
                    Log.e("TAG111", "sendMessage: message saved")
                }.addOnFailureListener {
                    Log.e("TAG111", "sendMessage: message failed to save")
                }

        } catch (e: Exception) {
            Log.e("TAG111", "sendMessage: exception - ${e.message}")
        }
    }

    override suspend fun getUser(): User = withContext(Dispatchers.IO) {
        var user = User("default_username", "default_userid") // Initialize with default values
        try {
            val querySnapshot = firebaseFireStore.collection("Users").get().await()
            for (document in querySnapshot) {
                val username = document.getString("username")
                val userId = document.getString("userid")
                if (userId == SharedPrefs.setUserCredential) {
                    user = userId?.let { username?.let { it1 -> User(username = it1, userid = it) } } ?: user
                    break
                }
            }
        } catch (exception: Exception) {
            Log.e("TAG555", "Error getting users", exception)
        }
        user
    }

    override suspend fun getMessage(receiverId: String): Flow<Resource<List<Message?>>> =
        callbackFlow {
            val senderId = SharedPrefs.setUserCredential ?: ""
            val chatId = getChatIdFromSenderAndReceiver(senderId, receiverId)
            firebaseFireStore.collection(Constant.CHAT_COLLECTION).document(chatId)
                .collection(chatId).addSnapshotListener { value, _ ->
                    Log.e("TAG111", "${value?.documents}")
                    val list = value?.documents?.map { documentSnapshot ->
                        documentSnapshot.toObject(Message::class.java)
                    } ?: emptyList()
                    trySend(Resource.Success(list))
                }
            awaitClose()
        }

    override suspend fun getLastMsg(receiverId: String): Flow<Resource<String?>> =
        callbackFlow {
            val senderId = SharedPrefs.setUserCredential ?: ""
            val chatId = getChatIdFromSenderAndReceiver(senderId, receiverId)
            try {
                val snapShot = firebaseFireStore.collection(Constant.CHAT_COLLECTION).document(chatId).get().await()
                if (snapShot.exists()) {
                    val lastMessage = snapShot.getString("lastMessage")
                    trySend(Resource.Success(lastMessage))
                } else {
                    trySend(Resource.Error("Chat not found"))
                }
            } catch (e: Exception) {
                trySend(Resource.Error("Failed to retrieve last message - ${e.message}"))
            }

            awaitClose()
        }

}