package com.kursatmemis.end2endmessaging.model.authentication

import com.google.firebase.auth.PhoneAuthCredential

data class VerificationResult(
    val verificationId: String?,
    val errorMessage: String?,
    val isCompleted: Boolean,
    val credential: PhoneAuthCredential? = null
)

