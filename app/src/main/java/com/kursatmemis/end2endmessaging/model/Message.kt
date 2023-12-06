package com.kursatmemis.end2endmessaging.model

import com.google.firebase.Timestamp

data class Message(
    var messageTextForReceiver: String? = null,
    var messageTextForSender: String? = null,
    val createdAt: Timestamp? = Timestamp.now(),
    val senderId: String? = null,
    val receiverId: String? = null,
    val isImage: String? = null,
    val isRead: String? = null,
    val sendNotification: Boolean? = null,
    val publicKey: String? = null
)
