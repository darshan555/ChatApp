package com.example.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatapp.databinding.HomeUserItemBinding
import com.example.chatapp.model.Message
import com.example.chatapp.model.User
import com.example.chatapp.util.UserDiffUtil
import com.example.chatapp.view.ChatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OtherUserAdapter(private val context: Context) :
    RecyclerView.Adapter<OtherUserAdapter.UserViewHolder>() {
    private var oldList = mutableListOf<User>()
    private val userLastMessages = mutableMapOf<String, Message?>()

    class UserViewHolder(val binding: HomeUserItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            HomeUserItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = oldList[position]
        holder.binding.usernameTV.text = item.username
        val lastMessage = userLastMessages[item.userId]
        holder.binding.lastMsgTV.text = lastMessage?.content ?:""
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userid", item.userId)
            intent.putExtra("username", item.username)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    fun setData(newList: MutableList<User>) {
        val diffUtil = UserDiffUtil(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldList = newList
        diffResult.dispatchUpdatesTo(this)
    }
    fun setLastMessage(user: User, lastMessage: Message?) {
        userLastMessages[user.userId] = lastMessage
        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }
    }
}


