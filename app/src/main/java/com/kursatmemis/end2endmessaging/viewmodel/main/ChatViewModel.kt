package com.kursatmemis.end2endmessaging.viewmodel.main

import androidx.lifecycle.ViewModel
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val firestoreRepository: FirebaseStoreRepository) :
    ViewModel() {

    val chatList = firestoreRepository.chatList

    fun getChatList() {
        firestoreRepository.getChatList()
    }

}