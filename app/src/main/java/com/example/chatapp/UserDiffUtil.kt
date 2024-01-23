package com.example.chatapp

import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UserDiffUtil {
    fun showToast(msg:String){
        Toast.makeText(MyApplication.applicationContext(), msg, Toast.LENGTH_SHORT).show()
    }
    fun getTimeFromTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }
}