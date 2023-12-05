package com.kursatmemis.end2endmessaging.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.databinding.ItemMessageBinding
import com.kursatmemis.end2endmessaging.model.Message
import javax.inject.Inject

class MessageListAdapter @Inject constructor(
    context: Context,
    private val messageList: ArrayList<Message>
) : ArrayAdapter<Message>(context, R.layout.item_message, messageList) {

    private lateinit var binding: ItemMessageBinding

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        //Aşağıdaki kodu uygulayınca mesajlar hatalı olarak gösteriliyor.
        /*if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            binding = ItemMessageBinding.inflate(layoutInflater, parent, false)
        } else {
            binding = ItemMessageBinding.bind(convertView)
        }*/

        val layoutInflater = LayoutInflater.from(context)
        binding = ItemMessageBinding.inflate(layoutInflater, parent, false)

        val message = messageList[position]
        val messageText = message.messageText

        if (message.senderId == Firebase.auth.currentUser!!.phoneNumber) {
            binding.leftChatLayout.visibility = View.INVISIBLE
            binding.rightChatTextview.text = messageText
        } else {
            binding.rightChatLayout.visibility = View.INVISIBLE
            binding.leftChatTextview.text = messageText
        }

        return binding.root
    }

    fun updateAdapter(newMessageList: ArrayList<Message>) {
        clear()
        addAll(newMessageList)
        notifyDataSetChanged()
    }

}