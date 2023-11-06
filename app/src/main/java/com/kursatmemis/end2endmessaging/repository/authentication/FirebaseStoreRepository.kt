package com.kursatmemis.end2endmessaging.repository.authentication

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kursatmemis.end2endmessaging.model.database.UserData
import com.kursatmemis.end2endmessaging.model.database.SaveUserDataResult
import javax.inject.Inject

class FirebaseStoreRepository @Inject constructor(private val firestoreDatabase: FirebaseFirestore) {

    private val _saveUserDataResult = MutableLiveData<SaveUserDataResult>()
    val saveUserDataResult get() = _saveUserDataResult

    fun saveUserDataToFirebaseStore(userData: UserData) {
        firestoreDatabase.collection("users").document(userData.phoneNumber!!)
            .set(userData)
            .addOnSuccessListener {
                saveUserDataResult.value = SaveUserDataResult(true, null)
            }
            .addOnFailureListener {
                val errorMessage = it.message ?: "Unknown Error"
                saveUserDataResult.value = SaveUserDataResult(true, errorMessage)
            }
    }

}