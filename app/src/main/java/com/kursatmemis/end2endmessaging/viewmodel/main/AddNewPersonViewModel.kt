package com.kursatmemis.end2endmessaging.viewmodel.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.kursatmemis.end2endmessaging.model.database_model.Contact
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStorageRepository
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddNewPersonViewModel @Inject constructor(
    private val firebaseStoreRepository: FirebaseStoreRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository
) : ViewModel(){

    val saveContactResult = firebaseStoreRepository.contactListServiceResult

    fun addNewContactToContactList(contact: Contact) {
        firebaseStoreRepository.addNewContactToContactList(contact)
    }

    fun getProfilePictureUrl(receiverPhoneNumber: String, callback: (Uri?) -> Unit) {
        firebaseStorageRepository.getProfilePictureUri(receiverPhoneNumber, callback)
    }

}