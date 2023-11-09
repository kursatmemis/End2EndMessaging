package com.kursatmemis.end2endmessaging.viewmodel.main

import androidx.lifecycle.ViewModel
import com.kursatmemis.end2endmessaging.model.database_model.Contact
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddNewPersonViewModel @Inject constructor(private val firebaseStoreRepository: FirebaseStoreRepository) : ViewModel(){

    val saveUserDataResult = firebaseStoreRepository.firebaseOperationResult

    fun addNewContactToContactList(contact: Contact, phoneNumber: String) {
        firebaseStoreRepository.saveNewPersonToFirebaseStore(contact, phoneNumber)
    }

}