package com.example.chatapp.reposImpl

import android.content.Intent
import com.example.chatapp.UserDiffUtil
import com.example.chatapp.SharedPrefs
import com.example.chatapp.model.Users
import com.example.chatapp.repository.AuthRepository
import com.example.chatapp.util.Resource
import com.example.chatapp.view.HomeActivity
import com.example.chatapp.view.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
    )
    : AuthRepository {
    override fun getUserId(): String {
        return firebaseFireStore.collection("Users").document().id
    }

    override fun createUser(user: Users): Flow<Resource<Boolean>> = callbackFlow {
        try {
            trySend(Resource.Loading)
            firebaseFireStore.collection("Users").add(user)
                .addOnSuccessListener {
                    trySend(Resource.Success(true))
                    close()
                }
                .addOnFailureListener { e ->
                    trySend(Resource.Error("User Profile Creation Failed due to ${e.localizedMessage}"))
                    close()
                }
        } catch (e: Exception) {
            trySend(Resource.Error("User Profile Creation Failed due to ${e.localizedMessage}"))
            close()
        }
        awaitClose()
    }

    override fun sendEmailVerification(email: String,password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    sentVerificationLink()
                }
            }
    }

    private fun sentVerificationLink() {
        val user: FirebaseUser?= firebaseAuth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    UserDiffUtil.showToast("Verification email sent.")
                }else{
                    UserDiffUtil.showToast("Failed to send verification email.")
                }
            }
    }

    override suspend fun loginUser(username: String, password: String, activity: LoginActivity) {
        try {
            val querySnapshot = firebaseFireStore.collection("Users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                val emailQuerySnapshot = firebaseFireStore.collection("Users")
                    .whereEqualTo("email", username)
                    .whereEqualTo("password", password)
                    .get()
                    .await()

                if (!emailQuerySnapshot.isEmpty) {
                    val userDoc = emailQuerySnapshot.documents.first()
//                    val email = userDoc.getString("email")
                    val id = userDoc.getString("userid")
                    firebaseAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener { authResult ->
                            if (authResult.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                if (user != null && user.isEmailVerified) {
                                    UserDiffUtil.showToast("Login successful.")
                                    SharedPrefs.isUserLogin = true
                                    SharedPrefs.setUserCredential = id
                                    val intent = Intent(activity, HomeActivity::class.java)
                                    activity.startActivity(intent)
                                    activity.finish()
                                } else {
                                    UserDiffUtil.showToast("Please verify your email.")
                                }
                            } else {
                                UserDiffUtil.showToast("Authentication failed.")
                            }
                        }
                } else {
                    UserDiffUtil.showToast("Username or email not found.")
                }
            } else {
                val userDoc = querySnapshot.documents.first()
                val email = userDoc.getString("email")
                val id = userDoc.getString("userid")
                if (email != null) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { authResult ->
                            if (authResult.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                if (user != null && user.isEmailVerified) {
                                    UserDiffUtil.showToast("Login successful.")
                                    SharedPrefs.isUserLogin = true
                                    SharedPrefs.setUserCredential = id
                                    val intent = Intent(activity, HomeActivity::class.java)
                                    activity.startActivity(intent)
                                    activity.finish()

                                } else {
                                    UserDiffUtil.showToast("Please verify your email.")
                                }
                            } else {
                                UserDiffUtil.showToast("Authentication failed.")
                            }
                        }
                }
            }
        } catch (e: Exception) {
            UserDiffUtil.showToast("An error occurred.")
        }
    }


}