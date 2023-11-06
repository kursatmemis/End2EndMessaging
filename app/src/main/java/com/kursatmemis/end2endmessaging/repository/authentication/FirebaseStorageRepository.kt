package com.kursatmemis.end2endmessaging.repository.authentication

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.kursatmemis.end2endmessaging.model.database.UploadImageResult
import javax.inject.Inject

class FirebaseStorageRepository @Inject constructor(private val firebaseStorage: FirebaseStorage) {

    private val storageRef = firebaseStorage.reference

    private val _uploadImageResult = MutableLiveData<UploadImageResult>()
    val uploadImageResult get() = _uploadImageResult

    fun saveProfilePictureToFirebaseStorage(imageUri: Uri?, phoneNumber: String) {
        var isUploadSuccessful: Boolean
        var errorMessage: String

        val profilePicturesRef = storageRef.child("profile_pictures")
        val pictureRef = profilePicturesRef.child(phoneNumber)
        if (imageUri != null) {
            pictureRef.putFile(imageUri)
                .addOnSuccessListener {
                    isUploadSuccessful = true
                    uploadImageResult.value = UploadImageResult(isUploadSuccessful, null)
                }
                .addOnFailureListener {
                    isUploadSuccessful = false
                    errorMessage = it.message ?: "Unknown Error"
                    uploadImageResult.value = UploadImageResult(isUploadSuccessful, null)
                }
        } else {
            isUploadSuccessful = false
            errorMessage = "Image Uri is null."
            uploadImageResult.value = UploadImageResult(isUploadSuccessful, errorMessage)
        }
    }

}