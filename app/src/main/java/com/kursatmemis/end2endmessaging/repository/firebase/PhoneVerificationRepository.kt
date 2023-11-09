package com.kursatmemis.end2endmessaging.repository.firebase

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.kursatmemis.end2endmessaging.model.firebase_result.VerificationResult
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhoneVerificationRepository @Inject constructor(private val auth: FirebaseAuth) {

    private val _verificationResult = MutableLiveData<VerificationResult>()
    val verificationResult get() = _verificationResult

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Doğrulama işlemi gerçekleşti.
                    _verificationResult.value = VerificationResult(null, null, true, credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Doğrulama işleminde bir sıkıntı oldu.
                    _verificationResult.value = VerificationResult(null, e.message, false)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Doğrulama kodu belirtilen telefon numarasına gönderildi.
                    _verificationResult.value = VerificationResult(verificationId, null, false)
                }
            }) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


}