package com.kursatmemis.end2endmessaging.viewmodel.authentication

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.kursatmemis.end2endmessaging.repository.firebase.PhoneVerificationRepository
import com.kursatmemis.end2endmessaging.repository.firebase.SignInWithPhoneNumberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CodeVerificationViewModel @Inject constructor(
    private val phoneVerificationRepository: PhoneVerificationRepository,
    private val signInWithPhoneNumberRepository: SignInWithPhoneNumberRepository
) : ViewModel() {

    val signInResult = signInWithPhoneNumberRepository.signInResult
    val verificationResult = phoneVerificationRepository.verificationResult

    fun signInWithPhoneAuthCredential(verificationId: String, enteredCode: String) {
        val credential = signInWithPhoneNumberRepository.getPhoneAuthCredential(
            verificationId,
            enteredCode
        )
        signInWithPhoneNumberRepository.signInWithPhoneAuthCredential(credential)
    }

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity) {
        phoneVerificationRepository.verifyPhoneNumber(phoneNumber, activity)
    }

}