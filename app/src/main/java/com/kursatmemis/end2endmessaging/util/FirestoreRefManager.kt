package com.kursatmemis.end2endmessaging.util

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreRefManager @Inject constructor(private val firestore: FirebaseFirestore) {
    fun getMessageRef(senderPhoneNumber: String, receiverPhoneNumber: String): DocumentReference {
        return firestore.collection("chats")
            .document(senderPhoneNumber)
            .collection("messageTo")
            .document(receiverPhoneNumber)
            .collection("messages")
            .document()
    }

    fun getMessageCollectionRef(senderPhoneNumber: String, receiverPhoneNumber: String) : CollectionReference {
        return firestore.collection("chats")
            .document(senderPhoneNumber)
            .collection("messageTo")
            .document(receiverPhoneNumber)
            .collection("messages")
    }

    // Parametrede belirtilen telefon numarasının rehberindeki kişilerin tutultuğu ref'i return eder.
    fun getContactListRef(phoneNumber: String): CollectionReference {
        return firestore.collection("contact_list")
            .document(phoneNumber)
            .collection("contacts")
    }

    fun getUserRef(phoneNumber: String): DocumentReference {
        return firestore.collection("users")
            .document(phoneNumber)
    }

    fun getChatRef(phoneNumber: String): CollectionReference {
        return firestore.collection("chats")
            .document(phoneNumber)
            .collection("messageTo")
    }

}