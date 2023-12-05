package com.kursatmemis.end2endmessaging.model

import com.google.firebase.Timestamp

data class ChatInfo(
    val createdAt: Timestamp? = null,
    val lastMessage: String? = null
)
