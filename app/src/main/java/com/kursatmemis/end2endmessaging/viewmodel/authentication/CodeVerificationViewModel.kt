package com.kursatmemis.end2endmessaging.viewmodel.authentication

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.kursatmemis.end2endmessaging.repository.authentication.PhoneVerificationManager
import com.kursatmemis.end2endmessaging.repository.authentication.SignInWithPhoneNumberManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CodeVerificationViewModel @Inject constructor(
    private val phoneVerificationManager: PhoneVerificationManager,
    private val signInWithPhoneNumberManager: SignInWithPhoneNumberManager
) : ViewModel() {

    val signInResult = signInWithPhoneNumberManager.signInResult
    val verificationResult = phoneVerificationManager.verificationResult

    fun signInWithPhoneAuthCredential(verificationId: String, enteredCode: String) {
        val credential = signInWithPhoneNumberManager.getPhoneAuthCredential(
            verificationId,
            enteredCode
        )
        signInWithPhoneNumberManager.signInWithPhoneAuthCredential(credential)
    }

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity) {
        phoneVerificationManager.verifyPhoneNumber(phoneNumber, activity)
    }

}