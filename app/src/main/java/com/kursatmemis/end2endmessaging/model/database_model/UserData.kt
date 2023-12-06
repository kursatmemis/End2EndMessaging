package com.kursatmemis.end2endmessaging.model.database_model

import com.google.firebase.Timestamp
import org.bouncycastle.crypto.params.AsymmetricKeyParameter

data class UserData(
    val id: String? = null,
    val phoneNumber: String? = null,
    val createdAt: Timestamp? = null,
    val displayName: String? = null,
    val description: String? = null,
    val lastSeen: Timestamp? = null,
    val status: String? = null,
    val photoUrl: String? = null,
    val publicKey: String? = null,
    val token: String? = null,
    val privateKey: String? = null
)