package com.kursatmemis.end2endmessaging.viewmodel.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kursatmemis.end2endmessaging.model.Message
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStorageRepository
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatChannelViewModel @Inject constructor(
    private val firebaseStoreRepository: FirebaseStoreRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository
) : ViewModel() {

    val messageList = firebaseStoreRepository.messageList
    val messageServiceResult = firebaseStoreRepository.messageServiceResult
    var receiverPublicKey = MutableLiveData<String>()
    var receiverPrivateKey = MutableLiveData<String>()
    var senderPublicKey = MutableLiveData<String>()
    var senderPrivateKey = MutableLiveData<String>()

    fun saveMessageToFirestore(receiverPhoneNumber: String, message: Message) {
        firebaseStoreRepository.saveMessageToFirestore(receiverPhoneNumber, message)
    }

    fun getMessageList(receiverPhoneNumber: String) {
        firebaseStoreRepository.getMessageList(receiverPhoneNumber)
    }

    fun getReceiverPublicKey(receiverPhoneNumber: String) {
        val db = Firebase.firestore
        db.collection("users")
            .document(receiverPhoneNumber)
            .get()
            .addOnSuccessListener {
                receiverPublicKey.value = it.get("publicKey").toString()
            }
    }

    fun getReceiverPrivateKey(receiverPhoneNumber: String) {
        val db = Firebase.firestore
        db.collection("users")
            .document(receiverPhoneNumber)
            .get()
            .addOnCompleteListener {
                receiverPrivateKey.value = it.result.get("privateKey").toString()
                println("aliciPrivate: ${it.result.get("privateKey").toString()}")
            }
    }

    fun getSenderPublicKey(senderPhoneNumber: String) {
        val db = Firebase.firestore
        db.collection("users")
            .document(senderPhoneNumber)
            .get()
            .addOnCompleteListener {
                senderPublicKey.value = it.result.get("publicKey").toString()
            }
    }

    fun getSenderPrivateKey(senderPhoneNumber: String) {
        val db = Firebase.firestore
        db.collection("users")
            .document(senderPhoneNumber)
            .get()
            .addOnCompleteListener {
                senderPrivateKey.value = it.result.get("privateKey").toString()
            }
    }


}