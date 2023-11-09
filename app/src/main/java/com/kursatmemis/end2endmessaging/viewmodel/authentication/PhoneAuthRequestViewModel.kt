package com.kursatmemis.end2endmessaging.viewmodel.authentication

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthCredential
import com.kursatmemis.end2endmessaging.repository.firebase.PhoneVerificationRepository
import com.kursatmemis.end2endmessaging.repository.firebase.SignInWithPhoneNumberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhoneAuthRequestViewModel @Inject constructor(
    private val phoneVerificationRepository: PhoneVerificationRepository,
    private val signInWithPhoneNumberRepository: SignInWithPhoneNumberRepository
) : ViewModel() {

    val verificationResult = phoneVerificationRepository.verificationResult
    val signInResult = signInWithPhoneNumberRepository.signInResult

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity) {
        phoneVerificationRepository.verifyPhoneNumber(phoneNumber, activity)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        signInWithPhoneNumberRepository.signInWithPhoneAuthCredential(credential)
    }

}