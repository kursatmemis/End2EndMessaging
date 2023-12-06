package com.kursatmemis.end2endmessaging.view.main.activity

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
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
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.inject.Inject

@AndroidEntryPoint
class ChatChannelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatChannelBinding
    private val chatChannelViewModel: ChatChannelViewModel by viewModels()
    private lateinit var receiverPhoneNumber: String
    @Inject
    lateinit var messageListAdapter: MessageListAdapter
    var receiverPublicKey: String = ""
    var receiverPrivateKey: String = ""
    var senderPublicKey: String = ""
    var senderPrivateKey: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiverPhoneNumber = intent.getStringExtra("receiverPhoneNumber")!!
        chatChannelViewModel.getReceiverPublicKey(receiverPhoneNumber)
        chatChannelViewModel.getReceiverPrivateKey(receiverPhoneNumber)
        chatChannelViewModel.getSenderPrivateKey(Firebase.auth.currentUser!!.phoneNumber!!)
        chatChannelViewModel.getSenderPublicKey(Firebase.auth.currentUser!!.phoneNumber!!)


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
            val sifrelenmisMesajForReceiver = encryptMessage(messageText, receiverPublicKey)
            val sifrelenmisMesajForSender = encryptMessage(messageText, senderPublicKey)

            val message = Message(
                messageTextForReceiver = sifrelenmisMesajForReceiver,
                sifrelenmisMesajForSender,
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

    fun encryptMessage(message: String, publicKeyString: String): String {
        // Public key string'i PublicKey nesnesine dönüştürme
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyString)
        //val publicKeyBytes = Base64.getDecoder().decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp6JviTMazc9tjk4AuEreVCXqJu9xwLGXSb7druhysN133odmseE87YHRtH2WX/+BSkEaappWCdlYLs1r8rzqhCrdhxzIATC5OFnUJjVcpyLuRr3EKZ6S+hp880u39Wfi6zGyrzONINcScXzHimCTM7o1rxqo0U1I4Gd5vVmkPlGTEyum7Ott3otaaqrD8doTv9CZdtfCtG274fo6pwljdIJV6/2XYLvddkpJPaOcx7F/+gLLctwWudgSxbp0irx+EvVtGuoSEfPhr/68lTyeifEIGdJYBjjpbFPlFPtEhkJMT8jyvFMlU58sqwGteps5ODL/JFd5VdUDfMYk8YApDQIDAQAB")
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey: PublicKey = keyFactory.generatePublic(keySpec)

        // Cipher oluşturma ve şifreleme işlemi
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(message.toByteArray())

        // Şifreli mesajı Base64 formatında string'e çevirme
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decryptMessage(encryptedMessage: String, privateKeyString: String): String {
        // Private key string'i PrivateKey nesnesine dönüştürme
        val privateKeyBytes = Base64.getDecoder().decode(privateKeyString)
        //val privateKeyBytes = Base64.getDecoder().decode("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCnom+JMxrNz22OTgC4St5UJeom73HAsZdJvt2u6HKw3Xfeh2ax4TztgdG0fZZf/4FKQRpqmlYJ2VguzWvyvOqEKt2HHMgBMLk4WdQmNVynIu5GvcQpnpL6GnzzS7f1Z+LrMbKvM40g1xJxfMeKYJMzujWvGqjRTUjgZ3m9WaQ+UZMTK6bs623ei1pqqsPx2hO/0Jl218K0bbvh+jqnCWN0glXr/Zdgu912Skk9o5zHsX/6Asty3Ba52BLFunSKvH4S9W0a6hIR8+Gv/ryVPJ6J8QgZ0lgGOOlsU+UU+0SGQkxPyPK8UyVTnyyrAa16mzk4Mv8kV3lV1QN8xiTxgCkNAgMBAAECggEAAOTx70VZ2lCBAGlfKPUnmjlJBSUeJdZaP3GbBZVrE676ezcJNeTE5oorZZBv4ZLiDtSuvQfRLbqyIWAZJJVIZHjFOdJmxl9P9fYskarEtmhm5Tf9TXqxf7Ie+wf+0xMq+2v3qlKUS6/1veb8ThPR2/3/2EgYCFrmhjBrcPtqY5zyAkzdOTgt0w69AHyMJZWpOZCeen2aUk0li7xo/PempcFcrbiN+xAtCn01tbhsIxz6k2aw4hP6nxRqLE5kj2emzpk4Oxp70r4cy/YGUndfpZ2DgKvOJDqc2LNCQbJKniA5ERuxwIcRO1LZThfooUykv4CSStQPDuBgyziQX/QU0QKBgQDX2xcn7U9ZGJO49Fg1GYJLEgj/uaTrJPa0gfzZkRLBz3eTzhcxpaKbbapZMRyziJGPyXu5teEGieN73e2TnBtl+SKaS/G8aTEHD1/InZchgJZaeTqgoj2raTM932nwg7hiJuuyy164xPM+DsovBtUhYq3rR9gnkvuvGlk5+jbKMQKBgQDGz4aS9b1593FmEw9k8WgEfzPc6GirRs3VzGory7oc/UAPQps+AfNKauSXCj9mvDsS9UOZFNilGnS8OfDTZNYF4WWgWKX11Gbg8CNmOWaFep0mq4FIRPHSyMyCvrhXREkH5RYDN8kwjhsPjxeM4y0y8ffYKZIzPUxw2nfqQ2V5nQKBgAMZbTqwiJwh4zUbpQyWIi3Lw39QfhK2RCiRWT0SfS91OCVSJzkaRLxwlaqULvJQ+q4S2YjwGJwMbMzBggDFlAshyaJnhsAKzp5oVRq/UIU1oHMKHJJj4fjNGORBifbfHU2je2zkyKZkhY2VjsegFH1XOmIPdQYUcTKxcSiI9DJxAoGAcQ1DVkA9LaUCq+u8vkb0QlJfGIhkNLk/hQsHeL4RqDhue/8BLkKHqVKdiDIAmZgB86baeKs9Aq3Hod1Ez8CwX4mrc4HVZbQRiGmlh5SucsqloH64NnNWG+mpOdPgGWewK7geS1gmpOC6DJ/u4WOxqR2lNRt2koKR/D6PH/yRMJUCgYBunwYk920PZsO/EKrGyquUdsXqBeteOjOMQ1RjykayUlARqWtx+ZMSnlvhP/VfDzHB7OtMw5GK6N1L8t7LeNkmogwU3s2Z5ZtbFeiZa7aGIvHcydXV60vhRbwugD//Ug5ZYHiGKcIkJJ/7nY6kZBM468ulRt33U4pznA/SoQ6qrg==")
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey: PrivateKey = keyFactory.generatePrivate(keySpec)

        // Şifreli mesajı Base64 formatında byte dizisine çevirme
        val encryptedBytes = Base64.getDecoder().decode(encryptedMessage)

        // Cipher oluşturma ve şifre çözme işlemi
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        // Çözülen byte dizisini string'e çevirme
        val decryptedMessage = String(decryptedBytes)
        return decryptedMessage
    }

    private fun observeLiveData() {
        chatChannelViewModel.messageList.observe(this) {
            val newMessageList = it
            it.sortBy {
                it.createdAt
            }

            for (msg in it) {
                try {
                    if (msg.senderId == Firebase.auth.currentUser!!.phoneNumber) {
                        msg.messageTextForSender = decryptMessage(msg.messageTextForSender!!, senderPrivateKey)
                        println("Error!: burda - 1")
                    } else {
                        println("Error!: receiverPrivate: $receiverPrivateKey")
                        msg.messageTextForReceiver = decryptMessage(msg.messageTextForReceiver!!, senderPrivateKey)
                    }
                } catch (e: Exception) {
                    println("Error!: ${e.toString()}")
                }
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

        chatChannelViewModel.receiverPublicKey.observe(this) {
            receiverPublicKey = it
            println("receiverPublicKey: $receiverPublicKey")
        }

        chatChannelViewModel.receiverPrivateKey.observe(this) {
            receiverPrivateKey = it
            println("receiverPrivateKey: $receiverPrivateKey")
        }

        chatChannelViewModel.senderPublicKey.observe(this) {
            senderPublicKey = it
        }

        chatChannelViewModel.senderPrivateKey.observe(this) {
            senderPrivateKey = it
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