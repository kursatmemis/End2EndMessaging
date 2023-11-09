package com.kursatmemis.end2endmessaging.viewmodel.main

import androidx.lifecycle.ViewModel
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactFragmentViewModel @Inject constructor(
    private val firebaseStoreRepository: FirebaseStoreRepository
) : ViewModel() {

    val contactList = firebaseStoreRepository.contactList

    fun getContactListFromFirebaseStore() {
        firebaseStoreRepository.getContactListFromFirebaseStore()
    }

}