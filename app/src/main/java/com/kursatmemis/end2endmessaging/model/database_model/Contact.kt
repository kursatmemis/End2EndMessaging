package com.kursatmemis.end2endmessaging.model.database_model

// Rehbere eklenen kişi modeli.
data class Contact(
    val name: String? = null,
    val surname: String? = null,
    val phoneNumber: String? = null,
    val imageUri: String? = null
)
