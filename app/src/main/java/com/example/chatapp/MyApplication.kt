package com.example.chatapp

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.example.chatapp.view.HomeActivity
import com.example.chatapp.view.MainActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        SharedPrefs.init(this)
    }

    companion object {
        private var instance: MyApplication? = null
        fun applicationContext(): MyApplication {
            return instance ?: throw IllegalStateException("MyApplication instance is null")
        }

        fun activityContext(): Activity? {
            return instance?.currentActivity
        }
    }

    var currentActivity: Activity? = null
}
