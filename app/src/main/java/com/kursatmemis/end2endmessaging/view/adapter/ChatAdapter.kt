package com.kursatmemis.end2endmessaging.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.databinding.ItemChatBinding
import com.kursatmemis.end2endmessaging.model.Chat
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStorageRepository
import com.kursatmemis.end2endmessaging.util.buildProgressDrawable
import com.kursatmemis.end2endmessaging.util.downloadFromUrl
import javax.inject.Inject

class ChatAdapter @Inject constructor(
    context: Context,
    private val chatList: ArrayList<Chat>,
    private val firebaseStorageRepository: FirebaseStorageRepository
) : ArrayAdapter<Chat>(context, R.layout.item_chat, chatList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val binding: ItemChatBinding

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            binding = ItemChatBinding.inflate(layoutInflater, parent, false)
        } else {
            binding = ItemChatBinding.bind(convertView)
        }

        val chat = chatList[position]
        val phoneNumber = chat.phoneNumber
        firebaseStorageRepository.getProfilePictureUri(phoneNumber!!) {
            val imageUrl: String?
            if (it == null) {
                imageUrl = null
            } else {
                imageUrl = it.toString()
            }
            binding.profileImage.downloadFromUrl(imageUrl, buildProgressDrawable(context))
        }

        binding.fullNameTextView.text = chat.phoneNumber
        binding.lastMessageTextView.text = chat.lastMessage

        return binding.root
    }

    fun updateAdapter(newChatList: ArrayList<Chat>) {
        clear()
        addAll(newChatList)
        notifyDataSetChanged()
    }

    fun getChatList() : ArrayList<Chat> {
        return chatList
    }
}