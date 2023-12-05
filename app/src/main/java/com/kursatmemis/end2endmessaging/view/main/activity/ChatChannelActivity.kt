package com.kursatmemis.end2endmessaging.view.main.activity

import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyProperties
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.databinding.ActivityChatChannelBinding
import com.kursatmemis.end2endmessaging.model.Message
import com.kursatmemis.end2endmessaging.util.closeKeyboard
import com.kursatmemis.end2endmessaging.util.userfeedback.showToastMessage
import com.kursatmemis.end2endmessaging.view.adapter.MessageListAdapter
import com.kursatmemis.end2endmessaging.viewmodel.main.ChatChannelViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.inject.Inject

@AndroidEntryPoint
class ChatChannelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatChannelBinding
    private val chatChannelViewModel: ChatChannelViewModel by viewModels()
    private lateinit var receiverPhoneNumber: String
    @Inject
    lateinit var messageListAdapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiverPhoneNumber = intent.getStringExtra("receiverPhoneNumber")!!
        setupSenderButton()
        chatChannelViewModel.getMessageList(receiverPhoneNumber)

        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val clickable = !s.isNullOrBlank()
                binding.messageSenderFab.isClickable = clickable
                if (!clickable) {
                    makeButtonColorFaded(binding.messageSenderFab)
                } else {
                    binding.messageSenderFab.clearColorFilter()
                }
            }
        })

        binding.messageSenderFab.setOnClickListener {
            val messageText = binding.messageEditText.text.toString()
            
            val message = Message(
                messageText = messageText,
                senderId = Firebase.auth.currentUser!!.phoneNumber!!,
                receiverId = receiverPhoneNumber
            )

            chatChannelViewModel.saveMessageToFirestore(receiverPhoneNumber, message)
            closeKeyboard(this, this)
            binding.messageEditText.text.clear()
            scrollDownListView()
        }

        observeLiveData()

    }

    private fun observeLiveData() {
        chatChannelViewModel.messageList.observe(this) {
            val newMessageList = it
            it.sortBy {
                it.createdAt
            }
            messageListAdapter = MessageListAdapter(this@ChatChannelActivity, it)
            binding.messagesListView.adapter = messageListAdapter
            Log.w("mKm - chatChannel", "observed.")
            Log.w("mKm - chatChannel", it.toString())

        }

        chatChannelViewModel.messageServiceResult.observe(this) {
            val isSuccessful = it.isSuccessful
            if (!isSuccessful) {
                showToastMessage(
                    this,
                    "Messages could not be loaded!",
                    Toast.LENGTH_LONG
                )
            }
        }
    }

    private fun setupSenderButton() {
        makeButtonColorFaded(binding.messageSenderFab)
        binding.messageSenderFab.isClickable = false
        scrollDownListView()
    }

    private fun makeButtonColorFaded(messageSenderFab: FloatingActionButton) {
        messageSenderFab.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.gray
            ), PorterDuff.Mode.SRC_IN
        )
    }

    private fun scrollDownListView() {
        val lastItemPosition = messageListAdapter.count - 1
        if (lastItemPosition >= 0) {
            // `smoothScrollToPosition` kullanarak animasyonlu bir şekilde kaydırma yapabilirsiniz
            //binding.messagesListView.smoothScrollToPosition(lastItemPosition)

            // Eğer animasyonsuz bir kaydırma istiyorsanız, `setSelection` kullanabilirsiniz
            binding.messagesListView.setSelection(lastItemPosition)
        }
    }
}