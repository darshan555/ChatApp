package com.example.chatapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.SharedPrefs
import com.example.chatapp.adapter.OtherUserAdapter
import com.example.chatapp.databinding.ActivityHomeBinding
import com.example.chatapp.model.User
import com.example.chatapp.repository.UsersRepository
import com.example.chatapp.util.Resource
import com.example.chatapp.viewmodel.HomePageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val homePageViewModel: HomePageViewModel by viewModels()
    private var myAdapter: OtherUserAdapter = OtherUserAdapter(this)
    private var list = emptyList<User>()

    @Inject
    lateinit var usersRepository: UsersRepository
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
            getLastMessage()
        }
        binding.logoutBTN.setOnClickListener {
            SharedPrefs.isUserLogin = false
            SharedPrefs.setUserCredential = null
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getLastMessage() {
        val userIds = list.map { it.userId }
        userIds.forEach { userId ->
            lifecycleScope.launch {
                usersRepository.getLastMessageAndUnreadCount(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val data = resource.data
                            val user = list.find { it.userId == userId }
                            myAdapter.setLastMessage(user!!, data.lastMessage)
//                            myAdapter.setUnreadMessageCount(user.userid, data.unreadCount, data.lastMessage)
                        }

                        is Resource.Loading -> {
                        }

                        is Resource.Error -> {
                        }
                    }
                }
            }
        }
    }

}