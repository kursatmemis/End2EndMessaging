package com.kursatmemis.end2endmessaging.view.main.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kursatmemis.end2endmessaging.databinding.FragmentChatBinding
import com.kursatmemis.end2endmessaging.util.userfeedback.closeProgressBar
import com.kursatmemis.end2endmessaging.view.BaseFragment
import com.kursatmemis.end2endmessaging.view.adapter.ChatAdapter
import com.kursatmemis.end2endmessaging.view.main.activity.ChatChannelActivity
import com.kursatmemis.end2endmessaging.viewmodel.main.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>() {

    private val chatViewModel: ChatViewModel by viewModels()
    @Inject lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel.getChatList()
        Log.w("mKm - calisti", "calisti")
    }

    override fun onStart() {
        super.onStart()

        Log.w("mKm - calisti", "calisti - onStart")
    }

    override fun createBindingObject(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatBinding? {
        return FragmentChatBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        binding.chatListView.adapter = chatAdapter

        binding.chatListView.setOnItemClickListener { parent, view, position, id ->
            val chat = chatAdapter.getChatList()[position]
            val messageTo = chat.phoneNumber!!
            goToChatChannelActivityWithReceiverPhoneNumber(messageTo)
           
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        chatViewModel.chatList.observe(viewLifecycleOwner) {
            val newChatList = it
            chatAdapter.updateAdapter(newChatList)
            closeProgressBar(binding.progressBar)
        }
    }

    private fun goToChatChannelActivityWithReceiverPhoneNumber(messageTo: String) {
        val intentToChatChannelActivity = Intent(requireActivity(), ChatChannelActivity::class.java)
        intentToChatChannelActivity.putExtra("receiverPhoneNumber", messageTo)
        startActivity(intentToChatChannelActivity)
    }

}