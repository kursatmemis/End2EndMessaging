package com.kursatmemis.end2endmessaging.viewmodel.authentication

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.kursatmemis.end2endmessaging.model.database_model.UserData
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStorageRepository
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val firebaseStoreRepository: FirebaseStoreRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository
) : ViewModel() {

    val uploadImageResult = firebaseStorageRepository.firebaseOperationResult
    val registractionUserDataResult = firebaseStoreRepository.userServiceResult


    fun saveProfilePictureToFirebaseStorage(imageUri: Uri?, phoneNumber: String) {
        firebaseStorageRepository.saveProfilePictureToFirebaseStorage(imageUri, phoneNumber)
    }

    fun saveUserDataToFirebaseStore(userData: UserData) {
        firebaseStoreRepository.saveUserDataToFirebaseStore(userData)
    }

}