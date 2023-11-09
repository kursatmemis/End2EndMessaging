package com.kursatmemis.end2endmessaging.repository.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.kursatmemis.end2endmessaging.model.firebase_result.FirebaseOperationResult
import javax.inject.Inject

class SignInWithPhoneNumberRepository @Inject constructor(private val auth: FirebaseAuth) {

    private val _signInResult = MutableLiveData<FirebaseOperationResult>()
    val signInResult get() = _signInResult

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInResult.value = FirebaseOperationResult(true, null)
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown Error!"
                    _signInResult.value = FirebaseOperationResult(false, errorMessage)
                }
            }
    }

    fun getPhoneAuthCredential(verificationId: String, enteredCode: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, enteredCode)
    }

}