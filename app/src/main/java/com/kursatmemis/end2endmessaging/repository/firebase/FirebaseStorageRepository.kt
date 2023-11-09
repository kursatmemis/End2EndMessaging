package com.kursatmemis.end2endmessaging.repository.firebase

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.kursatmemis.end2endmessaging.model.firebase_result.FirebaseOperationResult
import javax.inject.Inject

class FirebaseStorageRepository @Inject constructor(firebaseStorage: FirebaseStorage) {

    private val storageRef = firebaseStorage.reference
    private val PROFILE_PICTURES = "profile_pictures"

    private val _firebaseOperationResult = MutableLiveData<FirebaseOperationResult>()
    val firebaseOperationResult get() = _firebaseOperationResult

    fun saveProfilePictureToFirebaseStorage(imageUri: Uri?, phoneNumber: String) {
        var isUploadSuccessful: Boolean
        var errorMessage: String

        val profilePicturesRef = storageRef.child(PROFILE_PICTURES)
        val pictureRef = profilePicturesRef.child(phoneNumber)
        if (imageUri != null) {
            pictureRef.putFile(imageUri)
                .addOnSuccessListener {
                    isUploadSuccessful = true
                    _firebaseOperationResult.value = FirebaseOperationResult(isUploadSuccessful, null)
                }
                .addOnFailureListener {
                    isUploadSuccessful = false
                    errorMessage = it.message ?: "Unknown Error"
                    _firebaseOperationResult.value = FirebaseOperationResult(isUploadSuccessful, null)
                }
        } else {
            isUploadSuccessful = false
            errorMessage = "Image Uri is null."
            _firebaseOperationResult.value = FirebaseOperationResult(isUploadSuccessful, errorMessage)
        }
    }

    fun getProfilePictureUri(phoneNumber: String, callback: (Uri) -> Unit) {
        val profilePicturesRef = storageRef.child(PROFILE_PICTURES)
        val pictureRef = profilePicturesRef.child(phoneNumber)
        pictureRef.downloadUrl.addOnSuccessListener {
            callback(it)
        }
    }

}