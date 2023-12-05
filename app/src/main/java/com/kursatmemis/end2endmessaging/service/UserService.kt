package com.kursatmemis.end2endmessaging.service

import androidx.lifecycle.MutableLiveData
import com.kursatmemis.end2endmessaging.model.database_model.UserData
import com.kursatmemis.end2endmessaging.model.firebase_result.FirebaseOperationResult
import com.kursatmemis.end2endmessaging.util.FirestoreRefManager
import javax.inject.Inject

class UserService @Inject constructor(private val firebaseStoreRefManager: FirestoreRefManager) {

    private val _firebaseOperationResult = MutableLiveData<FirebaseOperationResult>()
    val firebaseOperationResult get() = _firebaseOperationResult

    fun addUserToFirestore(phoneNumber: String, userData: UserData) {
        val userRef = firebaseStoreRefManager.getUserRef(phoneNumber)
        userRef.set(userData)
            .addOnSuccessListener {
                _firebaseOperationResult.value = FirebaseOperationResult(true, null)
            }
            .addOnFailureListener {
                val errorMessage = it.message ?: "Unknown Error"
                _firebaseOperationResult.value = FirebaseOperationResult(true, errorMessage)
            }
    }

}