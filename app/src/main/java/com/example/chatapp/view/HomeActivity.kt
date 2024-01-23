package com.example.chatapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.SharedPrefs
import com.example.chatapp.adapter.OtherUserAdapter
import com.example.chatapp.databinding.ActivityHomeBinding
import com.example.chatapp.model.LastMessage
import com.example.chatapp.model.User
import com.example.chatapp.viewmodel.HomePageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val homePageViewModel: HomePageViewModel by viewModels()
    private var myAdapter: OtherUserAdapter = OtherUserAdapter(this)
    var list = emptyList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking {
            list = SharedPrefs.setUserCredential?.let { homePageViewModel.getOtherUsers() }!!
            Log.d("TAG666", "HomeActivity: $list")
            binding.otherUserRV.apply {
                layoutManager = LinearLayoutManager(this@HomeActivity)
                adapter = myAdapter
                myAdapter.setData(list as MutableList<User>)
            }
            binding.loginUserTV.text = homePageViewModel.getUser().username
        }

        binding.logoutBTN.setOnClickListener {
            SharedPrefs.isUserLogin = false
            SharedPrefs.setUserCredential = null
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}