package com.kursatmemis.end2endmessaging.viewmodel.authentication

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthCredential
import com.kursatmemis.end2endmessaging.repository.authentication.PhoneVerificationManager
import com.kursatmemis.end2endmessaging.repository.authentication.SignInWithPhoneNumberManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhoneAuthRequestViewModel @Inject constructor(
    private val phoneVerificationManager: PhoneVerificationManager,
    private val signInWithPhoneNumberManager: SignInWithPhoneNumberManager
) : ViewModel() {

    val verificationResult = phoneVerificationManager.verificationResult
    val signInResult = signInWithPhoneNumberManager.signInResult

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity) {
        phoneVerificationManager.verifyPhoneNumber(phoneNumber, activity)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        signInWithPhoneNumberManager.signInWithPhoneAuthCredential(credential)
    }

}