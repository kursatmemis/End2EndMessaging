package com.kursatmemis.end2endmessaging.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.kursatmemis.end2endmessaging.model.ChatInfo
import com.kursatmemis.end2endmessaging.model.Message
import com.kursatmemis.end2endmessaging.model.firebase_result.FirebaseOperationResult
import com.kursatmemis.end2endmessaging.util.ChatIdGenerator
import com.kursatmemis.end2endmessaging.util.FirestoreRefManager
import javax.inject.Inject

class MessageService @Inject constructor(
    private val firestoreRefManager: FirestoreRefManager,
    private val chatIdGenerator: ChatIdGenerator
) {

    private val _messageListResult = MutableLiveData<FirebaseOperationResult>()
    val messageListResult get() = _messageListResult


    private val _messageList = MutableLiveData<ArrayList<Message>>()
    private val messagesBetweenUsers = ArrayList<Message>()
    val messageList get() = _messageList


    fun saveMessageToFirestore(
        senderPhoneNumber: String,
        receiverPhoneNumber: String,
        message: Message
    ) {
        val messageRef = firestoreRefManager.getMessageRef(senderPhoneNumber, receiverPhoneNumber)
        messageRef.set(message).addOnSuccessListener {
            _messageListResult.value = FirebaseOperationResult(true, null)
        }

        val messageRefCollection =
            firestoreRefManager.getMessageCollectionRef(senderPhoneNumber, receiverPhoneNumber)



        messageRefCollection.parent?.get()?.addOnSuccessListener {
            val createdAt = it.get("createdAt") as Timestamp?
            if (createdAt == null) {
                // val lastMessage = message.messageText
                // val chatInfo = ChatInfo(Timestamp.now(), lastMessage)
                // messageRefCollection.parent?.set(chatInfo)
            } else {
                // val lastMessage = message.messageText
                // val chatInfo = ChatInfo(createdAt, lastMessage)
                // messageRefCollection.parent?.set(chatInfo)
            }
        }
    }

    fun getMessageListBetweenUsers(senderPhoneNumber: String, receiverPhoneNumber: String) {
        val senderMessagesRef =
            firestoreRefManager.getMessageCollectionRef(senderPhoneNumber, receiverPhoneNumber)
        val receiverMessagesRef =
            firestoreRefManager.getMessageCollectionRef(receiverPhoneNumber, senderPhoneNumber)

        senderMessagesRef.orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    val errorMessage = error.message ?: "Unknown Error!"
                    _messageListResult.value = FirebaseOperationResult(false, errorMessage)
                    return@addSnapshotListener
                }

                // Değişiklikleri işle
                for (documentChange in value?.documentChanges.orEmpty()) {
                    when (documentChange.type) {
                        DocumentChange.Type.ADDED -> {
                            // Yeni bir belge eklendi
                            val message = documentChange.document.toObject(Message::class.java)
                            messagesBetweenUsers.add(message)
                            _messageList.value = messagesBetweenUsers
                        }

                        DocumentChange.Type.MODIFIED -> {
                            // Bir belgede değişiklik yapıldı
                        }

                        DocumentChange.Type.REMOVED -> {
                            // Bir belge kaldırıldı
                        }
                    }
                }
            }
        receiverMessagesRef.orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    val errorMessage = error.message ?: "Unknown Error!"
                    _messageListResult.value = FirebaseOperationResult(false, errorMessage)
                    return@addSnapshotListener
                }

                // Değişiklikleri işle
                for (documentChange in value?.documentChanges.orEmpty()) {
                    when (documentChange.type) {
                        DocumentChange.Type.ADDED -> {
                            // Yeni bir belge eklendi
                            val message = documentChange.document.toObject(Message::class.java)
                            messagesBetweenUsers.add(message)
                            _messageList.value = messagesBetweenUsers
                            Log.w("mKm - deneme", "calisti be")
                        }

                        DocumentChange.Type.MODIFIED -> {
                            // Bir belgede değişiklik yapıldı
                        }

                        DocumentChange.Type.REMOVED -> {
                            // Bir belge kaldırıldı
                        }
                    }
                }
            }

    }

    fun clear() {
        messagesBetweenUsers.clear()
    }


}
