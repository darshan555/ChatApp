package com.example.chatapp.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.SharedPrefs
import com.example.chatapp.adapter.ChatDateDecoration
import com.example.chatapp.adapter.MessageAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.model.Message
import com.example.chatapp.util.getChatIdFromSenderAndReceiver
import com.example.chatapp.viewmodel.ChatViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private var myAdapter: MessageAdapter = MessageAdapter()
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var receiverId:String = ""
    private val todayCalendar = Calendar.getInstance()
    private val messageCalendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        receiverId = intent.getStringExtra("userid").toString()
        val username = intent.getStringExtra("username")


        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                        uri ->
                    uploadImageToStorage(uri)
                }
            }
        }

        binding.username.text = username

        lifecycleScope.launch {
            chatViewModel.getAllMessages(receiverId)
            chatViewModel.messages.onEach { data ->
                withContext(Dispatchers.Main) {
                    val sortedMessages = data.sortedBy { it?.time }
                    myAdapter.setData(sortedMessages)
                    binding.messageRecView.adapter = myAdapter
                    binding.messageRecView.layoutManager = LinearLayoutManager(this@ChatActivity)
                    binding.messageRecView.scrollToPosition(myAdapter.itemCount - 1)
                    val itemDecoration = ChatDateDecoration(
                        0,
                        getSectionCallback(sortedMessages as List<Message>)
                    )
                    binding.messageRecView.addItemDecoration(itemDecoration)
                }
            }.launchIn(lifecycleScope)
        }

        binding.sendBTN.setOnClickListener {
            lifecycleScope.launch {
                val message = Message(
                    content = binding.messageET.text.toString(),
                    time = System.currentTimeMillis(),
                    messageId = "",
                    currentUser = SharedPrefs.setUserCredential ?: "",
                    senderId = SharedPrefs.setUserCredential ?: "",
                    receiverId = receiverId,
                    chatId = getChatIdFromSenderAndReceiver(
                        SharedPrefs.setUserCredential ?: "",
                        receiverId
                    )
                )
                chatViewModel.sendMessage(message)
            }
            binding.messageET.text.clear()
        }

        binding.galleryIMG.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            galleryLauncher.launch(gallery)
        }
    }

    private fun uploadImageToStorage(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.continueWith { task->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task->
            if (task.isSuccessful){
                imageRef.downloadUrl.addOnSuccessListener {uri->
                    val downloadUri = uri.toString()
                    saveImageToFireStore(downloadUri.toString())
                }
            }
        }

    }

    private fun saveImageToFireStore(downloadUrl: String) {
        lifecycleScope.launch {
            val message = Message(
                content = downloadUrl,
                time = System.currentTimeMillis(),
                messageId = "",
                currentUser = SharedPrefs.setUserCredential ?: "",
                senderId = SharedPrefs.setUserCredential ?: "",
                receiverId = receiverId,
                chatId = getChatIdFromSenderAndReceiver(
                    SharedPrefs.setUserCredential ?: "",
                    receiverId
                )
            )
            chatViewModel.sendMessage(message)
        }
    }

    private fun getSectionCallback(messages: List<Message>): ChatDateDecoration.SectionCallback {
        return object : ChatDateDecoration.SectionCallback {
            override fun isSection(position: Int): Boolean {
                if (messages.isEmpty() || position >= messages.size || position == 0) {
                    return true
                }
                val currentMessage = messages[position]
                val previousMessage = messages[position - 1]

                return !isSameDay(currentMessage.time, previousMessage.time)
            }

            override fun getSectionHeader(position: Int): CharSequence? {
                if (messages.isEmpty() || position >= messages.size) {
                    return null
                }

                val messageTime = messages[position].time
                if (isSameDay(messageTime, System.currentTimeMillis())) {
                    return "Today"
                } else if (isSameDay(messageTime, System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS)) {
                    return "Yesterday"
                }
                return dateFormat.format(Date(messageTime))
            }

        }
    }
    private fun isSameDay(time1: Long, time2: Long): Boolean {
        todayCalendar.timeInMillis = time1
        messageCalendar.timeInMillis = time2

        return todayCalendar.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR) &&
                todayCalendar.get(Calendar.DAY_OF_YEAR) == messageCalendar.get(Calendar.DAY_OF_YEAR)
    }
}
