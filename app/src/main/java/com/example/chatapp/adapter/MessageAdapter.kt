package com.example.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chatapp.MyApplication
import com.example.chatapp.SharedPrefs
import com.example.chatapp.UserDiffUtil
import com.example.chatapp.databinding.ReceiveLayoutBinding
import com.example.chatapp.databinding.SentLayoutBinding
import com.example.chatapp.model.Message
import com.example.chatapp.util.MessageDffUtil

class MessageAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var oldList = emptyList<Message?>()
    companion object {
        const val VIEW_TYPE_SEND = 1
        const val VIEW_TYPE_RECEIVE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SEND -> SendViewHolder(
                SentLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_RECEIVE -> ReceiveViewHolder(
                ReceiveLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = oldList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_SEND -> (holder as SendViewHolder).bindSend(item)
            VIEW_TYPE_RECEIVE -> (holder as ReceiveViewHolder).bindReceive(item)
        }
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    override fun getItemViewType(position: Int): Int {
        return getViewType(oldList[position]?.currentUser ?: "")
    }

    inner class SendViewHolder(private val sBinding: SentLayoutBinding) :
        ViewHolder(sBinding.root) {

        fun bindSend(message: Message?) {
            if (message?.content?.let { isFirebaseStorageImageUrl(it) } == true) {
                Glide.with(MyApplication.applicationContext())
                    .load(message.content)
                    .into(sBinding.msgImg)
                sBinding.messageTV.visibility = View.GONE

            } else {
                sBinding.msgImg.visibility = View.GONE
                sBinding.messageTV.text = message?.content
            }
            sBinding.timeTV.text = message?.time?.let { UserDiffUtil.getTimeFromTimestamp(it) }
        }
    }

    inner class ReceiveViewHolder(private val rBinding: ReceiveLayoutBinding) :
        ViewHolder(rBinding.root) {

        fun bindReceive(message: Message?) {
            if (message?.content?.let { isFirebaseStorageImageUrl(it) } == true) {
                Glide.with(MyApplication.applicationContext())
                    .load(message.content)
                    .override(500, 500)
                    .centerCrop()
                    .into(rBinding.msgImg)
                rBinding.messageTV.visibility = View.GONE
            } else {
                rBinding.messageTV.text = message?.content
            }
            rBinding.timeTV.text = message?.time?.let { UserDiffUtil.getTimeFromTimestamp(it) }
        }
    }

    private fun getViewType(id: String): Int {
        return if (id == SharedPrefs.setUserCredential) {
            VIEW_TYPE_SEND
        } else {
            VIEW_TYPE_RECEIVE
        }
    }

    fun setData(newList: List<Message?>) {
        val diffUtil = MessageDffUtil(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldList = newList
        diffResult.dispatchUpdatesTo(this)
    }
    fun isFirebaseStorageImageUrl(text: String): Boolean {
        val firebaseStorageUrlPattern = Regex("https://firebasestorage\\.googleapis\\.com/.+\\?(alt=media&)?token=[a-zA-Z0-9_-]+")
        return firebaseStorageUrlPattern.matches(text)
    }
}