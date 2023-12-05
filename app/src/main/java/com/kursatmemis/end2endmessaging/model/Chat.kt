package com.kursatmemis.end2endmessaging.model

import com.google.firebase.Timestamp

data class Chat(
    val phoneNumber: String? = null,
    val createdAt: Timestamp? = null,
    val lastMessage: String? = null
)