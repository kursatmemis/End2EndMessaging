package com.kursatmemis.end2endmessaging.viewmodel.main

import androidx.lifecycle.ViewModel
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

    fun saveMessageToFirestore(receiverPhoneNumber: String, message: Message) {
        firebaseStoreRepository.saveMessageToFirestore(receiverPhoneNumber, message)
    }

    fun getMessageList(receiverPhoneNumber: String) {
        firebaseStoreRepository.getMessageList(receiverPhoneNumber)
    }


}