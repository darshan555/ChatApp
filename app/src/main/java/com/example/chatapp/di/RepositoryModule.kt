package com.example.chatapp.di

import com.example.chatapp.reposImpl.AuthRepositoryImpl
import com.example.chatapp.reposImpl.UsersRepositoryImpl
import com.example.chatapp.repository.AuthRepository
import com.example.chatapp.repository.UsersRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideUserRepository(firebaseFireStore: FirebaseFirestore): UsersRepository =
        UsersRepositoryImpl(firebaseFireStore)

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseFireStore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): AuthRepository = AuthRepositoryImpl(firebaseFireStore, firebaseAuth)
}