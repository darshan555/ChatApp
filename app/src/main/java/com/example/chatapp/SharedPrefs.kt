package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import java.util.ArrayList

class SharedPrefs {
    private lateinit var context: Context

    private fun getContext() = if (::context.isInitialized) context
    else throw RuntimeException("Please Initialize SharedPrefs")

    private var initialized = false
    private var listeners: ArrayList<SharedPreferences.OnSharedPreferenceChangeListener> =
        arrayListOf()

    private val pref by lazy {
        getInstance().getContext()
            .getSharedPreferences("ChatApp", Context.MODE_PRIVATE)
    }

    private fun edit(operation: SharedPreferences.Editor.() -> Unit) {
        val editor = getInstance().pref.edit()
        operation(editor)
        editor.apply()
    }
    companion object {
        fun init(context: Context) {
            if (getInstance().initialized.not()) {
                getInstance().context = context
                getInstance().initialized = true
            } else println("Already initialized")
        }

        @SuppressLint("StaticFieldLeak")
        private var instance: SharedPrefs? = null

        private fun getInstance(): SharedPrefs {
            return instance ?: synchronized(this) { SharedPrefs().also { instance = it } }
        }

        var isUserLogin: Boolean
            get() = getInstance().pref.getBoolean("isUserLogin", false)
            set(value) = getInstance().edit { putBoolean("isUserLogin", value) }
        var isSplashShow: Boolean
            get() = getInstance().pref.getBoolean("isSplashShow", false)
            set(value) = getInstance().edit { putBoolean("isSplashShow", value) }
        var setUserCredential: String?
            get() = getInstance().pref.getString("setUserCredential", null)
            set(value) = getInstance().edit { putString("setUserCredential", value) }

        /*var appPassword: String?
            get() = getInstance().pref.getString("appPassword",null)
            set(value) = getInstance().edit { putString("appPassword", value) }

        var shouldShowInitialBlockActivity: Boolean
            get() = getInstance().pref.getBoolean("shouldShowInitial", true)
            set(value) = getInstance().edit { putBoolean("shouldShowInitial", value) }*/
    }
}