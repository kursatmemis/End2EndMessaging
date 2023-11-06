package com.kursatmemis.end2endmessaging.repository.authentication

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.kursatmemis.end2endmessaging.model.authentication.SignInResult
import javax.inject.Inject

class SignInWithPhoneNumberManager @Inject constructor(private val auth: FirebaseAuth) {

    private val _signInResult = MutableLiveData<SignInResult>()
    val signInResult get() = _signInResult

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInResult.value = SignInResult(true, null)
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown Error!"
                    _signInResult.value = SignInResult(false, errorMessage)
                }
            }
    }

    fun getPhoneAuthCredential(verificationId: String, enteredCode: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, enteredCode)
    }

}