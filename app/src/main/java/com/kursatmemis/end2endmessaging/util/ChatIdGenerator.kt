package com.kursatmemis.end2endmessaging.util

import javax.inject.Inject

class ChatIdGenerator @Inject constructor() {
    // Telefon numaralarını sıralayarak birleştirip bir chatId return eder.
    fun generateChatId(senderPhoneNumber: String, receiverPhoneNumber: String): String {
        // Telefon numaralarını sıralayarak birleştir
        val sortedPhoneNumbers = listOf(senderPhoneNumber, receiverPhoneNumber).sorted()
        return sortedPhoneNumbers.joinToString(separator = "")
    }
}